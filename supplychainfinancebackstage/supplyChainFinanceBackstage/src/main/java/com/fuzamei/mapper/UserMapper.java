package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.fuzamei.pojo.bo.PwdBO;
import com.fuzamei.pojo.bo.UserBO;
import com.fuzamei.pojo.dto.AdminDTO;
import com.fuzamei.pojo.dto.BackRoleDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.BackTokenPO;
import com.fuzamei.pojo.po.BackUserPO;
import com.fuzamei.pojo.vo.AccountVO;
import com.fuzamei.pojo.vo.UserRoleVO;

public interface UserMapper {
	
	@Select(value = "select count(*) from back_token where user_id = #{userId} and token = #{token}")
	int verificationToken(@Param("userId") int userId, @Param("token") String token);//查询用户token信息是否存在

	UserDetailDTO queryUserByNameAndPwd(@Param("username") String username, @Param("password") String password);//根据用户名密码查询用户详细信息
	
	@Insert(value = "insert into back_token(user_id,token,create_time,update_time) "
			+ "values(#{userId},#{token},#{createTime},#{updateTime})")
	void insertToken(BackTokenPO backTokenPO);//当用户初次登录时候需要插入对应的token信息

	@Update(value = "update back_token set token=#{token} where user_id=#{userId}")
	void updateToken(@Param("userId") int userId, @Param("token") String token);//当用户再次登录时候需要更新对应的token信息

	@Select(value = "select * from back_users limit 1")
	AdminDTO queryAdmin();//查询管理员是否存在

	@Insert(value="insert into back_users(user_id,username,password,parent_id,person_name,create_time,update_time,is_delete,public_key,private_key) "
			+ "values(#{userId},#{username},#{password},#{parentId},#{personName},#{createTime},#{updateTime},#{isDelete},#{publicKey},#{privateKey})")
	void createAdmin(BackUserPO userPO);//将管理员的信息插入到表中

	UserDetailDTO queryUserAuthority(Integer userId);//根据用户id查询用户所有详细信息
	
	@Select(value = "select count(*) from back_users where username=#{username} and is_delete=1")
	int queryUserByName(String username);//通过用户名查询用户信息数量

	@Insert(value="insert into back_users(username,password,id_card_num,person_name,create_time,update_time,is_delete,public_key,private_key) "
			+ "values(#{username},#{password},#{idCardNum},#{personName},#{createTime},#{updateTime},#{isDelete},#{publicKey},#{privateKey})")
	@Options(useGeneratedKeys=true, keyProperty="userId", keyColumn="user_id")//用于返回主键id给参数UserBO对象
	void createAccount(UserBO userBO);//创建新用户

	@Update(value="update back_users set username=#{username},password=#{password},update_time=#{updateTime} where user_id=#{userId}")
	void updateAccount(UserBO userBO);//更新账号信息

	//以下用于账户信息查询的方法
	List<AccountVO> queryAccountInfoBySuperAdmin(UserBO userBO);//超级管理员查询账户信息用于显示
	int queryAccountInfoCountBySuperAdmin(UserBO userBO);//超级管理员查询账户信息的总条数用于分页
	List<AccountVO> queryAccountInfoByAdmin(UserBO userBO);//普通管理员查询账户信息用于显示
	int queryAccountInfoCountByAdmin(UserBO userBO);//普通管理员查询账户信息的总条数用于分页

	void deleteAccount(UserBO userBO);//逻辑删除账户信息

	@Select("select role_id,role_name from back_role where role_id!=1")
	List<BackRoleDTO> queryAllRoles();//查询后台所有的角色信息（不展示超级管理员的角色信息）
	
	@Select("select role_id,role_name from back_role where role_id not in (1,2)")
	List<BackRoleDTO> queryAllRolesByAdmin();

	@Select("select role_id from back_user_role where user_id=#{userId} and role_id!=1")
	List<Integer> showRoleInfoUnderAccount(UserBO userBO);//查询索要查询用户id下所有的roleId

	@Select("select count(*) from back_users where username=#{username} and user_id!=#{userId} and is_delete=1")
	int queryUserByNameAndId(UserBO userBO);

	@Select("select * from back_users where user_id=#{userId}")
	UserDetailDTO queryUserById(UserBO userBO);
	
	@Update("update back_users set password=#{newPwd},update_time=#{updateTime} where user_id=#{userId}")
	void modifyPassword(PwdBO pwdBO);
	
	@Select("select count(*) from back_users where is_delete=0 and username=#{username} and password=#{password}")
	int queryUserByNameAndPwdOfDelete(UserBO userBO);
	
	@Select("select count(*) from back_user_role where role_id=2")
	int ifHaveAdminRole();
	
	@Select("select user_id from back_user_role where role_id=2")
	Integer getAdminUserId();//有问题的，要改
	
	@Select("select user_id from back_user_role where role_id in (1,2)")
	List<Integer> queryAllAdminUserIds();


	

}
