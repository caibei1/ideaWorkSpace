package com.fuzamei.mapper;

import java.util.List;

import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.EnterprisePO;

public interface UserManagementMapper {
	
	public List<EnterpriseDTO> queryUserManagement(EnterpriseDTO enterpriseDTO);//查询用户管理
	public int queryUserManagementCount(EnterpriseDTO enterpriseDTO);//查询总条数
	public List<EnterprisePO>  queryAllStatus();//查询所有的状态1通过  ，2未通过
	
	public EnterpriseDTO queryIdentityCardFront(EnterpriseDTO enterpriseDTO);//根据身份证号查询认证token
	public List<EnterpriseDTO> queryUserInfo();//查询user信息
}
