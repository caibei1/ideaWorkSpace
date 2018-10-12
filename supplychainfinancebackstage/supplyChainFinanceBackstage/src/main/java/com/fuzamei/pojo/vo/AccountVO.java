package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AccountVO {
	private Integer userId;
	private String username;
	private String password;
	private Long createTime;
	private Long updateTime;
	
}
