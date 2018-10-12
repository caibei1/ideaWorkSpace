package com.fuzamei.pojo.tongdun;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RiskItemTD{
	private Integer riskId;//风险id号
	private String riskName;//风险名称
	private Map<String, Object> riskDetail;//风险详情
}
