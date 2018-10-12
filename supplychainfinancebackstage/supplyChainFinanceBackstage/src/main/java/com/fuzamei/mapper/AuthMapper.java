package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.fuzamei.pojo.bo.AuthBO;
import com.fuzamei.pojo.vo.AuthVO;

public interface AuthMapper {
	
	@Insert("insert into back_authority(authority_name,authority_url,create_time,update_time) values(#{authName},#{authUrl},#{createTime},#{updateTime})")
	void createAuth(AuthBO authBO);
	
	@Insert("insert into back_authority(authority_id,authority_name,authority_url,create_time,update_time) values(#{authId},#{authName},#{authUrl},#{createTime},#{updateTime})")
	void createSystemAuth(AuthBO authBO);

	@Select("select count(*) from back_authority where authority_name=#{authName} or authority_url=#{authUrl}")
	int queryAuthByName(String authName);

	@Update(value="update back_authority set authority_name=#{authName},update_time=#{updateTime} where authority_id=#{authId}")
	void updateAuth(AuthBO authBO);

	List<AuthVO> queryAuthInfo(AuthBO authBO);//查询所有符合条件的权限信息

	int queryAuthInfoCount(AuthBO authBO);//查询所有符合条件的权限信息条数供前端分页用

	void deleteAuth(AuthBO authBO);//根据id删除权限信息

	void createAdminAuth(AuthBO authBO);//给超级管理员将所有权限信息插入权限表

	@Select("select count(*) from back_authority where authority_name=#{authName} and authority_id!=#{authId}")
	int queryAuthByNameAndId(AuthBO authBO);//权限修改的时候查询除该权限id以外是否有相同的权限名

	
}
