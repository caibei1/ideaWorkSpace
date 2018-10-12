package com.fuzamei.pojo.po;

import com.fuzamei.pojo.BasePojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @describe 对应后台用户表
 */
@Setter
@Getter
@ToString
public class BackUserPO extends BasePojo{
	private Integer userId;//用户id
	private String username;//用户名
	private String password;//密码
	private String personName;//人名
	private Integer parentId;//上级用户id
	private Integer isDelete;//表示是否被删除1表示正常使用0表示已经被逻辑删除
	private String publicKey;//公钥
	private String privateKey;//私钥
	
	private String idCardNum;//身份证号码
}
