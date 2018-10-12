package com.fuzamei.pojo.bo;

import java.util.List;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.pojo.BasePojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AuthBO extends BasePojo{
	private Integer authId;//权限id
	private List<Integer> authIds;//要被删除的权限id
	private String authName;//权限名称
	private String authUrl;//权限url
	private AuthEnum[] authArray;//所有权限详细信息
	
	private Integer authIdThreshHold;//权限id的阈值
}
