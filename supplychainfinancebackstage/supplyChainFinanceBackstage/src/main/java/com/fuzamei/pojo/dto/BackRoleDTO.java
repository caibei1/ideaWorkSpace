package com.fuzamei.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @describe 对应后台用户的角色表
 */
@Setter
@Getter
@ToString
public class BackRoleDTO {
	private Integer roleId;//角色id
	private String roleName;//角色名称
	private Integer isSelected;//被选中
}
