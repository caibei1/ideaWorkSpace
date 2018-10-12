package com.fuzamei.pojo.vo;
/**
 * 
 * @author ylx
 * @descri 返回单个userId下的角色详细信息
 */

import java.util.List;

import com.fuzamei.pojo.dto.BackRoleDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@ToString
public class UserRoleVO {
	private Integer userId;//用户id
	private List<BackRoleDTO> roleList;
}
