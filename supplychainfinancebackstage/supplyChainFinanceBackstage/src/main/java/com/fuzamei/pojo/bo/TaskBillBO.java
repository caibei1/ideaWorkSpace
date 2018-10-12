package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskBillBO {
    /**
     * 应收帐编号
     * b_id
     */
    private Integer bId;

    /**
     * 出账企业
     * bill_out_enterprise
     */
    private Integer billOutEnterprise;

    /**
     * 出账企业名称
     * bill_out_enterprise_name
     */
    private String billOutEnterpriseName;

    /**
     * 收账企业
     * bill_in_enterprise
     */
    private Integer billInEnterprise;

    /**
     * 收账企业名称
     * bill_in_enterprise_name
     */
    private String billInEnterpriseName;

    /**
     * 担保企业
     * bill_warrantor_id
     */
    private Integer billWarrantorId;

    /**
     * 担保企业名称
     * bill_warrantor_name
     */
    private String billWarrantorName;

    /**
     * 合同编号
     * contract_no
     */
    private String contractNo;

    /**
     * 系统编号
     * system_num
     */
    private String systemNum;

    /**
     * 账单总金额
     * bill_money
     */
    private Double billMoney;

    /**
     * 凭证(正面)
     * evidence_front
     */
    private String evidenceFront;

    /**
     * 出账日
     * bill_outbill_date
     */
    private Long billOutbillDate;

    /**
     * 申请时间（utc时间）
     * bill_create_time
     */
    private Long billCreateTime;

    /**
     * 到期日（utc时间）
     * bill_end_date
     */
    private Long billEndDate;

    /**
     * 应收账款的状态
     * bill_state_id
     */
    private Integer billStateId;

    /**
     * 操作类型
     * bill_operator_type
     */
    private Integer billOperatorType;

    /**
     * 对手企业
     * rival_enterprise
     */
    private String rivalEnterprise;

    /**
     * hash值
     * bill_hash
     */
    private String billHash;

    /**
     * 协议利率
     * agreed_interest_rates
     */
    private Double agreedInterestRates;

    /**
     * 借款利率
     */
    private Double billBorrowingRate;

    /**
     * 1 线下合同 2 线上合同
     * type
     */
    private String type;

    /**
     * 修改时间
     * bill_modify_time
     */
    private long billModifyTime;

    /**
     * 单据审定额度
     */
    private Double billApproveMoney;

    /**
     * 后台状态
     */
    private Integer status;
}
