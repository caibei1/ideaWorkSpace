package com.fuzamei.constant;

import java.util.ArrayList;
import java.util.List;

import com.fuzamei.pojo.po.BackAuthPO;
import com.fuzamei.pojo.po.BackRolePO;

public class Roles {
	private Roles() {
		throw new AssertionError("禁止实例化");
	}
	public static final String SUPER_ADMIN="超级管理员";
	public static final String ADMIN="管理员";
	
	public static final List<String> ADMIN_ROLES = new ArrayList<String>();
	static{
		ADMIN_ROLES.add(SUPER_ADMIN);
		ADMIN_ROLES.add(ADMIN);
	}
	
	public static final List<Integer> ADMIN_ROLE_INDEX = new ArrayList<Integer>();//管理员角色id号
	public static final List<BackRolePO> BACK_ADMIN_ROLES = new ArrayList<BackRolePO>();//用于给超级管理员或者管理员创建角色用
	static{
		for (int i = 0; i < ADMIN_ROLES.size(); i++) {
			ADMIN_ROLE_INDEX.add(i+1);
			BackRolePO backRolePO = new BackRolePO();
			backRolePO.setRoleId(i+1);
			backRolePO.setRoleName(ADMIN_ROLES.get(i));
			BACK_ADMIN_ROLES.add(backRolePO);
		}
	}
}
