package com.fuzamei.service.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fuzamei.mapper.UserManagementMapper;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.service.UserManagementService;
import com.fuzamei.util.GetJsonData;
import com.fuzamei.util.PageDTO;

@Service
public class UserManagementServiceImpl implements UserManagementService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementServiceImpl.class);
	@Autowired
	private UserManagementMapper userManagementMapper;
	
	/**
	 * 查询用户管理(企业注册后)
	 */
	@Override
	public PageDTO queryUserManagement(EnterpriseDTO enterpriseDTO) {
		List<EnterpriseDTO> userslist = userManagementMapper.queryUserManagement(enterpriseDTO);
		int count = userManagementMapper.queryUserManagementCount(enterpriseDTO);
		return PageDTO.getPagination(count, userslist);
		
	}
    /**
     *查询所有的状态1通过， 2未通过  给前端
     */
	@Override
	public List<EnterprisePO> queryAllStatus() {
		return userManagementMapper.queryAllStatus();
	}
	
	/**
	 * 查看身份证正面照片
	 */
	@Override
	public EnterpriseDTO queryIdentityCardFront(EnterpriseDTO enterpriseDTO) {
		EnterpriseDTO  tokenNo=(EnterpriseDTO) userManagementMapper.queryUserManagement(enterpriseDTO);
		String token=tokenNo.getPlatformtoken();
		
		enterpriseDTO.setTag(1);
		enterpriseDTO.setType(1); 
		enterpriseDTO.setPlatformtoken(token);
		return enterpriseDTO;
		
	}
	
	//查询user信息
	@Override
	public List<EnterpriseDTO> queryUserInfo() {
		return userManagementMapper.queryUserInfo();
	}

}
