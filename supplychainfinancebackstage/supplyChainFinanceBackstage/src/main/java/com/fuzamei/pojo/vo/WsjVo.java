package com.fuzamei.pojo.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WsjVo {

	
	private String enterpriseName;  //企业名称
	private Integer status; 	  //状态
	private Long startTime;			  //起始时间
	private Long endTime;			  //结束时间
	private Integer page;			  //页数
	private Integer rowNum;			  //每页显示得条数
	private Integer startPage;		  //起始页
	private Integer auth;            //权限
}
