package com.fuzamei.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EnterpriseDTO {
	private Integer enterpriseId;//企业id
	private String   enterpriseName;  //企业名称
	private String   legalPersonName; //法人姓名
	private String   idCard;          //法人身份证
	private Integer  isVerification;  //状态    2代表未通过，1代表通过
	private Long     verificationTime;//认证日期
	private Long     authorizeTime;   //(改成这个认证日期字段了)认证日期
	private String   officeAddress;   //办公地址
	private String   license;         //营业执照号
	
	private String   phone;           //用户手机号
	private String   platformtoken;   //认证token   //前台用户表的 platformtoken
	private String   idCardNumber;    //(前台用户表)用户注册的身份证号
	private Integer  tag;             //1.代表图片 2.代表是视频
	private Integer  type;            //1.身份证正面  2.代表身份证背面
	
	private Long startTime;			  //起始时间
	private Long endTime;			  //结束时间
	private Integer page;			  //页数
	private Integer rowNum;			  //每页显示得条数
	private Integer startPage;		  //起始页
	
	//wsj加的  用于查询入金出金待审核或已审核
	
	/**
	 * 入金待审核  1/13
	 * 入金已审核    2/3/19
	 * 
	 * 出金待审核  1
	 * 出金初审通过或者待复审  2
	 * 出金复审通过   7
	 * 
	 * 出账成功   9
	 * 出账失败  10
	 */
	private List<Integer> states;
	private Integer operationType;       //1代表入金    2代表出金 
	private Integer auth;       //权限
	private List<String> excelHeader;//excel的第一行
}
