package com.fuzamei.mapper;

import java.util.List;

import com.fuzamei.pojo.po.BankMoneyFlowPO;
import com.fuzamei.pojo.vo.BankMoneyFlowVO;


public interface BankMoneyFlowMapper {

	Integer insert(BankMoneyFlowPO bankMoneyFlowPO);

	Integer countByBankTransactionNo(String bankTransactionNo);

	List<BankMoneyFlowPO> selectByEnterpriseIdAndOperationType(
			BankMoneyFlowVO bankMoneyFlowVO);
	
	String getPublicKey(Integer enterpriseId);

}
