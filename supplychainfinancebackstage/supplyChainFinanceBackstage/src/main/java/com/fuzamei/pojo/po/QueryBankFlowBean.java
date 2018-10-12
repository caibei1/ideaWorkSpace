package com.fuzamei.pojo.po;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class QueryBankFlowBean {//此类只针对出金对接接口
	private String accountid;    //大账户id
	private String flag ;        //0代表转出
	private List<String> tranflows;  //流水编号
}
