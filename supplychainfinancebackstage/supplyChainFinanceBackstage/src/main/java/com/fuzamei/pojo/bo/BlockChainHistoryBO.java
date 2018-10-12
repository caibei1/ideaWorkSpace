package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BlockChainHistoryBO {
	private Integer page;
	private Integer rowNum;
	private Integer startPage;
	private Long startTime;
	private Long endTime;
	private Integer operatorId;//操作人id号
	private Integer operationTypeId;//操作类型id
	private String operationType;//操作类型
	private Long hash;//操作hash
	private Long createTime;//创建时间
}
