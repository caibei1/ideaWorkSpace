package com.fuzamei.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.OperationHistoryEnum;
import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.mapper.IdentityManagementMapper;
import com.fuzamei.mapper.RoleMapper;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.bo.RoleAuthBO;
import com.fuzamei.pojo.bo.UserRoleBO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.service.IdentityManagementService;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.ProtoBuf4SM2Util;

import fzmsupply.Api;

@Service
public class IdentityManagementServiceImpl implements IdentityManagementService {
	
	@Autowired
	private IdentityManagementMapper identityManagementMapper;
	
	@Autowired
	private BlockChainHistoryMapper blockChainHistoryMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityManagementServiceImpl.class);

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void assignRoles4Account(UserRoleBO userRoleBO) {
		long currentTime = System.currentTimeMillis();
		identityManagementMapper.deleteOriginalAssignment4Account(userRoleBO);//根据userId删除原始的分配关系
		if(userRoleBO.getRoleIds().size()>0){
			//如果角色信息不为空进行插入
			identityManagementMapper.insertNewAssignment4Account(userRoleBO);//分配新的账号角色对应关系
		}
		//【准备上链：修改后台业务人员】(分配账号角色信息)
		UserDetailDTO userDetailDTO = userRoleBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(userRoleBO.getUserId());
		List<String> stringRoleIds = new ArrayList<String>();//将int类型的roleId全部转化成string,然后一个个放到stringRoleIds集合
		for (Integer roleId : userRoleBO.getRoleIds()) {
			if(roleId==2){
				stringRoleIds.add("admin");//管理员角色id比较特殊
			}else{
				stringRoleIds.add(String.valueOf(roleId));
			}
		}
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.updateOfficial(operatorId,operatorPubKey, operatorPriKey, uid, stringRoleIds.toArray(new String[0]));
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.UPDATEOFFICIAL.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.UPDATEOFFICIAL.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void assignAuthes4Role(RoleAuthBO roleAuthBO) {
		long currentTime = System.currentTimeMillis();
		identityManagementMapper.deleteOriginalAssignment4Role(roleAuthBO);//根据roleId删除原始的分配关系
		List<Integer> authIds = roleAuthBO.getAuthIds();
		if(authIds.size()>0){
			//如果权限信息不为空进行插入
			identityManagementMapper.insertNewAssignment4Role(roleAuthBO);//分配新的角色权限对应关系
		}
		//【准备上区块链：修改后台角色】
		UserDetailDTO userDetailDTO = roleAuthBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String roleId = String.valueOf(roleAuthBO.getRoleId());
		String roleName = roleMapper.queryRoleNameByRoleId(roleAuthBO);
		/*
		 * 19------对应RsSetCredit = 2; //设置企业信用总额度
		 * 22------对应RsExamineAsset = 1; //审核资产录入
		 * 42------对应RsExamineDeposit = 4; //审核企业入金
		 * 48------对应RsExamineWithdraw = 8; //企业出金初审
		 * 52------对应RsReviewWithdraw = 16; //企业出金复审
		 */
		List<Api.RightType> rightTypeList = new ArrayList<Api.RightType>();
		for (Integer authId : authIds) {
			if(authId==19){
				rightTypeList.add(Api.RightType.RsSetCredit);
			}else if(authId==22){
				rightTypeList.add(Api.RightType.RsExamineAsset);
			}else if(authId==42){
				rightTypeList.add(Api.RightType.RsExamineDeposit);
			}else if(authId==48){
				rightTypeList.add(Api.RightType.RsExamineWithdraw);
			}else if(authId==52){
				rightTypeList.add(Api.RightType.RsReviewWithdraw);
			}
		}
		ProtoBufBean protoBufBean;
		protoBufBean = ProtoBuf4SM2Util.updateBackRole(operatorId,operatorPubKey, operatorPriKey, roleId, roleName, rightTypeList.toArray(new Api.RightType[0]));
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.UPDATEROLE.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.UPDATEROLE.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}
	
}
