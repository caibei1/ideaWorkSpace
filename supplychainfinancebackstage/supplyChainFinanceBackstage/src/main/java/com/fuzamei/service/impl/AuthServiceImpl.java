package com.fuzamei.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.mapper.AuthMapper;
import com.fuzamei.mapper.IdentityManagementMapper;
import com.fuzamei.pojo.bo.AuthBO;
import com.fuzamei.pojo.vo.AuthVO;
import com.fuzamei.service.AuthService;
import com.fuzamei.util.PageDTO;
@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private AuthMapper authMapper;
	
	@Autowired
	private IdentityManagementMapper identityManagementMapper;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void createAuth(AuthBO authBO) {
		Long currentTime = System.currentTimeMillis();
		authBO.setCreateTime(currentTime);
		authBO.setUpdateTime(currentTime);
		AuthEnum[] authes = AuthEnum.values();
		for (AuthEnum authEnum : authes) {
			String authUrl = authEnum.getAuthUrl();
			if(authUrl.equals(authBO.getAuthUrl())){
				authBO.setAuthId(authEnum.getAuthId());
				authMapper.createSystemAuth(authBO);//如果url匹配系统中预设的权限url值，则指定插入的authId
				return;
			}
		}
		authMapper.createAuth(authBO);
	}

	@Override
	public int queryAuthByName(String authName) {
		return authMapper.queryAuthByName(authName);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void updateAuth(AuthBO authBO) {
		Long currentTime = System.currentTimeMillis();
		authBO.setUpdateTime(currentTime);
		authMapper.updateAuth(authBO);
	}

	@Override
	public PageDTO queryAuthInfo(AuthBO authBO) {
		authBO.setAuthIdThreshHold(AuthEnum.SUPER_ADMIN_AUTH_INDEX.size());//设置权限id阈值
		List<AuthVO> list = authMapper.queryAuthInfo(authBO);
		int count = authMapper.queryAuthInfoCount(authBO);
		return PageDTO.getPagination(count, list);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void deleteAuth(AuthBO authBO) {
		authMapper.deleteAuth(authBO);
		//删除角色权限表中的权限信息
		identityManagementMapper.deleteAuthFromRoleAuth(authBO);
	}

	@Override
	public int queryAuthByNameAndId(AuthBO authBO) {
		return authMapper.queryAuthByNameAndId(authBO);
	}

}
