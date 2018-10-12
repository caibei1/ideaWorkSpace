package com.fuzamei.pojo.dto;

import com.fuzamei.pojo.po.EnterprisePO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BillDTO {
	private Integer billId;//应收账款id号--->>对应bill表中的(应收帐编号)b_id
	private Long checkinTime;//登记时间--->>对应bill表中的申请时间(utc时间)bill_create_time
	private Integer payedEnterpriseId;//付款企业id--->>对应bill表中的出账企业id,bill_out_enterprise
	private String payedEnterpriseName;//付款企业名称--->>对应bill表中的出账企业名称,bill_out_enterprise_name
	private Integer receivedEnterpriseId;//收款企业id--->>对应bill表中的收账企业id,bill_in_enterprise
	private String receivedEnterpriseName;//收款企业名称--->>对应bill表中的收账企业名称,bill_in_enterprise_name
	private Integer warrantEnterpriseId;//担保企业Id--->>对应bill表中的担保企业名称，bill_warrantor_id
	private String warrantEnterpriseName;//担保企业--->>对应bill表中的担保企业名称，bill_warrantor_name
	private Double billMoney;//账单总额--->>对应bill表中的账单总金额，bill_money
	private Long billOutTime;//出账日期--->>对应bill表中的出账日,bill_outbill_date
	private Long endTime;//承兑日期--->>对应bill表中的到期日（utc时间）,bill_end_date
	private Double interestRate;//还款利率--->>对应bill表中的借款利率,borrowing_rate
	private Integer status;//状态id 0表示未审核，1表示已通过，2表示已拒绝
	private Double consumedLoan;//已经使用的额度
	private Double approveMoney;//用来校验该单据的审定额度是否已经存在了
	
	private EnterprisePO enterprisePO;//对应收款企业的信息
	
	private String billHash;//申请单据的时候产生的hash值
}
