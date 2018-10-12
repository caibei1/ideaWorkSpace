package com.fuzamei.pojo.po;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EnterprisePO {//企业表
	private Integer  enterpriseId;    //企业id
	private String   enterpriseName;  //企业名称
	private String   legalPersonName; //法人姓名
	private Integer  idCard;          //法人身份证
	private String   random;          //随机数
	private String   idType;          //证件类型
	private String   idNum;           //证件号码
	private Integer  isVerification;  //状态    2代表未通过，1代表通过
	private Long     verificationTime;//认证日期
	private String   officeAddress;   //办公地址
	private String   license;         //营业执照号
	private Double creditLine;//授信额度
	private Long authorizeTime;//审定时间
	private Double consumedLoan;//已使用额度
	private Double totalRepayment;//总还款额
}
