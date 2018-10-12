package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterpriseVO {
	// ========================企业信息=======================
	/**
	 * 用户id#########
	 */
	private Integer uid;
	/**
	 * 企业id#########
	 */
	private Integer enterpriseId;
	/**
	 * 企业名称#########
	 */
	private String enterpriseName;	//企业名称
	/**
	 * 法人姓名#########
	 */
	private String legalPersonName;
	/**
	 * 省份证#########
	 */
	private String idCard;
	/**
	 * 企业对应的random
	 */
	private String random;//企业对应的随机数
	/**
	 * 企业证件类型#########
	 */
	private String idType;
	// =========================enterprise_account======================
	/**
	 * 账户id
	 */
	private  Integer accountId;
	// ========================BankCard=======================
	/**
	 * 银行卡id
	 */
	private Integer cardId;
	/**
	 * 企业的账户名称(银行账号)#########初步：某一个账户下面的某一张银行卡卡号
	 */
	private String cardNumber;	//企业银行账号
	/**
	 * 账户的总资产#########
	 */
	private double enterpriseTotalAsset;
	
	// ========================证书信息 =======================	
	private String publicKey;	//签名证书公钥
	private String dn;
	private String serialNo;


    /**
     * 状态（新加的）
     * is_verification
     */
    private Integer isVerification;

    /**
     * 认证日期（新加的）
     * verification_time
     */
    private Long verificationTime;

    /**
     * 办公地址（新加的）
     * office_address
     */
    private String officeAddress;

    /**
     * 营业执照号码（新加的）
     * license
     */
    private String license;

	
	
}
