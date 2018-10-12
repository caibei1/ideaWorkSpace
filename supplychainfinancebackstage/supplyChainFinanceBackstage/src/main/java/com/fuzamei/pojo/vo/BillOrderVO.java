package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BillOrderVO {
	private String payedEnterprise;//付款企业
	private Double money;//应收账份额
	private Double lockupMoney;//质押份额(审定份额)
	private Double financingMoney;//融资份额
	private Double interestRate;//回购利率
	private Long expireTime;//回购日
	private Double repayment;//还款额
	private Integer status;//状态id
	private String statusName;//状态名
	private String hash;//查询区块链的hash值
}
