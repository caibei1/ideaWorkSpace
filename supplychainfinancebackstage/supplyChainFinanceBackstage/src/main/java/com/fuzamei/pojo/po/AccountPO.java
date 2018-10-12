package com.fuzamei.pojo.po;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AccountPO {
	private Integer id;
	private Integer accountId;
	private String  accountName;
	private Long    createTime;
	private Long    modifyTime;
	private Double  totalAsset;//（账户表的总资产）
	private Double  freezeMoney;//冻结金额
	private Double  usableMoney;//可用金额
	
}
