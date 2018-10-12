package com.fuzamei.constant;

public class Statuses {
	private Statuses() {
		throw new AssertionError("禁止实例化");
	}
	public static final Integer UNVERIFIED = 0;//未审核
	public static final Integer APPROVED = 1;//已通过
	public static final Integer REJECTED = 2;//未通过
	
	public static final Integer CONSENT = 3;//同意
	public static final Integer REFUSE = 4;//拒绝
	public static final Integer  CHECK_NO = 5;//待审核
	public static final Integer  CHECK_OK = 6;//已审核
	public static final Integer  CHECK_BEGIN_NO = 7;//待复审
	public static final Integer  CHECK_BEGIN_OK = 8;//已复审
	public static final Integer AGREED_TO_REVIEW= 9;      //已同意复审
	public static final Integer REFUSE_TO_REVIEW= 10;     //已拒绝复审
	public static final Integer  APPROVE_TRIAL=11;  //同意出审
	public static final Integer  REJECT_TRIAL=12;   //拒绝初审
	//////////////////////////////////////////////////////////////
	
	
	
	
}
