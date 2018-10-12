package com.fuzamei.pojo.bo;

import com.fuzamei.pojo.BasePojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BillOrderBO extends BasePojo{
	private Integer status;//状态id
	private String payedEnterprise;//付款企业
}
