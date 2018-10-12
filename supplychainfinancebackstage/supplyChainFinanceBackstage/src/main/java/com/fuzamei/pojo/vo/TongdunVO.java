package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TongdunVO {
	private Integer enterpriseId;//企业id
	private String enterpriseName;//企业名称
	private Long reportTime;//报告时间
	private String tongdunResult;//同盾返回结果(恶心的json)
	private String prettyJson;//改良过的json
	
	private String personName;//借贷人名
	private String idCardNum;//借款人身份证号码
	private String phoneNum;//借款人手机号
}
