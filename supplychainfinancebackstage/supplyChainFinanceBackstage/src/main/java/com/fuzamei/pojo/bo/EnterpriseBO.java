package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EnterpriseBO {
	private Integer  isVerification;  //状态    2代表未通过，1代表通过
	private Long     verificationTime;//认证日期
	private Long     startTime;		  //起始时间
	private Long     endTime;		  //结束时间
	private Integer  page;			  //页数
	private Integer  rowNum;		  //每页显示得条数
	private Integer  startPage;		  //起始页
}
