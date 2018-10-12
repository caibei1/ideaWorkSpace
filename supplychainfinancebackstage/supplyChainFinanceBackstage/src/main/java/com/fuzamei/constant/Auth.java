package com.fuzamei.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fuzamei.pojo.po.BackAuthPO;

/**
 * 
 * @author ylx
 * @description 针对管理员的权限信息
 */
public class Auth {
	private Auth() {
		throw new AssertionError("禁止实例化");
	}
	
	//这里16个是管理员的权限信息常量类
	public static final String CREATE_ACCOUNT = "创建账号";//普通管理员的(1)
	public static final String UPDATE_ACCOUNT = "修改账号";//普通管理员的(2)
	public static final String QUERY_ACCOUNTS = "查询账号信息";//普通管理员的(3)
	public static final String DELETE_ACCOUNTS = "删除账号";//普通管理员的(4)
	public static final String SHOW_ROLES = "查看账号的角色信息";//普通管理员的(5)
	public static final String ASSIGN_ROLES = "分配角色";//普通管理员的(6)
	public static final String QUERY_ROLES = "查询角色信息";//普通管理员的(7)
	public static final String CREATE_ROLE = "创建角色";//普通管理员的(8)*****
	public static final String UPDATE_ROLE = "修改角色";//普通管理员的(9)*****
	public static final String DELETE_ROLE = "删除角色";//普通管理员的(10)*****
	public static final String SHOW_AUTHES = "查看角色的权限信息";//普通管理员的(11)
	public static final String ASSIGN_AUTHES = "分配权限";//普通管理员的(12)
	public static final String QUERY_AUTHES = "查询权限信息";//普通管理员的(13)
	public static final String CREATE_AUTH = "创建权限";//普通管理员的(14)*****
	public static final String UPDATE_AUTH = "修改权限";//普通管理员的(15)*****
	public static final String DELETE_AUTH = "删除权限";//普通管理员的(16)*****
	
	public static final List<String> ADMIN_AUTHES = new ArrayList<String>();
	static{
		ADMIN_AUTHES.add(CREATE_ACCOUNT);
		ADMIN_AUTHES.add(UPDATE_ACCOUNT);
		ADMIN_AUTHES.add(QUERY_ACCOUNTS);
		ADMIN_AUTHES.add(DELETE_ACCOUNTS);
		ADMIN_AUTHES.add(SHOW_ROLES);
		ADMIN_AUTHES.add(ASSIGN_ROLES);
		ADMIN_AUTHES.add(QUERY_ROLES);
		ADMIN_AUTHES.add(CREATE_ROLE);
		ADMIN_AUTHES.add(UPDATE_ROLE);
		ADMIN_AUTHES.add(DELETE_ROLE);
		ADMIN_AUTHES.add(SHOW_AUTHES);
		ADMIN_AUTHES.add(ASSIGN_AUTHES);
		ADMIN_AUTHES.add(QUERY_AUTHES);
		ADMIN_AUTHES.add(CREATE_AUTH);
		ADMIN_AUTHES.add(UPDATE_AUTH);
		ADMIN_AUTHES.add(DELETE_AUTH);

		
	}
	public static final List<Integer> ADMIN_AUTH_INDEX = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,11,12,13});//管理员权限信息id号集合
	public static final List<Integer> SUPER_ADMIN_AUTH_INDEX = new ArrayList<Integer>();//超级管理员权限信息id号集合
	public static final List<BackAuthPO> BACK_ADMIN_AUTHES = new ArrayList<BackAuthPO>();//用于给超级管理员或者管理员创建权限用
	static{
		for (int i = 0; i < ADMIN_AUTHES.size(); i++) {
			SUPER_ADMIN_AUTH_INDEX.add(i+1);
			BackAuthPO backAuthPO = new BackAuthPO();
			backAuthPO.setAuthId(i+1);
			backAuthPO.setAuthName(ADMIN_AUTHES.get(i));
			BACK_ADMIN_AUTHES.add(backAuthPO);
		}
	}
	
	//以下全是普通权限信息
	public static final String QUERY_UNVERIFIED = "风控查看待审核信息";
	public static final String QUERY_VERIFIED = "风控查已待审核信息";
	public static final String DOWNLOAD_ATTACH = "风控下载附件"; 
	public static final String SET_CREDITLINE = "风控设置额度";
	public static final String QUERY_CREDITLINE = "风控查询额度";
	public static final String QUERY_BILL_ORDER ="质押单据查询";
	
	public static final String MODIFY_PWD = "修改用户个人密码";
	public static final String APPROVE_BILL = "同意应收账";
	public static final String REJECT_BILL = "拒绝应收账";
	public static final String UPLOAD_RESPOSIBLE_REPORTS = "上传尽调报告";
	public static final String DOWNLOAD_RESPOSIBLE_REPORTS = "下载尽调报告";
	public static final String QUERY_TONGDUN = "查询同盾信息";
	public static final String TONGDUN_MANAGEMENT = "同盾信息管理";
	public static final String GET_USED_MONEY = "获取已使用的额度";//用来查看企业已使用的额度
	public static final String GET_TOTAL_RETURNED_MONEY = "获取总还款额度";//用来查看企业总还款额度
	
	//chb写下面
	public static final String QUERY_USERMANAGE = "市场人员查看用户管理";
	public static final String QUERY_STATE="市场人员查询状态";
	public static final String FINANCIAL_MANAGE="财务管理出金入金";
	public static final String FINANCIAL_DIRECTOR="财务主管管理出金";//后加的
	//public static final String DOWNLOAD_CARD_PICTURE = "市场人员下载附件身份证图片";
	public static final String QUERY_IDCARD = "查询身份证图片";
	public static final String TO_BE_ALLOCATED= "财务查看待划拨列表";
	public static final String HAS_BEEN_ALLOCATED="财务查看已划拨列表"; 
	public static final String TO_BE_ALLOCATED_EXPORT="待划拨导出Excel";
	public static final String HAS_BEEN_ALLOCATED_EXPORT="已划拨导出Excel";
	public static final String AFFIRM_TRANSFER="确认划拨";
	public static final String REFUND="退款";
	public static final String GOLDEN_TO_AUDIT="财务查看入金待审核列表";
	public static final String GOLDEN_TO_GOLDEN="财务查看入金已审核列表";
	public static final String GOLDEN_TO_AUDIT_EXPORT="财务查看入金待审核导出Excel";
	public static final String GOLDEN_TO_GOLDEN_EXPORT="财务查看入金已审核导出Excel";
	public static final String GOLDEN_TO_AUDIT_AGREED="财务在入金待审核做同意操作";                   
	public static final String GOLDEN_TO_AUDIT_REFUSED="财务在入金待审核做拒绝操作";
	public static final String THE_GOLD_TO_AUDIT="财务查看出金待审核列表";
	public static final String THE_GOLD_TO_THE_APPROVED="财务查看出金已审核列表";
	public static final String THE_GOLD_TO_AUDIT_EXPORT="财务出金待审核导出Excel";
	public static final String THE_GOLD_TO_THE_APPROVE="财务出金已审核导出Excel";
	public static final String THE_GOLD_TO_AUDIT_AGREED="财务查看出金待审核做同意操作";
	public static final String THE_GOLD_TO_AUDIT_REFUSED="财务查看出金待审核做拒绝操作";
	
	public static final String THE_GOLD_TO_AUDIT_THE_REVIEW="财务主管查看待初审列表";
	public static final String THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED="财务主管查看已初审列表";
	public static final String THE_GOLD_TO_AUDIT_THE_REVIEW_AGREED="财务主管待复审里做同意操作";
	public static final String THE_GOLD_TO_AUDIT_THE_REVIEW_REFUSED="财务主管待复审里做拒绝操作";
	public static final String THE_GOLD_TO_AUDIT_THE_REVIEW_EXPORT="财务主管查看待初审里导出Excel";
	public static final String THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED_EXPORT="财务主管查看已初审里导出Excel";
	
	public static final String REFUNDED_FOR_REVIEW="财务查看退款待审核的列";
	public static final String HAS_REFUNDED_FOR_REVIEW="财务查看退款已审核的列";
	public static final String AGREED_REFUND="同意退款";
	
	
	
	
	
	
	public static final List<String> NORMAL_AUTHES = new ArrayList<String>();//普通权限信息
	static{
		NORMAL_AUTHES.add(QUERY_UNVERIFIED);
		NORMAL_AUTHES.add(QUERY_VERIFIED);
		NORMAL_AUTHES.add(DOWNLOAD_ATTACH);
		NORMAL_AUTHES.add(SET_CREDITLINE);
		NORMAL_AUTHES.add(QUERY_CREDITLINE);
		NORMAL_AUTHES.add(QUERY_BILL_ORDER);
		NORMAL_AUTHES.add(MODIFY_PWD);
		NORMAL_AUTHES.add(APPROVE_BILL);
		NORMAL_AUTHES.add(REJECT_BILL);
		NORMAL_AUTHES.add(UPLOAD_RESPOSIBLE_REPORTS);
		NORMAL_AUTHES.add(DOWNLOAD_RESPOSIBLE_REPORTS);
		NORMAL_AUTHES.add(QUERY_TONGDUN);
		NORMAL_AUTHES.add(TONGDUN_MANAGEMENT);
		NORMAL_AUTHES.add(GET_USED_MONEY);
		NORMAL_AUTHES.add(GET_TOTAL_RETURNED_MONEY);
		NORMAL_AUTHES.add(QUERY_USERMANAGE);
		NORMAL_AUTHES.add(FINANCIAL_MANAGE);
		NORMAL_AUTHES.add(FINANCIAL_DIRECTOR);
		//
		NORMAL_AUTHES.add(QUERY_STATE);
		NORMAL_AUTHES.add(QUERY_IDCARD);
		NORMAL_AUTHES.add(TO_BE_ALLOCATED);
		NORMAL_AUTHES.add(HAS_BEEN_ALLOCATED);
		NORMAL_AUTHES.add(TO_BE_ALLOCATED_EXPORT);
		NORMAL_AUTHES.add(HAS_BEEN_ALLOCATED_EXPORT);
		NORMAL_AUTHES.add(AFFIRM_TRANSFER);
		NORMAL_AUTHES.add(REFUND);
		NORMAL_AUTHES.add(GOLDEN_TO_AUDIT);
		NORMAL_AUTHES.add(GOLDEN_TO_GOLDEN);
		NORMAL_AUTHES.add(GOLDEN_TO_AUDIT_EXPORT);
		NORMAL_AUTHES.add(GOLDEN_TO_GOLDEN_EXPORT);
		NORMAL_AUTHES.add(GOLDEN_TO_AUDIT_AGREED);
		NORMAL_AUTHES.add(GOLDEN_TO_AUDIT_REFUSED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT);
		NORMAL_AUTHES.add(THE_GOLD_TO_THE_APPROVED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_EXPORT);
		NORMAL_AUTHES.add(THE_GOLD_TO_THE_APPROVE);
		NORMAL_AUTHES.add(GOLDEN_TO_AUDIT_AGREED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_REFUSED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_THE_REVIEW);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_THE_REVIEW_AGREED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_THE_REVIEW_REFUSED);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_THE_REVIEW_EXPORT);
		NORMAL_AUTHES.add(THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED_EXPORT);
		NORMAL_AUTHES.add(REFUNDED_FOR_REVIEW);
		NORMAL_AUTHES.add(HAS_REFUNDED_FOR_REVIEW);
		NORMAL_AUTHES.add(AGREED_REFUND);
	}
	
	public static final List<Integer> NORMAL_AUTHES_INDEX = new ArrayList<Integer>();//管理员权限信息id号集合
	public static final List<BackAuthPO> BACK_NORMAL_AUTHES = new ArrayList<BackAuthPO>();//用于给超级管理员或者管理员创建权限用
	static{
		for (int i = (SUPER_ADMIN_AUTH_INDEX.size()); i < (NORMAL_AUTHES.size()+SUPER_ADMIN_AUTH_INDEX.size()); i++) {
			NORMAL_AUTHES_INDEX.add(i+1);
			BackAuthPO backAuthPO = new BackAuthPO();
			backAuthPO.setAuthId(i+1);
			backAuthPO.setAuthName(NORMAL_AUTHES.get(i-SUPER_ADMIN_AUTH_INDEX.size()));
			BACK_NORMAL_AUTHES.add(backAuthPO);
		}
	}
	
	public static final List<String> ALL_AUTHES = new ArrayList<String>();//用于存放所有权限信息
	public static final List<BackAuthPO> ALL_BACK_AUTHES = new ArrayList<BackAuthPO>();//用于存放所有权限信息
	public static final List<Integer> ALL_AUTHES_INDEX = new ArrayList<Integer>();//用于存放所有权限ID信息
	static{
		ALL_BACK_AUTHES.addAll(BACK_ADMIN_AUTHES);
		ALL_BACK_AUTHES.addAll(BACK_NORMAL_AUTHES);
		ALL_AUTHES.addAll(ADMIN_AUTHES);
		ALL_AUTHES.addAll(NORMAL_AUTHES);
		ALL_AUTHES_INDEX.addAll(SUPER_ADMIN_AUTH_INDEX);
		ALL_AUTHES_INDEX.addAll(NORMAL_AUTHES_INDEX);
	}
	
}
