package com.fuzamei.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.pojo.bo.AuthBO;
import com.fuzamei.pojo.bo.RoleAuthBO;
import com.fuzamei.pojo.bo.RoleBO;
import com.fuzamei.pojo.bo.UserBO;
import com.fuzamei.pojo.bo.UserRoleBO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.vo.RoleAuthVO;
import com.fuzamei.pojo.vo.UserRoleVO;
import com.fuzamei.service.AuthService;
import com.fuzamei.service.IdentityManagementService;
import com.fuzamei.service.RoleService;
import com.fuzamei.service.UserService;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController
@RequestMapping(value="/identityManagement")
public class IdentityManagementAction {
	
	@Autowired
	private HttpServletRequest req;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private IdentityManagementService identityManagementService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityManagementAction.class);
	
	/**
	 * @Title queryAccountInfo
	 * @Description: TODO(查询账户信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"page":1,
	 	"rowNum":10,
	 	"username":""
	 }
	 */
	@RequestMapping(value="/account/queryAccountInfo",method = RequestMethod.POST)
	protected ResultResp queryAccountInfo(@RequestBody UserBO userBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		UserDetailDTO userDetailDTO;
		LOGGER.info("用户:{}--->进行查询账户信息",userId);
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.QUERY_ACCOUNTS.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(userBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(userBO.getRowNum(), 1);
			userBO.setStartPage((page-1)*rowNum);
			//插入当前用户id主要用于区分超级管理员和管理员之间看到的用户信息
			//超级管理员能看到所有管理员的信息，但看不到自己的信息
			//管理员不能看到自己和管理员的信息
			userBO.setUserDetailDTO(userDetailDTO);
			userBO.setUserId(userId);//设定当前用户id,排除查询到当前用户信息
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = userService.queryAccountInfo(userBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}

	/**
	 * @Title updateAccountInfo
	 * @Description: TODO(修改账户信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"userId":"",
	 	"username":"",
	 	"password":"",
	 }
	 */
	@RequestMapping(value="/account/updateAccountInfo",method = RequestMethod.POST)
	protected ResultResp updateAccountInfo(@RequestBody UserBO userBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->进行账户信息更新操作",userId);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.UPDATE_ACCOUNT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkAndAssignInt(userBO.getUserId());//校验用户id不能能null
			ValidationUtil.checkBlankString(userBO.getUsername(), RegexConstant.USERNAME_REGEX);//校验用户名
			ValidationUtil.checkBlankString(userBO.getPassword(), RegexConstant.PWD_REGEX);//校验密码
			if(userBO.getUserId()==1){
				throw new RuntimeException("严禁对超级管理员进行修改");
			}
			if(userId!=1){//这个可以保证普通管理员不会修改自己和同级管理员的信息
				List<Integer> adminIds = userService.queryAllAdminUserIds();
				if(adminIds.contains(userBO.getUserId())){
					throw new RuntimeException("无法修改管理员的账户信息");
				}
			}
			int count = userService.queryUserByNameAndId(userBO);//查看未被删除的用户名是否存在
			if(count!=0){
				throw new RuntimeException("该用户名已经存在");
			}
			UserDetailDTO userDetailDTO = userService.queryUserById(userBO);//查看当前id的用户是否为被删除用户
			if(userDetailDTO.getIsDelete()==0){
				throw new RuntimeException("该用户已被删除");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			userService.updateAccount(userBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->修改用户信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title createNewAccount
	 * @Description: TODO(添加用户信息,创建账户)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"username":"",
	 	"password":"",
	 	"idCardNum":"",
	 	"confirmedPersonName":"",
	 	"personName":"",
	 	"confirmedPersonName":""
	 }
	 */
	@RequestMapping(value="/account/createNewAccount",method = RequestMethod.POST)
	protected ResultResp createNewAccount(@RequestBody UserBO userBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->创建账户操作",userId);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.CREATE_ACCOUNT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankString(userBO.getUsername(), RegexConstant.USERNAME_REGEX);//校验用户名
			ValidationUtil.checkBlankString(userBO.getPassword(), RegexConstant.PWD_REGEX);//校验密码
			ValidationUtil.checkBlankString(userBO.getIdCardNum(), RegexConstant.ID_CARD);//校验身份证号
			ValidationUtil.checkBlankString(userBO.getPersonName());//校验人名
			if(!userBO.getIdCardNum().equals(userBO.getConfirmedIdCardNum())){
				throw new RuntimeException("用户名两次输入不一致");
			}
			if(!userBO.getPersonName().equals(userBO.getConfirmedPersonName())){
				throw new RuntimeException("人名两次输入不一致");
			}
			
			int count = userService.queryUserByName(userBO.getUsername());
			if(count!=0){
				throw new RuntimeException("该用户名已经存在");
			}
			int countOfDelete = userService.queryUserByNameAndPwdOfDelete(userBO);
			if(countOfDelete!=0){
				throw new RuntimeException("该账号密码可能存在较大安全隐患，请重新创建");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			userBO.setUserDetailDTO(userDetailDTO);//将操作者详细信息带入service
			userService.createAccount(userBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->创建用户失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title deleteAccount
	 * @Description: TODO(通过userIds批量删除用户：【逻辑删除】)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"userIds":[1,2,3,4]
	 }
	 */
	@RequestMapping(value="/account/deleteAccount",method = RequestMethod.POST)
	protected ResultResp deleteAccount(@RequestBody UserBO userBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->进行了删除账户的操作",userId);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.DELETE_ACCOUNTS.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			List<Integer> userIds = userBO.getUserIds();//要被删除的id号
			if(userIds==null||userIds.size()==0){
				throw new RuntimeException("请选择要删除的用户");
			}
			if(userIds.contains(1)){//不能有超级管理员的id号
				throw new RuntimeException("严禁删除超级管理员");
			}
			if(userId!=1){//保证管理员不会删除同类伙伴
				List<Integer> adminIds = userService.queryAllAdminUserIds();
				for (Integer uid : userIds) {
					if(adminIds.contains(uid)){//不能删除自己和同级管理员
						throw new RuntimeException("严禁删除管理员");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			userBO.setUserDetailDTO(userDetailDTO);
			userService.deleteAccount(userBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->删除用户失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title showRoleInfoUnderAccount
	 * @Description: TODO(通过userId查询该用户下的角色信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"userId":""
	 }
	 */
	@RequestMapping(value="/account/showRoleInfoUnderAccount",method = RequestMethod.POST)
	protected ResultResp showRoleInfoUnderAccount(@RequestBody UserBO userBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->进行查看了用户下的角色信息",userId);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.SHOW_ROLES.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			if(userBO.getUserId()==null){
				throw new RuntimeException("用户id不能为空");
			}
			//将当前操作人的id号放入，如果是超级管理员就显示除超级管理员的全部角色信息
			//如果不是超级管理员，必然是普通管理员，就显示除超级管理员和普通管理员的角色信息
			userBO.setOperatorId(userId);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			//显示除超级管理员的全部角色信息======================>>TODO
			UserRoleVO userRoleVO = userService.showRoleInfoUnderAccount(userBO);
			return ResultResp.getResult(HintEnum.CHECK_SUCCESS.getCode(), true, HintEnum.CHECK_SUCCESS.getHintMsg(), userRoleVO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->查询用户角色信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.CHECK_FAIL.getCode(), false, HintEnum.CHECK_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title assignRoles4Account
	 * @Description: TODO(为单个账号分配角色)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"userId":"",
	 	"roleIds":[1,2,3]
	 }
	 */
	@RequestMapping(value="/account/assignRoles4Account",method = RequestMethod.POST)
	protected ResultResp assignRoles4Account(@RequestBody UserRoleBO userRoleBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->为账户进行了分配角色的操作",userId);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.ASSIGN_ROLES.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		Integer beModifieduserId = userRoleBO.getUserId();//被修改的用户id
		List<Integer> roleIds = userRoleBO.getRoleIds();
		try {
			ValidationUtil.checkNullInteger(beModifieduserId);//校验被修改的用户id
			if(beModifieduserId==1){
				throw new RuntimeException("不能为超级管理员分配角色信息");
			}
			if(roleIds.contains(1)){//不能分配超级管理员的roleId
				throw new RuntimeException("严禁分配超级管理员角色");
			}
			if(userId!=1){
				if(roleIds.contains(2)){
					throw new RuntimeCryptoException("管理员无权分配管理员角色");
				}
				List<Integer> adminIds = userService.queryAllAdminUserIds();
				if(adminIds.contains(beModifieduserId)){
					throw new RuntimeException("管理员无权给管理员分配角色");
				}
			}
			/*UserDetailDTO userDetailDTO = roleService.queryAdminIfoName(Roles.ADMIN);//查询管理员的角色id和账号id(假设只有一个普通管理员)
			//如果管理员不存在，那当前操作人必然是超级管理员
			//如果管理员存在，该条件同样适用于管理员操作的判断
			if(userDetailDTO!=null && userDetailDTO.getUserId()!=null && userDetailDTO.getRoleId()!=null && roleIds.contains(userDetailDTO.getRoleId())){
				throw new RuntimeException("管理员已经存在，不能再分配管理员的角色信息");
			}*/
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			userRoleBO.setUserDetailDTO(userDetailDTO);
			identityManagementService.assignRoles4Account(userRoleBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情：{}-->账号分配角色信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title queryRoleInfo
	 * @Description: TODO(查询角色信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"page":1,
	 	"rowNum":10,
	 	"roleName":""
	 }
	 */
	@RequestMapping(value="/role/queryRoleInfo",method = RequestMethod.POST)
	protected ResultResp queryRoleInfo(@RequestBody RoleBO roleBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->进行查询角色信息",userId);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_ROLES.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(roleBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(roleBO.getRowNum(), 1);
			roleBO.setStartPage((page-1)*rowNum);
			//将当前操作人的userId号注入参数中，如果是超级管理员可以看到管理员等人的角色信息
			//如果当前操作人的userId号不是超级管理员，则必定是管理员，那就显示除管理员以外的所有角色信息
			roleBO.setOperatorId(userId);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = roleService.queryRoleInfo(roleBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title createNewRole
	 * @Description: TODO(添加新角色)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"roleName":""
	 }
	 */
	@RequestMapping(value="/role/createNewRole",method = RequestMethod.POST)
	public ResultResp createNewRole(@RequestBody RoleBO roleBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->创建了角色:{}",userId,roleBO.getRoleName());
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.CREATE_ROLE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankString(roleBO.getRoleName(), RegexConstant.NAME_REGEX);//校验角色名
			int count = roleService.queryRoleByName(roleBO.getRoleName());
			if(count!=0){
				throw new RuntimeException("该角色名已经存在");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			roleBO.setUserDetailDTO(userDetailDTO);//只有创建admin时候上链，其他角色不上链
			roleService.createRole(roleBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->创建角色失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), true, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title updateRoleInfo
	 * @Description: TODO(更新角色信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"roleId":"",
	 	"roleName":""
	 }
	 */
	@RequestMapping(value="/role/updateRoleInfo",method = RequestMethod.POST)
	public ResultResp updateRoleInfo(@RequestBody RoleBO roleBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.UPDATE_ROLE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankString(roleBO.getRoleName(), RegexConstant.NAME_REGEX);//校验角色名
			if(roleBO.getRoleId()==1){
				throw new RuntimeException("严禁修改超级管理员的角色信息");
			}
			if(roleBO.getRoleId()==2){
				throw new RuntimeException("严禁修改普通管理员的角色信息");
			}
			int count = roleService.queryRoleByNameAndId(roleBO);
			if(count!=0){
				throw new RuntimeException("该角色名已经存在");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			roleService.updateRole(roleBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->更新角色信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), true, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title deleteRole
	 * @Description: TODO(通过roleIds批量删除角色：【物理删除】)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"roleIds":[1,2,3,4]
	 }
	 */
	@RequestMapping(value = "/role/deleteRole",method = RequestMethod.POST)
	public ResultResp deleteRole(@RequestBody RoleBO roleBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.DELETE_ROLE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			List<Integer> roleIds = roleBO.getRoleIds();
			if(roleIds.contains(1)){
				throw new RuntimeException("严禁删除超级管理员角色");
			}
			if(roleIds.contains(2)){
				throw new RuntimeException("严禁删除普通管理员角色");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			roleBO.setUserDetailDTO(userDetailDTO);//操作人信息带入service层处理
			roleService.deleteRole(roleBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->删除角色信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title showAuthInfoUnderRole
	 * @Description: TODO(通过roleId查询该角色下的权限信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"roleId":""
	 }
	 */
	@RequestMapping(value="/role/showAuthInfoUnderRole",method = RequestMethod.POST)
	protected ResultResp showAuthInfoUnderRole(@RequestBody RoleBO roleBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.SHOW_AUTHES.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			Integer roleId = roleBO.getRoleId();
			if(roleId==null){
				throw new RuntimeException("角色id不能为空");
			}
			if(roleId==1){
				throw new RuntimeException("严禁查看超级管理员的权限信息");
			}
			if(userId!=1 && roleId==2){//普通管理员不能查看自己角色权限信息
				throw new RuntimeException("严禁查看管理员的权限信息");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			RoleAuthVO roleAuthVO = roleService.showAuthInfoUnderRole(roleBO);
			return ResultResp.getResult(HintEnum.CHECK_SUCCESS.getCode(), true, HintEnum.CHECK_SUCCESS.getHintMsg(), roleAuthVO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->查询角色权限信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.CHECK_FAIL.getCode(), false, HintEnum.CHECK_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title assignAuthes4Role
	 * @Description: TODO(为单个角色分配权限)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"roleId":"",
	 	"authIds":[1,2,3]
	 }
	 */
	@RequestMapping(value="/role/assignAuthes4Role",method = RequestMethod.POST)
	protected ResultResp assignAuthes4Role(@RequestBody RoleAuthBO roleAuthBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.ASSIGN_AUTHES.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			Integer roleId = roleAuthBO.getRoleId();
			if(roleId==null){
				throw new RuntimeException("角色id不能为空");
			}
			if(roleId==1){
				throw new RuntimeException("严禁修改超级管理员角色的权限信息");
			}
			if(roleId==2){
				throw new RuntimeException("严禁修改管理员角色的权限信息");
			}
			for (Integer authId : roleAuthBO.getAuthIds()) {
				if(AuthEnum.SUPER_ADMIN_AUTH_INDEX.contains(authId)){
					throw new RuntimeException("严禁分配管理员级别的权限信息");
				}
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			roleAuthBO.setUserDetailDTO(userDetailDTO);
			identityManagementService.assignAuthes4Role(roleAuthBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->角色分配权限信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title queryAuthInfo
	 * @Description: TODO(查询权限信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"page":1,
	 	"rowNum":10,
	 	"authName":""
	 }
	 */
	@RequestMapping(value="/auth/queryAuthInfo",method = RequestMethod.POST)
	protected ResultResp queryAuthInfo(@RequestBody AuthBO authBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_AUTHES.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(authBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(authBO.getRowNum(), 1);
			authBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = authService.queryAuthInfo(authBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title createNewAuth
	 * @Description: TODO(添加新权限)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"authName":"",
	 	"authUrl":""
	 }
	 */
	@RequestMapping(value="/auth/createNewAuth",method = RequestMethod.POST)
	public ResultResp createNewAuth(@RequestBody AuthBO authBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.CREATE_AUTH.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankString(authBO.getAuthName(), RegexConstant.NAME_REGEX);//校验权限名
			ValidationUtil.checkBlankString(authBO.getAuthUrl(),RegexConstant.AUTH_NAME_REGEX);//校验权限url
			int count = authService.queryAuthByName(authBO.getAuthName());
			if(count!=0){
				throw new RuntimeException("该权限名或权限url已经存在");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			authService.createAuth(authBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->创建权限失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	/**
	 * @Title updateAuthInfo
	 * @Description: TODO(更新权限信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"authId":"",
	 	"authName":""
	 }
	 */
	@RequestMapping(value="/auth/updateAuthInfo",method = RequestMethod.POST)
	public ResultResp updateAuthInfo(@RequestBody AuthBO authBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.UPDATE_AUTH.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkAndAssignInt(authBO.getAuthId());
			ValidationUtil.checkBlankString(authBO.getAuthName(), RegexConstant.NAME_REGEX);//校验权限名
			if(AuthEnum.SUPER_ADMIN_AUTH_INDEX.contains(authBO.getAuthId())){
				throw new RuntimeException("严禁修改管理员级别权限");
			}
			int count = authService.queryAuthByNameAndId(authBO);
			if(count!=0){
				throw new RuntimeException("该权限名已经存在");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			authService.updateAuth(authBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->更新权限失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title deleteAuth
	 * @Description: TODO(通过authIds批量删除权限：【物理删除】)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"authIds":[1,2,3,4]
	 }
	 */
	@RequestMapping(value = "/auth/deleteAuth",method = RequestMethod.POST)
	public ResultResp deleteAuth(@RequestBody AuthBO authBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.DELETE_AUTH.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			for (Integer authId : authBO.getAuthIds()) {
				if(AuthEnum.SUPER_ADMIN_AUTH_INDEX.contains(authId)){
					throw new RuntimeException("严禁删除管理员级别权限");
				}
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			authService.deleteAuth(authBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->删除权限信息失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
}
