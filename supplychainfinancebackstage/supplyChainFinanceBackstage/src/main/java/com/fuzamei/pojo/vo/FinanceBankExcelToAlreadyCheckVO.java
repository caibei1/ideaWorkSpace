package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FinanceBankExcelToAlreadyCheckVO {//针对出金 已复审 导出Excel用的
	
	private String   moneyFlowNo;              //交易编号
	private String   enterpriseName;           //企业名称
	private Double   amount;				   //出金，入金的金额
	private String     firstTime;                //初审完成时间
	//private String   personName;             //back_users表的人名字  初审人员  和复审人员      到时候看可以把 TODO
	private String   chuShenName;             //初审的人
	private String   fuShenName;            //复审的人
	private String     secondTime;               //复审完成时间
	private String   stateName;                //back_state表的 状态名称
	
	public FinanceBankExcelToAlreadyCheckVO(String moneyFlowNo, String enterpriseName,String  firstTime, String  secondTime,Double amount, String chuShenName,String  fuShenName, String  stateName ) {
		this.moneyFlowNo = moneyFlowNo;
		this.enterpriseName = enterpriseName;
		this.amount = amount;
		this.firstTime = firstTime;
		this.secondTime = secondTime;
		this.stateName = stateName;
		this.chuShenName = chuShenName;
		this.fuShenName = fuShenName;
	}
}   
