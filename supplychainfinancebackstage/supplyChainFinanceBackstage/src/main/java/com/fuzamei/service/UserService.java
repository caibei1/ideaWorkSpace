package com.fuzamei.service;

import java.util.List;

import com.fuzamei.pojo.bo.PwdBO;
import com.fuzamei.pojo.bo.UserBO;
import com.fuzamei.pojo.dto.AdminDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.vo.AdminVO;
import com.fuzamei.pojo.vo.UserRoleVO;
import com.fuzamei.util.PageDTO;

public interface UserService {

	boolean verificationToken(int userId, String token);

	UserDetailDTO queryUserByNameAndPwd(String username, String password);

	void insertToken(Integer userId, String token);

	void updateToken(Integer userId, String token);

	AdminDTO queryAdmin();

	AdminVO createAdmin(String username, String password, String token);
	
	UserDetailDTO checkUserAuthority(Integer userId,boolean andOrNot,String... authorities);
	
	void checkUserRole(Integer userId,boolean andOrNot,String... roles);

	int queryUserByName(String username);

	void createAccount(UserBO userBO);

	void updateAccount(UserBO userBO);

	PageDTO queryAccountInfo(UserBO userBO);

	void deleteAccount(UserBO userBO);

	UserRoleVO showRoleInfoUnderAccount(UserBO userBO);

	int queryUserByNameAndId(UserBO userBO);

	UserDetailDTO queryUserById(UserBO userBO);

	void modifyPassword(PwdBO pwdBO);

	int queryUserByNameAndPwdOfDelete(UserBO userBO);

	List<Integer> queryAllAdminUserIds();
}
