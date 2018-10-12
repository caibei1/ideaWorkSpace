package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FinancePO extends AccountPO  {
	
    private Integer transactionId;//交易流水编号
    private Integer transactionFlowId;//日志编号
    private Integer enterpriseId;//企业id（新加）
    private Integer operatorId;//操作人
    private Long bankCardId;//银行卡号（新加）
    private String enterpriseName;//企业名称（新加）
    private Double amount;//额度
    private Integer stateId;//操作动作 1代表入金   2 代表出金
    private Integer status;// 状态  5待审核  6已审核
    private Long createTime;//创建时间
    private Long endTime;//完成时间
    private Long reviewTime;//复审完成时间（新加的）
    private Integer manuslAutomatic;//1  人工   2自动 
    private Integer type;//交易类型(需要讨论状态命名是否需要区分)
    private String cashFlowHash;//交易流水hash
}
