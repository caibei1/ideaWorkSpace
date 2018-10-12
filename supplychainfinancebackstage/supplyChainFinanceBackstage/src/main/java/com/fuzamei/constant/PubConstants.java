package com.fuzamei.constant;

public class PubConstants {
	
	public interface BankMoneyFlowOperationType{
		/** 入金  **/
		final Integer  RECHARGE = 1;
		/** 出金  **/
		final Integer  WITHDRAWAL = 2;

	}
	
	public interface BankMoneyFlowState{
		/** 待审核  **/
		final Integer CHECK_PENDING= 1;
		/** 审核通过  **/
		final Integer CHECKED = 2;
		/** 已拒绝  **/
		final Integer  REFUSED = 3;
		/** 坏账  **/
		final Integer  NO_REGISTER_BANK_CARD = 4;
		/** 待划拨  **/
		final Integer  HUAPO_PENDING = 11;
		
	}
	public interface BankMoneyFlowManuslAutomatic{
		/** 人工  **/
		final Integer ARTIFICIAL = 1;
		/** 自动  **/
		final Integer AUTO = 2;
	}
	
	public interface BankMoneyFlowFlag{
		/** 入金  **/
		final String RECHARGE = "1";
		/** 出金  **/
		final String WITHDRAWAL = "0";
	}
}
