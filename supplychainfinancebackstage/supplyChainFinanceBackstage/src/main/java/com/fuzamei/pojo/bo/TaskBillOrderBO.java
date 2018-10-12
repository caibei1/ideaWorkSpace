package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TaskBillOrderBO extends TaskBillBO {
    /**
     * 订单id||市场id
     * b_order_id
     */
    private Integer bOrderId;


    /**
     * 发起交易企业
     * b_order_out_enterprise
     */
    private Integer bOrderOutEnterprise;

    /**
     * 发起交易企业名称
     * b_order_out_enterprise_name
     */
    private String bOrderOutEnterpriseName;

    /**
     * 接收交易企业
     * b_order_in_enterprise
     */
    private Integer bOrderInEnterprise;

    /**
     * 接受交易名称
     * b_order_in_enterprise_name
     */
    private String bOrderInEnterpriseName;

    /**
     * 持有金额||质押金额
     * b_order_money
     */
    private Double bOrderMoney;

    /**
     * 交易金额(支付||挂牌）
     * b_order_record_money
     */
    private Double bOrderRecordMoney;

    /**
     * 父id
     * parent_id
     */
    private Integer parentId;

    /**
     * 高度(从0)
     * level
     */
    private Integer level;

    /**
     * 源packid
     * pack_id
     */
    private Integer packId;

    /**
     * 合并支付id
     * pay_id
     */
    private Integer payId;

    /**
     * utc时间
     * b_order_create_time
     */
    private Long bOrderCreateTime;

    /**
     * 订单状态(需要讨论状态命名是否需要区分)
     * b_order_state_id
     */
    private Integer bOrderStateId;

    /**
     * 操作类型(需要讨论状态命名是否需要区分)
     * b_order_operator_type
     */
    private Integer bOrderOperatorType;

    /**
     * 贴现率
     * discount_rate
     */
    private Double discountRate;

    /**
     * 借款利率
     */
    private Double billOrderBorrowingRate;

    /**
     * 贴现费
     * discount_charges
     */
    private Double discountCharges;

    /**
     * hash
     * b_order_hash
     */
    private String bOrderHash;

    /**
     * 备注
     * remark
     */
    private String remark;

    /**
     * 订单金额
     * order_money
     */
    private Double orderMoney;

    /**
     * 回购时间
     * buy_back_date
     */
    private Long buyBackDate;

    /**
     * 修改时间
     * b_order_modify_time
     */
    private Long bOrderModifyTime;


    /**
     * 回购完成时间
     */
    private Long bOrderCompletionTime;

    /**
     * 单据审定额度
     */
    private Double billOrderApproveMoney;
}
