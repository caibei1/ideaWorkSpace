package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WsjCashFlowPO {

	private Integer transactionId;
	private Long transactionFlowId;
	private Integer enterpriseId;
	private Integer accountId;
	private Integer operatorId;
	private String bankCardId;
	private String enterpriseName;
	private Double amount;
	private Integer stateId;
	private Integer status;
	private Long createTime;
	private Long endTime;
	private Long reviewTime;
	private Integer manuslAutomatic;
	private Integer type;
	private String cashFlowHash;
}
