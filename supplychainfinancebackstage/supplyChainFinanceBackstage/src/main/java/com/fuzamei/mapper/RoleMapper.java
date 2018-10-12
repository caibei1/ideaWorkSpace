package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.fuzamei.pojo.bo.RoleAuthBO;
import com.fuzamei.pojo.bo.RoleBO;
import com.fuzamei.pojo.bo.UserRoleBO;
import com.fuzamei.pojo.dto.BackAuthDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.BackRolePO;
import com.fuzamei.pojo.vo.RoleVO;

public interface RoleMapper {

	@Select("select count(*) from back_role where role_name=#{roleName}")
	int queryRoleByName(String roleName);//查询角色名是否重复
	
	@Insert("insert into back_role(role_name,create_time,update_time) values(#{roleName},#{createTime},#{updateTime})")
	@Options(useGeneratedKeys=true, keyProperty="roleId", keyColumn="role_id")//用于返回主键roleId给参数RoleBO对象
	void createRole(RoleBO roleBO);//创建新的角色

	@Update("update back_role set role_name=#{roleName},update_time=#{updateTime} where role_id=#{roleId}")
	void updateRole(RoleBO roleBO);//更新角色信息

	List<RoleVO> queryRoleInfo(RoleBO roleBO);//查询所有符合条件的角色信息供前端显示(超级管理员的方法)
	int queryRoleInfoCount(RoleBO roleBO);//查询所有符合条件的角色信息的条数供前端分页显示(超级管理员的方法)
	List<RoleVO> queryRoleInfoByAdmin(RoleBO roleBO);//查询所有符合条件的角色信息供前端显示(管理员的方法)
	int queryRoleInfoCountByAdmin(RoleBO roleBO);//查询所有符合条件的角色信息的条数供前端分页显示(管理员的方法)

	void deleteRole(RoleBO roleBO);//物理删除角色信息

	@Select("select authority_id,authority_name from back_authority where authority_id > #{authIdThreshHold}")
	List<BackAuthDTO> queryAllAuthes(RoleBO roleBO);//查询所有的权限信息

	@Select("select authority_id from back_role_authority where role_id=#{roleId}")
	List<Integer> showAuthInfoUnderRole(RoleBO roleBO);//查询该roleId下所有auth的id号

	@Insert("insert into back_role(role_id,role_name,create_time,update_time) values(#{roleId},#{roleName},#{createTime},#{updateTime})")
	void createAdminRole(BackRolePO backRolePO);//为管理员创建角色信息

	@Select("select count(*) from back_role where role_name=#{roleName} and role_id!=#{roleId}")
	int queryRoleByNameAndId(RoleBO roleBO);//对于要修改的角色要排除除自己以外相同的名字
	
	UserDetailDTO queryAdminIfoName(String admin);
	
	@Select("select role_name from back_role where role_id=#{roleId}")
	String queryRoleNameByRoleId(RoleAuthBO roleAuthBO);

}
