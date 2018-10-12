package com.fuzamei.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.OperationHistoryEnum;
import com.fuzamei.constant.Roles;
import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.mapper.IdentityManagementMapper;
import com.fuzamei.mapper.RoleMapper;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.bo.RoleBO;
import com.fuzamei.pojo.bo.UserRoleBO;
import com.fuzamei.pojo.dto.BackAuthDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.BackRoleAuthPO;
import com.fuzamei.pojo.po.BackRolePO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.pojo.vo.RoleAuthVO;
import com.fuzamei.pojo.vo.RoleVO;
import com.fuzamei.service.RoleService;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ProtoBuf4SM2Util;
@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private IdentityManagementMapper identityManagementMapper;
	
	@Autowired
	private BlockChainHistoryMapper blockChainHistoryMapper;

	@Override
	public int queryRoleByName(String roleName) {
		return roleMapper.queryRoleByName(roleName);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void createRole(RoleBO roleBO) {
		Long currentTime = System.currentTimeMillis();
		roleBO.setCreateTime(currentTime);
		roleBO.setUpdateTime(currentTime);
		ProtoBufBean protoBufBean;//定义好ProtoBufBean
		if(roleBO.getRoleName().equals(Roles.ADMIN)){//只有名字匹配上【管理员】的才会分配管理员权限
			//将普通管理员角色信息插入【角色表】中
			BackRolePO backRolePO = new BackRolePO();
			backRolePO.setRoleId(2);//普通管理员的角色id为2
			backRolePO.setRoleName(Roles.ADMIN);
			backRolePO.setCreateTime(currentTime);
			backRolePO.setUpdateTime(currentTime);
			roleMapper.createAdminRole(backRolePO);
			//给管理员角色分配所有权限【插入角色权限关联表中】
			BackRoleAuthPO backRoleAuthPO = new BackRoleAuthPO();
			backRoleAuthPO.setRoleId(2);//固定死roleId是2
			backRoleAuthPO.setAuthorityIds(AuthEnum.ADMIN_AUTH_INDEX);
			identityManagementMapper.createAuth4Admin(backRoleAuthPO);
			//管理员区块链已经预设好，不上区块链
//			//【准备上区块链：创建后台角色】(管理员角色的上链)
//			String operatorPubKey = userDetailDTO.getPublicKey();
//			String operatorPriKey = userDetailDTO.getPrivateKey();
//			protoBufBean = ProtoBuf4SM2Util.createBackRole(operatorPubKey, operatorPriKey, "admin", Roles.ADMIN);//只有管理员在创建的时候直接上链
		}else{
			roleMapper.createRole(roleBO);//插入时roleId已经返回
			//【准备上区块链：创建后台角色】(普通业务员角色的上链)
			UserDetailDTO userDetailDTO = roleBO.getUserDetailDTO();//定义好用户详细信息
			String operatorId = String.valueOf(userDetailDTO.getUserId());
			String operatorPubKey = userDetailDTO.getPublicKey();
			String operatorPriKey = userDetailDTO.getPrivateKey();
			String roleId = String.valueOf(roleBO.getRoleId());
			protoBufBean = ProtoBuf4SM2Util.createBackRole(operatorId,operatorPubKey, operatorPriKey, roleId, roleBO.getRoleName());//只有管理员在创建的时候直接上链
			//【插入操作记录表】
			BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
			blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
			blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.CREATEROLE.getTypeId());
			blockChainHistoryBO.setOperationType(OperationHistoryEnum.CREATEROLE.getType());
			blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
			blockChainHistoryBO.setCreateTime(currentTime);
			blockChainHistoryMapper.createHistory(blockChainHistoryBO);//存入操作记录
			//发送区块链
			String result = BlockChainUtil.sendPost(protoBufBean);
			boolean checkResult = BlockChainUtil.checkResult(result);
			if(!checkResult){
				throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
			}
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void updateRole(RoleBO roleBO) {
		Long currentTime = System.currentTimeMillis();
		roleBO.setUpdateTime(currentTime);
		roleMapper.updateRole(roleBO);
		
	}

	@Override
	public PageDTO queryRoleInfo(RoleBO roleBO) {
		if(roleBO.getOperatorId()==1){//给超级管理员显示的
			List<RoleVO> list = roleMapper.queryRoleInfo(roleBO);
			int count = roleMapper.queryRoleInfoCount(roleBO);
			return PageDTO.getPagination(count, list);
		} else {//给普通管理员显示的
			List<RoleVO> list = roleMapper.queryRoleInfoByAdmin(roleBO);
			int count = roleMapper.queryRoleInfoCountByAdmin(roleBO);
			return PageDTO.getPagination(count, list);
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void deleteRole(RoleBO roleBO) {
		long currentTime = System.currentTimeMillis();
		roleMapper.deleteRole(roleBO);
		//删除用户角色关联表中角色信息
		identityManagementMapper.deleteRoleFromUserRole(roleBO);
		//删除角色权限关联表中角色信息
		identityManagementMapper.deleteRoleFromRoleAuth(roleBO);
		//【准备上链：删除后台角色】
		UserDetailDTO userDetailDTO = roleBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		List<String> stringRoleIds = new ArrayList<String>();//将int类型的roleId全部转化成string,然后一个个放到stringRoleIds集合
		for (Integer roleId : roleBO.getRoleIds()) {
			stringRoleIds.add(String.valueOf(roleId));
		}
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.deleteBackRole(operatorId,operatorPubKey, operatorPriKey, stringRoleIds.toArray(new String[0]));//操作人只有超管
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.DELETEROLE.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.DELETEROLE.getType());
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);//存入操作记录
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}

	@Override
	public RoleAuthVO showAuthInfoUnderRole(RoleBO roleBO) {
		roleBO.setAuthIdThreshHold(AuthEnum.SUPER_ADMIN_AUTH_INDEX.size());//设置管理员权限的id阈值
		if(roleBO.getRoleId()!=2){//除了管理员的就直接显示所有业务权限
			List<BackAuthDTO> backAuthList = roleMapper.queryAllAuthes(roleBO);
			List<Integer> authIdList = roleMapper.showAuthInfoUnderRole(roleBO);
			for (BackAuthDTO backAuthDTO : backAuthList) {
				if(authIdList.contains(backAuthDTO.getAuthorityId())){
					backAuthDTO.setIsSelected(1);//选中的为1
				}else{
					backAuthDTO.setIsSelected(0);//未选中的为0
				}
			}
			RoleAuthVO roleAuthVO = new RoleAuthVO();
			roleAuthVO.setRoleId(roleBO.getRoleId());
			roleAuthVO.setAuthList(backAuthList);
			return roleAuthVO;
		}else{
			return new RoleAuthVO();//管理员的不显示业务权限信息
		}
	}

	@Override
	public int queryRoleByNameAndId(RoleBO roleBO) {
		return roleMapper.queryRoleByNameAndId(roleBO);
	}

	@Override
	public UserDetailDTO queryAdminIfoName(String admin) {
		return roleMapper.queryAdminIfoName(admin);
	}

}
