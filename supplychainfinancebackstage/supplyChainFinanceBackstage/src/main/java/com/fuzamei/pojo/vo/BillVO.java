package com.fuzamei.pojo.vo;
/**
 * 
 * @author 应收账单实体类，供前端查看用的实体类
 *
 */

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BillVO {
	private Integer billId;//应收账款id号--->>对应bill表中的(应收帐编号)b_id
	private Long checkinTime;//登记时间--->>对应bill表中的申请时间(utc时间)bill_create_time
	private String payedEnterprise;//付款企业名称--->>对应bill表中的出账企业名称,bill_out_enterprise_name
	private Integer receivedEnterpriseId;//收款企业id--->>对应bill表中的收账企业id，bill_in_enterprise
	private String receivedEnterprise;//收款企业名称--->>对应bill表中的收账企业名称,bill_in_enterprise_name
	private String warrantEnterprise;//担保企业--->>对应bill表中的担保企业名称，bill_warrantor_name
	private Double billMoney;//账单总额--->>对应bill表中的账单总金额，bill_money
	private Long chargeoffTime;//出账日期--->>对应bill表中的出账日,bill_outbill_date
	private Long endTime;//承兑日期--->>对应bill表中的到期日（utc时间）,bill_end_date
	private Double interestRate;//还款利率--->>对应bill表中的协议利率,agreed_interest_rates
	private String attachmentUrl;//附件下载地址（找attachment表里面type是zip结尾的url，只有一条信息）
//	private List<String> responsibleUrls;//尽职调查报告下载地址(详细信息封装在BackAttachmentVO中)
	private List<ResponsibleAttachmentVO> responsibleAttachments;//尽职调查报告下载地址(详细信息封装在BackAttachmentVO中)
	private String statusName;//状态名称
}
