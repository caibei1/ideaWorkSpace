package com.fuzamei.pojo.bo;

import java.util.List;

import com.fuzamei.pojo.BasePojo;
import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RoleBO extends BasePojo{
	private Integer roleId;//角色id
	private String roleName;//角色名称
	private List<Integer> roleIds;//批量删除角色id时候用
	
	private Integer operatorId;//当前操作人的id号
	private String operatorRoleName;//当前操作人的角色名称
	private Integer authIdThreshHold;//权限id的一个阈值，用于屏蔽超级管理员级别的所有权限信息
	private UserDetailDTO userDetailDTO;
}
