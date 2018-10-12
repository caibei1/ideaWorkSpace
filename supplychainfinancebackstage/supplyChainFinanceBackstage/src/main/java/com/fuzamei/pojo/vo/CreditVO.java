package com.fuzamei.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditVO {
	private Integer enterpriseId;//收款企业id
	private String enterpriseName;//收款企业名称
	private Double creditLine;//总授信额度
	private Double unconsumedLoan;//未使用额度
	private Double consumedLoan;//已经使用额度
	private Double tobePayedLoan;//待还款额
	private Double totalRepayment;//总还款额度
}
