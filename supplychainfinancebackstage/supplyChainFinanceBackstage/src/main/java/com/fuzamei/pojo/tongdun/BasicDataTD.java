package com.fuzamei.pojo.tongdun;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicDataTD {
	private Integer taxBlackInfo;//税务负面信息查询调用结果数量(这里应该算条数taxneg_message和taxneg_consistence)
	private Map<String, Object> taxBlackInfoMessage;//税务负面信息详情
	
	private Integer companyQuery;//企业工商查询调用结果(这里应该算条数ent_biz_message和ent_biz_consistence)
	private Map<String, Object> companyQueryMessage;//企业工商信息详情
	
	private Integer annualReport;//企业年报调用结果(这里应该算条数ent_report_consistence和ent_report_message)
	private Map<String, Object> annualReportMessage;//税务负面信息详情
	
	private Integer courtQueryInfo;//企业涉诉综合查询调用结果(这里应该算条数qysszhcx_consistence和qysszhcx_message)
	private Map<String, Object> courtQueryInfoMessage;//税务负面信息详情
	
}
