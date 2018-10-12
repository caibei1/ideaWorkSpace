package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RoleVO {
	private Integer roleId;
	private String roleName;
	private Long createTime;
	private Long updateTime;
}
