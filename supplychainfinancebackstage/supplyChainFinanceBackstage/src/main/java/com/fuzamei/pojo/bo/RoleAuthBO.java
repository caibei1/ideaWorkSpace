package com.fuzamei.pojo.bo;

import java.util.List;

import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @descri 角色权限关联表实体类
 */
@Setter
@Getter
@ToString
public class RoleAuthBO {
	private Integer roleId;//角色id
	private List<Integer> authIds;//分配好的权限id
	private UserDetailDTO userDetailDTO;
}
