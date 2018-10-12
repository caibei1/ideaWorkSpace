package com.fuzamei.pojo.tongdun;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AntifraudReportTD {
	private Integer finalScore;//分数
	private String finalDecision;//拒绝还是同意REJECT或者APPROVE
	private Integer messageNum;//信息数量
	private List<RiskItemTD> riskItems;//风险条目(贷前反欺诈风险情况)
}
