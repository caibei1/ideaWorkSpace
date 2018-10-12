package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @describe 对应后台用户的权限表
 */
@Setter
@Getter
@ToString
public class BackUserAuthorityPO {
	private Integer authorityId;//权限id
	private String authorityName;//权限名称
}
