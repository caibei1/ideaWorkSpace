package com.fuzamei.pojo.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BackUserVO {
	private Integer userId;//用户id
	private String username;//用户名
	private String tokenId;//合成好的令牌
	private List<String> roles;//角色名字
	private List<String> authUrl;//权限url名
	private Map<String, Object> authUrlJSON;
	private Boolean ifFirstLogin;//查看是否是初次登录true表示初次登录，false表示不是初次登录
	
	private Integer page;
	private Integer startPage;
	private Integer rowNum;
	
}
