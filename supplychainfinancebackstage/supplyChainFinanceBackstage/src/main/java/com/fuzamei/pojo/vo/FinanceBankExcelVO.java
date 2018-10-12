package com.fuzamei.pojo.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinanceBankExcelVO {// 只入金审核 待审核的 导出Excel用的

	private String moneyFlowNo; // 交易编号
	private String enterpriseName; // 企业名称
	private Double amount; // 出金，入金的金额

	// @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	//@DateTimeFormat(pattern = "yyyy-MM-dd  HH:mm:ss") // 用 String
	private String createTime; // 创建时间
	private String bankCard; // 银行卡号


	public FinanceBankExcelVO(String moneyFlowNo, String enterpriseName, Double amount, String createTime,String bankCard) {
		this.moneyFlowNo = moneyFlowNo;
		this.enterpriseName = enterpriseName;
		this.amount = amount;
		this.createTime = createTime;
		this.bankCard = bankCard;
	}

	
}
