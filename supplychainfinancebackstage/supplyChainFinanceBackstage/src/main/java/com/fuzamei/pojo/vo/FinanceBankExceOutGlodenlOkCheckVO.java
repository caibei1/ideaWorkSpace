package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FinanceBankExceOutGlodenlOkCheckVO {//只针对出金 第一次审核  已审核  导出Excel用的
	
	private String   moneyFlowNo;              //交易编号
	private String   enterpriseName;           //企业名称
	private String   createTime;               //创建时间
	private Double   amount;				   //出金，入金的金额
	private String   firstTime;                //初审完成时间
	private String   stateName;                  //back_state表的 状态名称
	
	public FinanceBankExceOutGlodenlOkCheckVO(String moneyFlowNo, String enterpriseName,String  createTime,  Double amount, String firstTime,String stateName) {
		this.moneyFlowNo = moneyFlowNo;
		this.enterpriseName = enterpriseName;
		this.createTime = createTime;
		this.amount = amount;
		this.firstTime = firstTime;
		this.stateName = stateName;
	}
}   
