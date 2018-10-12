package com.fuzamei.pojo.po;

import java.util.List;

import com.fuzamei.pojo.BasePojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BackAuthPO extends BasePojo{
	private Integer authId;//权限id
	private String authName;//权限名
}
