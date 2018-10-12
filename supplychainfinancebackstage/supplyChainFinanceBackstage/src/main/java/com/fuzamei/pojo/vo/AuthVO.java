package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AuthVO {
	private Integer authorityId;//权限id
	private String authorityName;//权限名
	private Long createTime;//创建时间
	private Long updateTime;//更新时间
}
