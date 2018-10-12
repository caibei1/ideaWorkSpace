package com.fuzamei.service;

import java.util.List;

import com.fuzamei.pojo.bean.BankFlowResultBean;
import com.fuzamei.pojo.vo.BankMoneyFlowVO;

public interface BankMoneyFlowService {
	public Integer insert(BankMoneyFlowVO bankMoneyFlowVO);
	
	public List<String> addBankRecharge(BankFlowResultBean bankFlowResultBean);
	
	public int countByBankTransactionNo(String bankTransactionNo);
}
