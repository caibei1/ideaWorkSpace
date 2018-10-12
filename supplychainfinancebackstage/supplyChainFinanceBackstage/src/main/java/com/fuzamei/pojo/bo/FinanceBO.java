package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FinanceBO {
	private String   enterpriseName;  //企业名称
	
	private Long startTime;			  //起始时间
	private Long endTime;			  //结束时间
	private Integer page;			  //页数
	private Integer rowNum;			  //每页显示得条数
	private Integer startPage;		  //起始页
	
	private Integer status;//状态id，，1表示已通过，2表示已拒绝 5未审核 6 已审核
	private Integer state;

	
	
	
}
