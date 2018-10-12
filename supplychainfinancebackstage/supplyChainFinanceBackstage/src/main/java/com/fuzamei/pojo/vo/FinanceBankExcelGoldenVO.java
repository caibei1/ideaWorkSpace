package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinanceBankExcelGoldenVO {//针对入金审核 已审核的导出Excel
	
	private String   moneyFlowNo;              //交易编号
	private String   enterpriseName;           //企业名称
	private Double   amount;				   //出金，入金的金额
	private String   createTime;               //创建时间
	private String   endTime;                  //完成时间
	private String   bankCard;                 //银行卡号
	private String   stateName;                //back_state表的 状态名称
	
	public FinanceBankExcelGoldenVO(String moneyFlowNo, String enterpriseName, Double amount, String createTime,String endTime, String bankCard,String stateName) {
		this.moneyFlowNo = moneyFlowNo;
		this.enterpriseName = enterpriseName;
		this.amount = amount;
		this.createTime = createTime;
		this.createTime = endTime;
		this.bankCard = bankCard;
		this.stateName = stateName;
	}

}
