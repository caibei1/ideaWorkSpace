package com.fuzamei.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserDetailDTO {
	private Integer userId;//用户id
	private String username;//用户名
	private String password;//密码
	private String personName;//人名
	private Integer parentId;//上级用户id
	private Integer isDelete;//是否已经被删除1表示正常，0表示删除
	private Long createTime;//创建时间
	private Long updateTime;//更新时间
	private String publicKey;//公钥
	private String privateKey;//私钥
	private String token;//令牌
	private Integer roleId;//角色id号
	private List<String> roleName;//角色名字
	private List<String> authorityName;//权限名
	private List<String> authorityUrl;//权限url地址
}
