package com.fuzamei.service;

import java.util.List;

import com.fuzamei.pojo.bo.RoleBO;
import com.fuzamei.pojo.bo.UserRoleBO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.vo.RoleAuthVO;
import com.fuzamei.util.PageDTO;

public interface RoleService {

	int queryRoleByName(String roleName);

	void createRole(RoleBO roleBO);

	void updateRole(RoleBO roleBO);

	PageDTO queryRoleInfo(RoleBO roleBO);

	void deleteRole(RoleBO roleBO);

	RoleAuthVO showAuthInfoUnderRole(RoleBO roleBO);

	int queryRoleByNameAndId(RoleBO roleBO);

	UserDetailDTO queryAdminIfoName(String admin);

}
