package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinanceBankExcelGoldenRecordVO {//针对入金记录表待划拨的导出Excel
	
	private String   moneyFlowNo;              //交易编号
	private String   createBy;                 //法人姓名
	private String   memorandum;               //备注信息or划拨企业
	private Double   amount;				   //出金，入金的金额
	private String   createTime;               //创建时间
	private String   bankCard;                 //银行卡号

	
	public FinanceBankExcelGoldenRecordVO(String moneyFlowNo, String createBy,String  memorandum,Double amount, String bankCard,String  createTime ) {
		this.moneyFlowNo = moneyFlowNo;
		this.createBy = createBy;
		this.amount = amount;
		this.createTime = createTime;
		this.bankCard = bankCard;
		this.memorandum = memorandum;
		
	}
}
