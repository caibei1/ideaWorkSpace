package com.fuzamei.pojo.tongdun;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TongdunResultTD {
	private String id;//保镖id
	private Long reportTime;//报告时间
	private PersonalInfoTD personalInfoTD;//个人信息对象(模块)
	private BasicDataTD basicDataTD;//基本数据对象(模块)
	private AntifraudReportTD antifraudReportTD;//反欺诈报告对象(模块)
}
