package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BillOrderPO {
	private Integer billOrderId;//对应bill_order表中b_order_id字段
	private Integer billId;//对应bill_order中b_id字段
	private Integer payedEnterpriseId;//付款企业id，对应b_order_out_enterprise
	private String payedEnterpriseName;//付款企业名称，对应b_order_out_enterprise_name
	private Integer receivedEnterpriseId;//对应b_order_in_enterprise
	private String receivedEnterpriseName;//对应b_order_in_enterprise_name
	private Double interestRate;//对应borrowing_rate借款利率
	private Double billMoney;//对应bill_money 账单那张实体单子上标着的金额数量
	private Double approveMoney;//对应approve_money（单据本身可以借贷的额度）
	private Long endTime;//对应bill_end_date(承兑日，单据的截止日期，不是单据的回购日，不是一个概念)
	private Long createTime;//对应b_order_create_time
	private Long updateTime;//对应b_order_modify_time
	private Integer status;//状态id，对应b_order_state_id
	
	private String billOrderHash;//从bill表的billHash里查询出来的hash值
}
 