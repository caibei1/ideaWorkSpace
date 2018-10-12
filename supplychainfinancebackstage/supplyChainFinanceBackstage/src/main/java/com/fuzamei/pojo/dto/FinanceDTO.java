package com.fuzamei.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FinanceDTO {
	private Integer transactionId;  //交易流水编号
	private Integer operatorId;         //（操作人）
	private String  enterpriseName;     //（企业名称）
	private Integer type;               //交易类型
	private Integer  stateId;            //操作动作 1代表入金   2 代表出金
	private Double  amount;             //入金和出金的额度
	private Long    createTime;         //创建时间
	private Long    endTime;            //完成时间
	private Long    reviewTime;          //复审完成时间
	private Integer manuslAutomatic;    //人工和自动的出金和入金的类型   1人工，2自动
	private Integer status;             //状态   5待审核   6已审核
	
	private String personName;          //（后台用户表 ） 的人名（出审人员）
	private Long   bankCardId;          //银行卡号
	private Integer enterpriseId;       //企业id
	private Integer accoundId;          //账号id
	
	private String  statusName;//后台状态表得状态名称
	
}
