package com.fuzamei.pojo.tongdun;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalInfoTD {
	private String enterpriseName;//借款企业名称
	private String personName;//借款人姓名
	private String phoneNum;//手机号
	private String idCardNum;//身份证号
}
