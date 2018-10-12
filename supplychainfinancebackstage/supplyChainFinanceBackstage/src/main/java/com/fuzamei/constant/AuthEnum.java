package com.fuzamei.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AuthEnum {
	
	CREATE_ACCOUNT(1,"创建账号","/identityManagement/account/createNewAccount"),////普通管理员的(1)
	UPDATE_ACCOUNT(2,"修改账号","/identityManagement/account/updateAccountInfo"),//普通管理员的(2)
	QUERY_ACCOUNTS(3,"查询账号信息","/identityManagement/account/queryAccountInfo"),//普通管理员的(3)
	DELETE_ACCOUNTS(4,"删除账号","/identityManagement/account/deleteAccount"),//普通管理员的(4)
	SHOW_ROLES(5,"查看账号的角色信息","/identityManagement/account/showRoleInfoUnderAccount"),//普通管理员的(5)
	ASSIGN_ROLES(6,"分配角色","/identityManagement/account/assignRoles4Account"),//普通管理员的(6)
	QUERY_ROLES(7,"查询角色信息","/identityManagement/role/queryRoleInfo"),//普通管理员的(7)
	CREATE_ROLE(8,"创建角色","/identityManagement/role/createNewRole"),//超级管理员的(8)*****
	UPDATE_ROLE(9,"修改角色","/identityManagement/role/updateRoleInfo"),//超级管理员的(9)*****
	DELETE_ROLE(10,"删除角色","/identityManagement/role/deleteRole"),//超级管理员的(10)*****
	SHOW_AUTHES(11,"查看角色的权限信息","/identityManagement/role/showAuthInfoUnderRole"),//普通管理员的(11)
	ASSIGN_AUTHES(12,"分配权限","/identityManagement/role/assignAuthes4Role"),//普通管理员的(12)
	QUERY_AUTHES(13,"查询权限信息","/identityManagement/auth/queryAuthInfo"),//普通管理员的(13)
	CREATE_AUTH(14,"创建权限","/identityManagement/auth/createNewAuth"),//超级管理员的(14)*****
	UPDATE_AUTH(15,"修改权限","/identityManagement/auth/updateAuthInfo"),//超级管理员的(15)*****
	DELETE_AUTH(16,"删除权限","/identityManagement/auth/deleteAuth"),//超级管理员的(16)*****
	/*==================================以上为超级管理员和普通管理员核心权限================================================================*/
	
	
	/*==================================以下为普通业务人员的业务权限=======================================================================*/
	//风控权限模块
	QUERY_UNVERIFIED(17,"风控查看待审核信息","/ventureManagement/firstTrial/queryToBeVerified"),
	QUERY_LEFTOVERLOAN(60,"查询企业剩余额度","/ventureManagement/firstTrial/queryLeftoverLoan"),
	QUERY_VERIFIED(18,"风控查看已待审核信息","/ventureManagement/firstTrial/queryVerified"),
	SET_CREDITLINE(19,"风控设置企业授信额度","/ventureManagement/creditLineManagement/setEnterpriseCreditLine"),
	QUERY_CREDITLINE(20,"风控查询额度","/ventureManagement/creditLineManagement/queryEnterpriseCreditLine"),
	QUERY_BILL_ORDER(21,"质押单据查询","/ventureManagement/lockupAndFinancingManagement/queryBillOrder"),
	APPROVE_BILL(22,"同意应收账","/ventureManagement/firstTrial/approveBill"),
	REJECT_BILL(23,"拒绝应收账","/ventureManagement/firstTrial/rejectBill"),
	UPLOAD_RESPOSIBLE_REPORTS(24,"上传尽调报告","/ventureManagement/firstTrial/uploadResponsibleReports"),
	DOWNLOAD_RESPOSIBLE_REPORTS(25,"下载尽调报告","/ventureManagement/firstTrial/downloadResponsibleReports"),
	QUERY_TONGDUN(26,"查询同盾信息","/ventureManagement/firstTrial/queryTongdunResult"),
	TONGDUN_MANAGEMENT(27,"同盾信息管理","/ventureManagement/tongdunResultManagement/queryAllEnterpriseTongdunInfo"),
	//用户中心模块
	MODIFY_PWD(28,"修改用户个人密码","/userCenter/modifyPassword"),
	//用户管理模块
	QUERY_USERMANAGE(29,"市场人员查看用户管理","/userManagement/queryUserManagement"),
	QUERY_STATE(30,"市场人员查询状态","/userManagement/queryAllStatus"),
	QUERY_IDCARD(31,"查询身份证图片","/userManagement/queryIdentityCard"),
	
	
	//吴少杰用户管理模块
	WSJ_QUERY_USERMANAGE(29,"市场人员查看用户管理","/wsj/findEnterprise"),	
	WSJ_GOLDEN_TO_AUDIT(38,"财务查看入金待审核列表","/wsj/inGold/queryInGold"),
	WSJ_GOLDEN_TO_AUDIT_AGREED(42,"财务在入金待审核做同意操作","/wsj/inGold/inGold"),//
	WSJ_GOLDEN_TO_AUDIT_REFUSED(43,"财务在入金待审核做拒绝操作","/wsj/inGold/refuseInGold"),//
	WSJ_THE_GOLD_TO_THE_APPROVED(45,"财务查看出金已审核列表","/financeManage/outGold/queryOutGoldOkCheck"),//
	WSJ_THE_GOLD_TO_AUDIT_AGREED(48,"财务查看出金待审核做同意操作","/financeManage/outGold/approvebankMoneyFlowOutGolden"),//
	WSJ_THE_GOLD_TO_AUDIT_REFUSED(49,"财务查看出金待审核做拒绝操作","/financeManage/outGold/rejectbankMoneyFlowOutGolden"),//
	WSJ_THE_GOLD_TO_AUDIT_THE_REVIEW(50,"财务主管查看待初审列表","/financeManage/outGoldRecheck/queryToReview"),//
	WSJ_THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED(51,"财务主管查看已初审列表","/financeManage/outGoldRecheck/queryHaveReview"),//
	WSJ_THE_GOLD_TO_AUDIT_THE_REVIEW_AGREED(52,"财务主管待复审里做同意操作","/financeManage/outGoldRecheck/approvebankMoneyFlowOutGoldenToReview"),//
	WSJ_THE_GOLD_TO_AUDIT_THE_REVIEW_REFUSED(53,"财务主管待复审里做拒绝操作","/financeManage/outGoldRecheck/rejectbankMoneyFlowOutGoldenToReview"),//
	
	//财务管理模块
	TO_BE_ALLOCATED(32,"财务查看待划拨列表","/financeManage/goldenRecord/queryAwaitTransfera"),//
	HAS_BEEN_ALLOCATED(33,"财务查看已划拨列表","/financeManage/goldenRecord/queryHasBeenAllocated"),//
	TO_BE_ALLOCATED_EXPORT(34,"待划拨导出Excel","/financeManage/goldenRecord/selectAwaitTransfera"),//
	HAS_BEEN_ALLOCATED_EXPORT(35,"已划拨导出Excel","/financeManage/goldenRecord/selectHasBeenAllocated"),//
	AFFIRM_TRANSFER(36,"确认划拨","/financeManage/goldenRecord/awaitTransfera"),//
	REFUND(37,"退款","/financeManage/goldenRecord/hasBeenAllocated"),//
	GOLDEN_TO_AUDIT(38,"财务查看入金待审核列表","/financeManage/goldenAudit/queryGolden"),//
	GOLDEN_TO_GOLDEN(39,"财务查看入金已审核列表","/financeManage/goldenAudit/queryGoldenOkCheck"),//
	GOLDEN_TO_AUDIT_EXPORT(40,"财务查看入金待审核导出Excel","/financeManage/goldenAudit/selectGoldenExportExcel"),//
	GOLDEN_TO_GOLDEN_EXPORT(41,"财务查看入金已审核导出Excel","/financeManage/goldenAudit/selectGoldenExportExcelCheckOk"),//
	GOLDEN_TO_AUDIT_AGREED(42,"财务在入金待审核做同意操作","/financeManage/goldenAudit/approvebankMoneyFlow"),//
	GOLDEN_TO_AUDIT_REFUSED(43,"财务在入金待审核做拒绝操作","/financeManage/goldenAudit/rejectbankMoneyFlow"),//
	THE_GOLD_TO_AUDIT(44,"财务查看出金待审核列表","/financeManage/outGold/queryOutGold"),//
	THE_GOLD_TO_THE_APPROVED(45,"财务查看出金已审核列表","/financeManage/outGold/queryOutGoldOkCheck"),//
	THE_GOLD_TO_AUDIT_EXPORT(46,"财务出金待审核导出Excel","/financeManage/outGold/selectGoldenOutExportExcel"),//
	THE_GOLD_TO_THE_APPROVE(47,"财务出金已审核导出Excel","/financeManage/outGold/selectGoldenOutExportExcelOkCheck2"),//
	THE_GOLD_TO_AUDIT_AGREED(48,"财务查看出金待审核做同意操作","/financeManage/outGold/approvebankMoneyFlowOutGolden"),//
	THE_GOLD_TO_AUDIT_REFUSED(49,"财务查看出金待审核做拒绝操作","/financeManage/outGold/rejectbankMoneyFlowOutGolden"),//
	THE_GOLD_TO_AUDIT_THE_REVIEW(50,"财务主管查看待初审列表","/financeManage/outGoldRecheck/queryToReview"),//
	THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED(51,"财务主管查看已初审列表","/financeManage/outGoldRecheck/queryHaveReview"),//
	THE_GOLD_TO_AUDIT_THE_REVIEW_AGREED(52,"财务主管待复审里做同意操作","/financeManage/outGoldRecheck/approvebankMoneyFlowOutGoldenToReview"),//
	THE_GOLD_TO_AUDIT_THE_REVIEW_REFUSED(53,"财务主管待复审里做拒绝操作","/financeManage/outGoldRecheck/rejectbankMoneyFlowOutGoldenToReview"),//
	THE_GOLD_TO_AUDIT_THE_REVIEW_EXPORT(54,"财务主管查看待初审里导出Excel","/financeManage/outGoldRecheck/selectOutGlodenToReview"),//
	THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED_EXPORT(55,"财务主管查看已初审里导出Excel","/financeManage/outGoldRecheck/selectOutGlodenToReviewOkCheck"),//
	REFUNDED_FOR_REVIEW(56,"财务查看退款待审核的列","/financeManage/recordRefundAudit/queryToAuditARefund"),//
	HAS_REFUNDED_FOR_REVIEW(57,"财务查看退款已审核的列","/financeManage/recordRefundAudit/queryhasARefund"),//
	AGREED_REFUND(58,"同意退款","/financeManage/recordRefundAudit/approveArefund"),//
	//历史记录查看
	QUERY_HISTORY(59,"查看区块链历史记录","/blockChainHistory/queryHistory");
	
	
	
	public static final List<Integer> ADMIN_AUTH_INDEX = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,11,12,13,28,59});//普通管理员权限信息id号集合(还要分配查看区块链的权限和修改密码的权限)
	public static final List<Integer> SUPER_ADMIN_AUTH_INDEX = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});//超级管理员权限信息id号集合
	public static final List<Integer> ALL_AUTHES_INDEX = new ArrayList<Integer>();//所有权限id号集合
	static{  
		for (int i = 1; i <= 60; i++) {
			ALL_AUTHES_INDEX.add(i);
		}
	}
	public static final List<String> ALL_AUTHURL = new ArrayList<String>();
	static{
		AuthEnum[] authurls = AuthEnum.values();
		for (int i = 0; i < authurls.length; i++) {
			ALL_AUTHURL.add(authurls[i].getAuthUrl());
		}
	}
	
	
	/*
	 * 19------对应RsSetCredit = 2; //设置企业信用总额度
	 * 22------对应RsExamineAsset = 1; //审核资产录入
	 * 42------对应RsExamineDeposit = 4; //审核企业入金
	 * 48------对应RsExamineWithdraw = 8; //企业出金初审
	 * 52------对应RsReviewWithdraw = 16; //企业出金复审
	 */
	public static final List<Integer> BLOCKCHIAN_AUTHES = Arrays.asList(new Integer[]{19,22,42,48,52});//上链的权限id
	
	
	
	private AuthEnum(Integer authId,String authName,String authUrl){
		this.authId=authId;
		this.authName=authName;
		this.authUrl=authUrl;
	}
	private Integer authId;
	private String authName;
	private String authUrl;
	public Integer getAuthId() {
		return authId;
	}
	public void setAuthId(Integer authId) {
		this.authId = authId;
	}
	public String getAuthName() {
		return authName;
	}
	public void setAuthName(String authName) {
		this.authName = authName;
	}
	public String getAuthUrl() {
		return authUrl;
	}
	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}
	
	
}
