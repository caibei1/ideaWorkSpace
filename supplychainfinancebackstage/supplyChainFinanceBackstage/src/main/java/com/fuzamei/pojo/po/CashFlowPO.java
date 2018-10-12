package com.fuzamei.pojo.po;

import java.util.List;

import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CashFlowPO {
	private Long   moneyFlowNo;              //交易编号
	private Integer  enterpriseId;             //企业id
	private String   enterpriseName;           //企业名称
	private Integer  operationType;   
	private Long   bankCard;    
	private Long     endTime;  
	private Long     createTime;     
	private Double   amount;
	private Integer accountId;
	private Integer  state;    
}
