package com.fuzamei.mapper;


import com.fuzamei.pojo.po.EnterprisePO;


public interface EnterpriseMapper {

	
	EnterprisePO selectOneEnterpriseByName(String  enterpriseName);
	
}
