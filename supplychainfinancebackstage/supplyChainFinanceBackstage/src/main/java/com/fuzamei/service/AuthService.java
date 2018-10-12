package com.fuzamei.service;

import com.fuzamei.pojo.bo.AuthBO;
import com.fuzamei.util.PageDTO;

public interface AuthService {

	void createAuth(AuthBO authBO);

	int queryAuthByName(String authName);

	void updateAuth(AuthBO authBO);

	PageDTO queryAuthInfo(AuthBO authBO);

	void deleteAuth(AuthBO authBO);

	int queryAuthByNameAndId(AuthBO authBO);

}
