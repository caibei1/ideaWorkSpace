package com.fuzamei.constant;
/**
 * 
 * @author ylx[yanglingxiao2009@163.com]
 * @desc 权限名常量类
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fuzamei.pojo.po.BackAuthPO;

public class Authes {
	private Authes() {
		throw new AssertionError("禁止实例化");
	}
	public static final String SHOW_ROLES = "查看账号的角色信息";//普通管理员的(1)
	public static final String ASSIGN_ROLES = "分配角色";//普通管理员的(2)
	public static final String QUERY_ROLES = "查询角色信息";//普通管理员的(3)
	public static final String CREATE_ROLE = "创建角色";//普通管理员的(4)
	public static final String UPDATE_ROLE = "修改角色";//普通管理员的(5)
	public static final String DELETE_ROLE = "删除角色";//普通管理员的(6)
	public static final String SHOW_AUTHES = "查看角色的权限信息";//普通管理员的(7)
	public static final String ASSIGN_AUTHES = "分配权限";//普通管理员的(8)
	public static final String QUERY_AUTHES = "查询权限信息";//普通管理员的(9)
	public static final String CREATE_AUTH = "创建权限";//普通管理员的(10)
	public static final String UPDATE_AUTH = "修改权限";//普通管理员的(11)
	public static final String DELETE_AUTH = "删除权限";//普通管理员的(12)
	public static final String DELETE_ACCOUNTS = "删除账号";//普通管理员的(13)
	public static final String CREATE_ACCOUNT = "创建账号";//普通管理员的(14)
	public static final String UPDATE_ACCOUNT = "修改账号";//普通管理员的(15)
	public static final String QUERY_ACCOUNTS = "查询账号信息";//普通管理员的(16)
	
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
	public static final String FINANCIAL_MANAGE="财务管理出金入金";
	public static final String FINANCIAL_DIRECTOR="财务主管管理出金";//后加的
	public static final String DOWNLOAD_CARD_PICTURE = "市场人员下载附件身份证图片";
	
	public static final List<String> ALL_AUTHES = new ArrayList<String>();
	static{
		ALL_AUTHES.add(QUERY_UNVERIFIED);
		ALL_AUTHES.add(QUERY_VERIFIED);
		ALL_AUTHES.add(DOWNLOAD_ATTACH);
		ALL_AUTHES.add(SET_CREDITLINE);
		ALL_AUTHES.add(CREATE_ACCOUNT);
		ALL_AUTHES.add(UPDATE_ACCOUNT);
		ALL_AUTHES.add(QUERY_ACCOUNTS);
		ALL_AUTHES.add(DELETE_ACCOUNTS);
		ALL_AUTHES.add(SHOW_ROLES);
		ALL_AUTHES.add(ASSIGN_ROLES);
		ALL_AUTHES.add(QUERY_ROLES);
		ALL_AUTHES.add(CREATE_ROLE);
		ALL_AUTHES.add(UPDATE_ROLE);
		ALL_AUTHES.add(DELETE_ROLE);
		ALL_AUTHES.add(SHOW_AUTHES);
		ALL_AUTHES.add(ASSIGN_AUTHES);
		ALL_AUTHES.add(QUERY_AUTHES);
		ALL_AUTHES.add(CREATE_AUTH);
		ALL_AUTHES.add(UPDATE_AUTH);
		ALL_AUTHES.add(DELETE_AUTH);
		ALL_AUTHES.add(QUERY_USERMANAGE);
		ALL_AUTHES.add(FINANCIAL_MANAGE);
		ALL_AUTHES.add(DOWNLOAD_CARD_PICTURE);
		ALL_AUTHES.add(FINANCIAL_DIRECTOR);
	}
	public static final List<Integer> AUTH_INDEX = new ArrayList<Integer>();
	public static final List<BackAuthPO> BACK_AUTHES = new ArrayList<BackAuthPO>();//用于给超级管理员创建权限用
	static{
		for (int i = 0; i < ALL_AUTHES.size(); i++) {
			AUTH_INDEX.add(i+1);
			BackAuthPO backAuthPO = new BackAuthPO();
			backAuthPO.setAuthId(i+1);
			backAuthPO.setAuthName(ALL_AUTHES.get(i));
			BACK_AUTHES.add(backAuthPO);
		}
	}

}
