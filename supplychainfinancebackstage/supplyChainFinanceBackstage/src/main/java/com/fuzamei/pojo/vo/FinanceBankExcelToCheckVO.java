package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FinanceBankExcelToCheckVO {//针对出金 待复审  导出Excel用的
	
	private String   moneyFlowNo;              //交易编号
	private String   enterpriseName;           //企业名称
	private Double   amount;				   //出金，入金的金额
	private String   firstTime;                //初审完成时间
	private String   personName;               //back_users表的人名字  初审人员
	
	public FinanceBankExcelToCheckVO(String moneyFlowNo, String enterpriseName,String  firstTime,  Double amount, String  personName ) {
		this.moneyFlowNo = moneyFlowNo;
		this.enterpriseName = enterpriseName;
		this.amount = amount;
		this.firstTime = firstTime;
		this.personName = personName;
	}
	
}   
