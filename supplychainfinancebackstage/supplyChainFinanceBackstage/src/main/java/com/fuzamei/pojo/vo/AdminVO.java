package com.fuzamei.pojo.vo;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AdminVO {
	private Integer userId;//用户id
	private String username;//用户名
	private String tokenId;//合成好的令牌
	private List<String> roles;//角色名字
	private List<String> authurl;//权限url
	private Map<String, Object> authUrlJSON;
	private Boolean ifFirstLogin;//显示是否是初次登录
}
