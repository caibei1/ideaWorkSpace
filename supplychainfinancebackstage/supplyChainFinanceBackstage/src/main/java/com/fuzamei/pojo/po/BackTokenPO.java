package com.fuzamei.pojo.po;

import com.fuzamei.pojo.BasePojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @describe 对应后台用户的令牌表
 */
@Setter
@Getter
@ToString
public class BackTokenPO extends BasePojo{
	private Integer userId;//用户id
	private String token;//用户令牌
}
