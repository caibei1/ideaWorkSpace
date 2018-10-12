package com.fuzamei.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BackAuthDTO {
	private Integer authorityId;//权限id
	private String authorityName;//权限名称
	private Integer isSelected;//被选中
}
