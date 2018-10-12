/*
 * Welcome to use the TableGo Tools.
 * 
 * http://vipbooks.iteye.com
 * http://blog.csdn.net/vipbooks
 * http://www.cnblogs.com/vipbooks
 * 
 * Author:bianj
 * Email:edinsker@163.com
 * Version:5.0.0
 */

package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * BANK_MONEY_FLOW
 * 
 * @author bianj
 * @version 1.0.0 2018-03-15
 */
@Getter
@Setter
public class BankMoneyFlowVO {
    /** 版本号 */
    private static final long serialVersionUID = 3901136920888734403L;

    /** 主键id */
    private Integer id;

    /** 交易编号 */
    private String moneyFlowNo;

    /** 银行交易流水编号 */
    private String bankTransactionNo;

    /** 出金，入金的金额 */
    private Double amount;

    /** 银行卡号 */
    private String bankCard;

    /** 创建时间 */
    private Long createTime;

    /** 创建人 */
    private String createBy;

    /** 初审完成时间 */
    private Long firstTime;

    /** 银行名称 */
    private String bankName;

    /** 转账类型？？？？？ */
    private Integer transferType;

    /** 复审完成时间 */
    private Long secondTime;

    /** 企业id */
    private Integer enterpriseId;

    /** 账户id */
    private Integer accountId;

    /** 企业名称 */
    private String enterpriseName;

    /** 操作类型 1代表入金 2代表出金 */
    private Integer operationType;

    /** 1代表人工 or 2代表自动 */
    private Integer manuslAutomatic;

    /** 初审的人 */
    private String firstCheckBy;

    /** 复审的人 */
    private String secondCheckBy;

    /** 交易流水hash */
    private String cashFlowHash;

    /** 状态  10种状态 */
    private Integer state;

    /** 完成时间 */
    private Long endTime;
    /**
     * 备注信息
     */
    private String memorandum;

   
   
}