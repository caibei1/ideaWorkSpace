package com.fuzamei.pojo.vo;

import lombok.ToString;

import lombok.Getter;

import lombok.Setter;

@Setter
@Getter
@ToString
public class BlockChainHistoryVO {
	private String operator;//操作人
	private String operationType;//操作类型
	private Long createTime;//创建时间
}
