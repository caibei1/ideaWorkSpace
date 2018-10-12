package com.fuzamei.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import com.fuzamei.pojo.bo.AuthBO;
import com.fuzamei.pojo.bo.RoleAuthBO;
import com.fuzamei.pojo.bo.RoleBO;
import com.fuzamei.pojo.bo.UserRoleBO;
import com.fuzamei.pojo.po.BackRoleAuthPO;
import com.fuzamei.pojo.po.BackUserRolePO;

public interface IdentityManagementMapper {

	@Delete("delete from back_user_role where user_id=#{userId}")
	void deleteOriginalAssignment4Account(UserRoleBO userRoleBO);//删除账号角色表中所有userId对应的信息

	void insertNewAssignment4Account(UserRoleBO userRoleBO);//给userId分配多个角色id

	@Delete("delete from back_role_authority where role_id=#{roleId}")
	void deleteOriginalAssignment4Role(RoleAuthBO roleAuthBO);//删除角色权限表中所有的roleid对应的信息

	void insertNewAssignment4Role(RoleAuthBO roleAuthBO);//给roleId分配多个权限id

	@Insert("insert into back_user_role(user_id,role_id) values(#{userId},#{roleId})")//超级管理员分配角色
	void createRole4Admin(BackUserRolePO backUserRolePO);

	void createAuth4Admin(BackRoleAuthPO backRoleAuthPO);//超级管理员角色分配权限

	void deleteRoleFromUserRole(RoleBO roleBO);//删除用户角色中角色信息

	void deleteRoleFromRoleAuth(RoleBO roleBO);//删除角色权限中角色信息

	void deleteAuthFromRoleAuth(AuthBO authBO);//删除角色权限表中的权限信息

}
