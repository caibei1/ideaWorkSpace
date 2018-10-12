package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @describe 对应后台的操作记录表
 */
@Setter
@Getter
@ToString
public class BackOperationHistoryPO {
	private Integer id;//主键id
	private Integer operationTypeId;//操作类型id
	private String operatorId;//操作用户id
	private Long operationTime;//操作时间
	private String hash;//操作hash
}
