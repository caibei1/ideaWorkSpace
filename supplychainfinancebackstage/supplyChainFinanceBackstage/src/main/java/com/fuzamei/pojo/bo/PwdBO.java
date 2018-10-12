package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @descri 修改密码的实体类
 */
@Setter
@Getter
@ToString
public class PwdBO {
	private Integer userId;//用户id
	private String originalPwd;//原始密码
	private String newPwd;//新密码
	private String newConfirmedPwd;//确认的新密码
	private Long updateTime;//修改时间
}
