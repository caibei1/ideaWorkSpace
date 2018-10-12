package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FinanceBankExceOutGlodenlVO {//只针对出金 第一次审核  待审核  导出Excel用的
	
	private String   moneyFlowNo;              //交易编号
	private String   enterpriseName;           //企业名称
	private String     createTime;               //创建时间
	private Double   amount;				   //出金，入金的金额
	
	
	public FinanceBankExceOutGlodenlVO(String moneyFlowNo, String enterpriseName, Double amount, String createTime) {
		this.moneyFlowNo = moneyFlowNo;
		this.enterpriseName = enterpriseName;
		this.amount = amount;
		this.createTime = createTime;
	}

}   
