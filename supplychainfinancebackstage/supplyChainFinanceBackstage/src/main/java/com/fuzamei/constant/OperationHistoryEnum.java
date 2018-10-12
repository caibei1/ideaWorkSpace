package com.fuzamei.constant;

import lombok.Getter;

@Getter
public enum OperationHistoryEnum {
	
	INITPLATFORM(1,"平台初始化"),
	CREATEROLE(2,"创建后台角色"),
	UPDATEROLE(3,"修改后台角色"),
	DELETEROLE(4,"删除后台角色"),
	CREATEOFFICIAL(5,"创建后台人员"),
	UPDATEOFFICIAL(6,"修改后台人员"),
	DELETEOFFICIAL(7,"删除后台人员"),
	EXAMINEASSET(8,"资产审核录入"),//就是单据审核同意操作
	SETCREDIT(9,"设置企业用户信用总额度"),
	EXAMINEDEPOSIT(10,"审核企业用户入金"),
	EXAMINEWITHDRAW(11,"出纳审核企业用户出金"),
	REVIEWWITHDRAW(12,"财务主管审核或复核企业用户出金");
	
	private OperationHistoryEnum(Integer typeId,String type){
		this.typeId=typeId;
		this.type=type;
	}
	private Integer typeId;//操作类型id
	private String type;//操作类型名称
}
