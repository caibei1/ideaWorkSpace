package com.fuzamei.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintMSG;
import com.fuzamei.constant.OperationHistoryEnum;
import com.fuzamei.constant.Roles;
import com.fuzamei.mapper.AuthMapper;
import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.mapper.IdentityManagementMapper;
import com.fuzamei.mapper.RoleMapper;
import com.fuzamei.mapper.UserMapper;
import com.fuzamei.pojo.bo.AuthBO;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.bo.PwdBO;
import com.fuzamei.pojo.bo.UserBO;
import com.fuzamei.pojo.dto.AdminDTO;
import com.fuzamei.pojo.dto.BackRoleDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.BackRoleAuthPO;
import com.fuzamei.pojo.po.BackRolePO;
import com.fuzamei.pojo.po.BackTokenPO;
import com.fuzamei.pojo.po.BackUserPO;
import com.fuzamei.pojo.po.BackUserRolePO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.pojo.vo.AccountVO;
import com.fuzamei.pojo.vo.AdminVO;
import com.fuzamei.pojo.vo.UserRoleVO;
import com.fuzamei.service.UserService;
import com.fuzamei.util.AuthUrlToJsonUtil;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ProtoBuf4SM2Util;
import com.fuzamei.util.blockChain.sm2.SM2Utils;
import com.fuzamei.util.blockChain.sm2.Sm2KeyPair;
import com.fuzamei.util.blockChain.sm2.Util;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private AuthMapper authMapper;
	
	@Autowired
	private IdentityManagementMapper identityManagementMapper;
	
	@Autowired
	private BlockChainHistoryMapper blockChainHistoryMapper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private static final ReentrantLock LOCK = new ReentrantLock();
	
	@Override
	public boolean verificationToken(int userId, String token) {
		int count = userMapper.verificationToken(userId,token);
		if(count!=1) return false;
		return true;
	}

	@Override
	public UserDetailDTO queryUserByNameAndPwd(String username, String password) {
		return userMapper.queryUserByNameAndPwd(username,password);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void insertToken(Integer userId, String token) {
		long currentTime = System.currentTimeMillis();
		BackTokenPO backTokenPO = new BackTokenPO();
		backTokenPO.setUserId(userId);
		backTokenPO.setToken(token);
		backTokenPO.setCreateTime(currentTime);
		backTokenPO.setUpdateTime(currentTime);
		userMapper.insertToken(backTokenPO);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void updateToken(Integer userId, String token) {
		userMapper.updateToken(userId,token);
	}

	@Override
	public AdminDTO queryAdmin() {
		return userMapper.queryAdmin();
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public AdminVO createAdmin(String username, String password, String token) {//记住还要在关联表中插入管理员角色和用户id的数据
		try {
			LOCK.lock();
			long currentTime = System.currentTimeMillis();
			//创建超级管理员账号
			BackUserPO userPO = new BackUserPO();
			userPO.setUserId(1);
			userPO.setUsername(username);
			userPO.setPassword(password);
			userPO.setParentId(0);
			userPO.setPersonName("超级管理员");
			userPO.setCreateTime(currentTime);
			userPO.setUpdateTime(currentTime);
			userPO.setIsDelete(1);
			userPO.setPublicKey(ConfReadUtil.getProperty("superadminPubKeySM2"));
			userPO.setPrivateKey(ConfReadUtil.getProperty("superAdminPrivateKeySM2"));
			userMapper.createAdmin(userPO);//创建超级管理员
			//超级管理员token信息插入
			BackTokenPO backTokenPO = new BackTokenPO();
			backTokenPO.setUserId(1);//超级管理员id为1
			backTokenPO.setToken(token);
			backTokenPO.setCreateTime(currentTime);
			backTokenPO.setUpdateTime(currentTime);
			userMapper.insertToken(backTokenPO);//插入管理员的token
			//将超级管理员角色信息插入【角色表】中
			BackRolePO backRolePO = new BackRolePO();
			backRolePO.setRoleId(1);//超级管理员的角色id为1
			backRolePO.setRoleName(Roles.SUPER_ADMIN);
			backRolePO.setCreateTime(currentTime);
			backRolePO.setUpdateTime(currentTime);
			roleMapper.createAdminRole(backRolePO);
			//【插入用户角色关联表】
			BackUserRolePO backUserRolePO = new BackUserRolePO();
			backUserRolePO.setUserId(1);
			backUserRolePO.setRoleId(1);
			identityManagementMapper.createRole4Admin(backUserRolePO);
			//将所有权限信息插入权限表中
			AuthBO authBO = new AuthBO();
			authBO.setAuthArray(AuthEnum.values());//所有权限详细信息
			authBO.setCreateTime(currentTime);
			authBO.setUpdateTime(currentTime);
			authMapper.createAdminAuth(authBO);//本质创建所有权限
			//给超级管理员角色分配所有权限【插入角色权限关联表中】
			BackRoleAuthPO backRoleAuthPO = new BackRoleAuthPO();
			backRoleAuthPO.setRoleId(1);
			backRoleAuthPO.setAuthorityIds(AuthEnum.ALL_AUTHES_INDEX);
			identityManagementMapper.createAuth4Admin(backRoleAuthPO);
			
			//将普通管理员角色信息插入【角色表】中
			BackRolePO normalAdminBackRolePO = new BackRolePO();
			normalAdminBackRolePO.setRoleId(2);//普通管理员的角色id为2
			normalAdminBackRolePO.setRoleName(Roles.ADMIN);
			normalAdminBackRolePO.setCreateTime(currentTime);
			normalAdminBackRolePO.setUpdateTime(currentTime);
			roleMapper.createAdminRole(normalAdminBackRolePO);
			//给管理员角色分配所有权限【插入角色权限关联表中】
			BackRoleAuthPO normalAdminBackRoleAuthPO = new BackRoleAuthPO();
			normalAdminBackRoleAuthPO.setRoleId(2);
			List<Integer> adminAuthIdList = new ArrayList<Integer>();
			adminAuthIdList.addAll(AuthEnum.ADMIN_AUTH_INDEX);
			normalAdminBackRoleAuthPO.setAuthorityIds(adminAuthIdList);
			identityManagementMapper.createAuth4Admin(normalAdminBackRoleAuthPO);
			
			//将数据整合成VO返回给前端
			AdminVO adminVO = new AdminVO();
			adminVO.setUsername(username);
			adminVO.setUserId(1); 
			adminVO.setRoles(Arrays.asList(new String[]{Roles.SUPER_ADMIN}));
			adminVO.setAuthurl(AuthEnum.ALL_AUTHURL);
			adminVO.setAuthUrlJSON(AuthUrlToJsonUtil.authUrlToJson(AuthEnum.ALL_AUTHURL));
			adminVO.setIfFirstLogin(true);//true表示是初次登录
			adminVO.setTokenId("Bearer"+token+"&"+1);
			return adminVO;
		}finally{
			LOCK.unlock();//是否报异常，统统释放锁
		}
	}
	
	/**
	 * 通过用户id查看用户权限信息，同时对所要求的多个权限进行比对
	 * 如果andOrNot为true,表示后面的多个权限信息是以【且】的形式进行比对
	 * 如果andOrNot为false,表示后面的多个权限信息是以【或】的形式进行比对
	 */
	@Override
	public UserDetailDTO checkUserAuthority(Integer userId, boolean andOrNot, String... authorities) {
		UserDetailDTO userDetailDTO = userMapper.queryUserAuthority(userId);
		List<String> authes = userDetailDTO.getAuthorityUrl();
		if(andOrNot){
			//权限要全部满足
			for (String authority : authorities) {
				if(!authes.contains(authority)){
					throw new RuntimeException(HintMSG.NO_AUTH);
				}
			}
			return userDetailDTO;
		}else{
			//权限只要有一个满足即可
			for (String authority : authorities) {
				if(authes.contains(authority)){
					return userDetailDTO;//有权限就通过
				}
			}
			throw new RuntimeException(HintMSG.NO_AUTH);
		}
	}

	/**
	 * 通过用户id查看用户角色信息，同时对所要求的多个角色进行比对
	 * 如果andOrNot为true,表示后面的多个角色信息是以【且】的形式进行比对
	 * 如果andOrNot为false,表示后面的多个角色信息是以【或】的形式进行比对
	 */
	@Override
	public void checkUserRole(Integer userId, boolean andOrNot, String... roleNames) {
		UserDetailDTO userDetailDTO = userMapper.queryUserAuthority(userId);
		List<String> roles = userDetailDTO.getRoleName();
		if(andOrNot){
			//角色信息要全部满足
			for (String role : roleNames) {
				if(!roles.contains(role)){
					throw new RuntimeException(HintMSG.NO_AUTH);
				}
			}
			return;
		}else{
			//角色信息只要有一个满足即可
			for (String role : roleNames) {
				if(roles.contains(role)){
					return;//有权限就通过
				}
			}
			throw new RuntimeException(HintMSG.NO_AUTH);
		}
	}

	@Override
	public int queryUserByName(String username) {
		return userMapper.queryUserByName(username);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void createAccount(UserBO userBO) {
		Long currentTime = System.currentTimeMillis();
		Sm2KeyPair sm2KeyPair;
		String privateKey;//该账户的SM2私钥
		String publicKey;//该账户的SM2公钥
		byte[] sign;
		String sourceText="1";
		do {//确保验签能通过
			sm2KeyPair=SM2Utils.generateKeyPair();
			privateKey = Util.getHexString(sm2KeyPair.getPriKey());
			publicKey = Util.getHexString(sm2KeyPair.getPubKey());
			sign = SM2Utils.sign(sm2KeyPair.getPriKey(), sourceText.getBytes());
		} while (!SM2Utils.verifySign(sm2KeyPair.getPubKey(), sourceText.getBytes(), sign));
		userBO.setPrivateKey(privateKey);//SM2的私钥
		userBO.setPublicKey(publicKey);//SM2的公钥
		userBO.setCreateTime(currentTime);
		userBO.setUpdateTime(currentTime);
		userBO.setIsDelete(1);
		userMapper.createAccount(userBO);//此时的userBO的主键id已经返回
		//【准备上区块链：创建后台人员】
		UserDetailDTO userDetailDTO = userBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(userBO.getUserId());
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.createOfficial(operatorId,operatorPubKey, operatorPriKey, uid, publicKey, userBO.getPersonName(), userBO.getIdCardNum());
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.CREATEOFFICIAL.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.CREATEOFFICIAL.getType());
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
	public void updateAccount(UserBO userBO) {
		Long currentTime = System.currentTimeMillis();
		userBO.setUpdateTime(currentTime);
		userMapper.updateAccount(userBO);
	}

	@Override
	public PageDTO queryAccountInfo(UserBO userBO) {
		UserDetailDTO userDetailDTO = userBO.getUserDetailDTO();
		List<String> roleName = userDetailDTO.getRoleName();
		if(roleName.contains(Roles.SUPER_ADMIN)){//如果是超级管理员，显示除自己以外的账户信息
			List<AccountVO> accountList = userMapper.queryAccountInfoBySuperAdmin(userBO);
			int count = userMapper.queryAccountInfoCountBySuperAdmin(userBO);
			return PageDTO.getPagination(count, accountList);
		}
		if(roleName.contains(Roles.ADMIN)){//如果是普通管理员，显示除管理员们和超级管理员以外的账户信息
			List<Integer> adminIds = userMapper.queryAllAdminUserIds();//查询所有管理员的账号id值，让所有管理员看不到其他管理员的信息
			userBO.setAdminIds(adminIds);//查询的时候排除所有的管理员和超级管理员的id
			List<AccountVO> accountList = userMapper.queryAccountInfoByAdmin(userBO);
			int count = userMapper.queryAccountInfoCountByAdmin(userBO);
			return PageDTO.getPagination(count, accountList);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void deleteAccount(UserBO userBO) {
		long currentTime = System.currentTimeMillis();
		userMapper.deleteAccount(userBO);
		//【准备上链：删除后台人员】
		UserDetailDTO userDetailDTO = userBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		List<String> stringUids = new ArrayList<String>();//将int类型的userId全部转化成string,然后一个个放到stringRoleIds集合
		for (Integer uid : userBO.getUserIds()) {
			stringUids.add(String.valueOf(uid));
		}
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.deleteOfficial(operatorId, operatorPubKey, operatorPriKey, stringUids.toArray(new String[0]));//操作人只有超管
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.DELETEOFFICIAL.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.DELETEOFFICIAL.getType());
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
	public UserRoleVO showRoleInfoUnderAccount(UserBO userBO) {
		List<BackRoleDTO> backRoleList;
		if(userBO.getOperatorId()==1){//超级管理员能看到的角色信息
			backRoleList = userMapper.queryAllRoles();//显示除自己以外的所有角色信息
		}else {//普通管理员不显示超级管理员和管理员角色信息
			backRoleList = userMapper.queryAllRolesByAdmin();//显示除超级管理员和管理员的角色信息
		}
		List<Integer> roleIdList = userMapper.showRoleInfoUnderAccount(userBO);
		for (BackRoleDTO backRoleDTO : backRoleList) {
			if(roleIdList.contains(backRoleDTO.getRoleId())){
				backRoleDTO.setIsSelected(1);//选中的为1
			}else{
				backRoleDTO.setIsSelected(0);//没选中的为0
			}
		}
		UserRoleVO userRoleVO = new UserRoleVO();//封装成VO返回给前端显示
		userRoleVO.setUserId(userBO.getUserId());
		userRoleVO.setRoleList(backRoleList);
		return userRoleVO;
	}

	@Override
	public int queryUserByNameAndId(UserBO userBO) {
		return userMapper.queryUserByNameAndId(userBO);
	}

	@Override
	public UserDetailDTO queryUserById(UserBO userBO) {
		return userMapper.queryUserById(userBO);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void modifyPassword(PwdBO pwdBO) {
		Long currentTime = System.currentTimeMillis();
		pwdBO.setUpdateTime(currentTime);
		userMapper.modifyPassword(pwdBO);
	}

	@Override
	public int queryUserByNameAndPwdOfDelete(UserBO userBO) {
		return userMapper.queryUserByNameAndPwdOfDelete(userBO);
	}

	@Override
	public List<Integer> queryAllAdminUserIds() {
		return userMapper.queryAllAdminUserIds();
	}
	
	
	

	
}
