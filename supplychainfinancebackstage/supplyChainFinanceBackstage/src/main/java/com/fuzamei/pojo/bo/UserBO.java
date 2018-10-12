package com.fuzamei.pojo.bo;

import java.util.List;

import com.fuzamei.pojo.BasePojo;
import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserBO extends BasePojo{
	private Integer userId;//用户id
	private String username;//用户名
	private String password;//密码
	private String idCardNum;//身份证号
	private String confirmedIdCardNum;//身份证号确认
	private String personName;//人名
	private String confirmedPersonName;//人名确认
	private String publicKey;//用户的公钥
	private String privateKey;//用户的私钥
	private Integer isDelete;//是否被删除1表示正常使用0表示逻辑删除
	private List<Integer> userIds;//批量删除用户使用的用户id号
	private UserDetailDTO userDetailDTO;//用户详细信息
	
	private Integer operatorId;//当前操作人的id号
	
	private List<Integer> adminIds;//所有管理员的userId号
}
