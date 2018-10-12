package com.fuzamei.pojo.po;

import java.util.List;

import com.fuzamei.pojo.BasePojo;

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
public class BackRolePO extends BasePojo{
	private Integer roleId;//角色id
	private String roleName;//角色名称
}
