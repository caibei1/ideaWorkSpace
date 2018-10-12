package com.fuzamei.constant;

public class States {
	private States() {
		throw new AssertionError("禁止实例化");
	}
	public static final Integer  CHECK_NO = 1;          //待审核  （出，入金的）
	public static final Integer  GOLDEN_PASS=2;         //入金通过
	public static final Integer  GOLDEN_REJECT=3;       //入金拒绝
	public static final Integer  HEIZHANG=4;            //黑账       暂不考虑
	public static final Integer  FIRST_PASS=5;          //初审通过      or  待复审审核
	public static final Integer  FIRST_REJECT=6;        //初审拒绝      
	public static final Integer  SECOND_PASS=7;         //复审通过      or
	public static final Integer  SECOND_REJECT=8;       //复审拒绝
	public static final Integer  OUT_ACCOUNT_SUCCEED=9; //出账成功
	public static final Integer  OUT_ACCOUNT_DEFEAT=10; //出账失败
	
	public static final Integer   AWAIT_TRANSFERA_AMOUNT=11;  //待划拨   后加的
	public static final Integer   REFUNDED_AMOUNT=12;         //已退款   后加的ed
	public static final Integer   TRANSFERA_AMOUNT=13;        //已划拨   后加的
	public static final Integer   A_REFUND=14;                //退款中   后加的ing
	public static final Integer   PROCESSED=15;               //等待处理  新加的ing
	public static final Integer   INTHEPROCESSING=19;         //处理中 新加的ing
	public static final Integer  REFUND_SUCCEED=20;           //退款成功
	public static final Integer   DEFEATED=16;                //退款失败  新加的
	public static final Integer  CASHFLOW_SUCCED=17;          //宋东洋用的    成功
	public static final Integer  CASHFLOW_DEFEATED=18;        //宋东洋用的    失败 
	
	
}
