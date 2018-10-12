package com.fuzamei.service;

import com.fuzamei.pojo.bo.RoleAuthBO;
import com.fuzamei.pojo.bo.UserRoleBO;

public interface IdentityManagementService {

	void assignRoles4Account(UserRoleBO userRoleBO);

	void assignAuthes4Role(RoleAuthBO roleAuthBO);

}
