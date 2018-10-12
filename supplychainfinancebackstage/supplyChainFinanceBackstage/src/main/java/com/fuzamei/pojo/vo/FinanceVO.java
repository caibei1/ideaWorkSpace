package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FinanceVO {
	private Integer transactionFlowId;  //交易流水编号
	private String   moneyFlowNo;
	private String  enterpriseName;     //（企业名称）
	private Double  amount;             //入金和出金的额度
	private Long    createTime;         //创建时间
	private String   cardId;             //银行卡号
}
