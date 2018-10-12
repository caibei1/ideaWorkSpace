package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTransactionFlowBO {
    /**
     * 交易id
     * transaction_flow_id
     */
    private Integer transactionFlowId;

    /**
     * 应收账id
     * b_id
     */
    private Integer bId;

    /**
     * 持有的应收账id
     * b_order_id
     */
    private Integer bOrderId;

    /**
     * 出账公司id
     * transaction_flow_out_enterprise
     */
    private Integer transactionFlowOutEnterprise;

    /**
     * 出账企业名称
     * transaction_flow_out_enterprise_name
     */
    private String transactionFlowOutEnterpriseName;

    /**
     * 收账公司id
     * transaction_flow_in_enterprise
     */
    private Integer transactionFlowInEnterprise;

    /**
     * 收账企业名称
     * transaction_flow_in_enterprise_name
     */
    private String transactionFlowInEnterpriseName;

    /**
     * 交易份额
     * transaction_flow_money
     */
    private Double transactionFlowMoney;

    /**
     * 交易时间(utc时间)
     * transaction_flow_transaction_time
     */
    private Long transactionFlowTransactionTime;

    /**
     * 完成时间
     * transaction_flow_completion_time
     */
    private Long transactionFlowCompletionTime;

    /**
     * 贴现率
     * transaction_flow_discount_rate
     */
    private Double transactionFlowDiscountRate;

    /**
     * 贴现费
     * transaction_flow_discount_charges
     */
    private Double transactionFlowDiscountCharges;

    /**
     * 操作业务类型
     * transaction_flow_business_id
     */
    private Integer transactionFlowBusinessId;

    /**
     * 操作状态
     * transaction_flow_state_id
     */
    private Integer transactionFlowStateId;

    /**
     * 附言
     * transaction_flow_remark
     */
    private String transactionFlowRemark;

    /**
     * hash值
     * transaction_flow_hash
     */
    private String transactionFlowHash;

    /**
     * 订单金额
     * transaction_flow_order_money
     */
    private Double transactionFlowOrderMoney;

    /**
     * 支付id
     * transaction_flow_pay_id
     */
    private Integer transactionFlowPayId;

    /**
     * 包id
     * p_id
     */
    private Integer pId;

    /**
     * 兑付/回购金额
     * transaction_flow_honour_money
     */
    private Double transactionFlowHonourMoney;

    /**
     * 回购时间
     * transaction_flow_buy_back_date
     */
    private Long transactionFlowBuyBackDate;

}
