package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TongdunBO {
	private Integer page;
	private Integer rowNum;
	private Integer startPage;
	private Integer receivedEnterpriseId;//收款企业id(同盾查询用)
	private String receivedEnterpriseName;//收款企业名称(同盾结果管理里面查询企业名称用)
	private String prettyJson;//用来插入改良过的同盾json值
}
