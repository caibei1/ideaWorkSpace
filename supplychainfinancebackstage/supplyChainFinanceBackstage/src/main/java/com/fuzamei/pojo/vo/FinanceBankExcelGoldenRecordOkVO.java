package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FinanceBankExcelGoldenRecordOkVO {//只针对入金记录 已划拨的 导出Excel用的
	
	private String   moneyFlowNo;              //交易编号
	private String   createBy;                 //法人姓名
	private String   transferEnterprise;       //备注信息or划拨企业
	private Double   amount;				   //出金，入金的金额
	private String   createTime;               //创建时间
	private String   endTime;                  //完成时间
	private String   bankCard;                 //银行卡号
	private String   stateName;                //back_state表的 状态名称
	
	public FinanceBankExcelGoldenRecordOkVO(String moneyFlowNo, String createBy,String  transferEnterprise,Double amount,String createTime,String bankCard, String endTime,String  stateName ) {
		this.moneyFlowNo = moneyFlowNo;
		this.createBy = createBy;
		this.transferEnterprise = transferEnterprise;
		this.amount = amount;
		this.createTime = createTime;
		this.endTime = endTime;
		this.bankCard = bankCard;
		this.stateName = stateName;
		
	}
}   
