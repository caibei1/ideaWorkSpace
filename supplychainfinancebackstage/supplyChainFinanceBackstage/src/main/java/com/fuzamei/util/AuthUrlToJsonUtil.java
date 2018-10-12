package com.fuzamei.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.fuzamei.constant.AuthEnum;
import com.fuzamei.pojo.authvo.Account;
import com.fuzamei.pojo.authvo.Auth;
import com.fuzamei.pojo.authvo.BlockChainHistory;
import com.fuzamei.pojo.authvo.CreditLineManagement;
import com.fuzamei.pojo.authvo.FinanceManage;
import com.fuzamei.pojo.authvo.FirstTrial;
import com.fuzamei.pojo.authvo.GoldenAudit;
import com.fuzamei.pojo.authvo.GoldenRecord;
import com.fuzamei.pojo.authvo.IdentityManagement;
import com.fuzamei.pojo.authvo.LockupAndFinancingManagement;
import com.fuzamei.pojo.authvo.OutGold;
import com.fuzamei.pojo.authvo.OutGoldRecheck;
import com.fuzamei.pojo.authvo.RecordRefundAudit;
import com.fuzamei.pojo.authvo.Role;
import com.fuzamei.pojo.authvo.TongdunResultManagement;
import com.fuzamei.pojo.authvo.UserCenter;
import com.fuzamei.pojo.authvo.UserManagement;
import com.fuzamei.pojo.authvo.VentureManagement;
import com.mchange.util.AssertException;

public class AuthUrlToJsonUtil {
	
	private AuthUrlToJsonUtil(){
		throw new AssertException("instaniation is not allowed");
	}
	
	
	public static Map<String, Object> authUrlToJson(List<String> authUrls){
		IdentityManagement identityManagement = new IdentityManagement();
		Account account = new Account();
		Set<String> accountAuthes = new HashSet<String>();
		Role role = new Role();
		Set<String> roleAuthes = new HashSet<String>();
		Auth auth = new Auth();
		Set<String> authAuthes = new HashSet<String>();
		
		VentureManagement ventureManagement = new VentureManagement();
		FirstTrial firstTrial = new FirstTrial();
		Set<String> firstTrialAuthes = new HashSet<String>();
		
		LockupAndFinancingManagement lockupAndFinancingManagement = new LockupAndFinancingManagement();
		Set<String> lockupAndFinancingManagementAuthes = new HashSet<String>();
		
		CreditLineManagement creditLineManagement = new CreditLineManagement();
		Set<String> creditLineManagementAuthes = new HashSet<String>();
		
		TongdunResultManagement tongdunResultManagement = new TongdunResultManagement();
		Set<String> tongdunResultManagementAuthes = new HashSet<String>();
		
		FinanceManage financeManage = new FinanceManage();
		GoldenAudit goldenAudit = new GoldenAudit();
		Set<String> goldenAuditAuthes = new HashSet<String>();
		
		GoldenRecord goldenRecord = new GoldenRecord();
		Set<String> goldenRecordAuthes = new HashSet<String>();
		
		OutGold outGold = new OutGold();
		Set<String> outGoldAuthes = new HashSet<String>();
		
		OutGoldRecheck outGoldRecheck = new OutGoldRecheck();
		Set<String> outGoldRecheckAuthes = new HashSet<String>();
		
		RecordRefundAudit recordRefundAudit = new RecordRefundAudit();
		Set<String> recordRefundAuditAuthes = new HashSet<String>();
		
		UserCenter userCenter = new UserCenter();
		Set<String> userCenterAuthes = new HashSet<String>();
		
		BlockChainHistory blockChainHistory = new BlockChainHistory();
		Set<String> blockChainHistoryAuthes = new HashSet<String>();
		
		UserManagement userManagement = new UserManagement();
		Set<String> userManagementAuthes = new HashSet<String>();
		
		Map<String, Object> map = new LinkedHashMap<>();
		for (String authUrl : authUrls) {
			String[] split = authUrl.split("/");
			if(split.length==4){//三级目录
				String level1 = split[1];
				String level2 = split[2];
				String level3 = split[3];
				if(level1.equals("identityManagement")){
					identityManagement.setIfHave(true);
					if(level2.equals("account")){
						account.setIfHave(true);
						accountAuthes.add(level3);
					}
					if(level2.equals("role")){
						role.setIfHave(true);
						roleAuthes.add(level3);
					}
					if(level2.equals("auth")){
						auth.setIfHave(true);
						authAuthes.add(level3);
					}
				}
				if(level1.equals("ventureManagement")){
					ventureManagement.setIfHave(true);
					if(level2.equals("firstTrial")){
						firstTrial.setIfHave(true);
						firstTrialAuthes.add(level3);
					}
					if(level2.equals("lockupAndFinancingManagement")){
						lockupAndFinancingManagement.setIfHave(true);
						lockupAndFinancingManagementAuthes.add(level3);
					}
					if(level2.equals("creditLineManagement")){
						creditLineManagement.setIfHave(true);
						creditLineManagementAuthes.add(level3);
					}
					if(level2.equals("tongdunResultManagement")){
						tongdunResultManagement.setIfHave(true);
						tongdunResultManagementAuthes.add(level3);
					}
				}
				if(level1.equals("financeManage")){
					financeManage.setIfHave(true);
					if(level2.equals("goldenAudit")){
						goldenAudit.setIfHave(true);
						goldenAuditAuthes.add(level3);
					}
					if(level2.equals("goldenRecord")){
						goldenRecord.setIfHave(true);
						goldenRecordAuthes.add(level3);
					}
					if(level2.equals("outGold")){
						outGold.setIfHave(true);
						outGoldAuthes.add(level3);
					}
					if(level2.equals("outGoldRecheck")){
						outGoldRecheck.setIfHave(true);
						outGoldRecheckAuthes.add(level3);
					}
					if(level2.equals("recordRefundAudit")){
						recordRefundAudit.setIfHave(true);
						recordRefundAuditAuthes.add(level3);
					}
				}
			}
			if(split.length==3){//二级目录
				String level1 = split[1];
				String level2 = split[2];
				if(level1.equals("userCenter")){
					userCenter.setIfHave(true);
					userCenterAuthes.add(level2);
				}
				if(level1.equals("userManagement")){
					userManagement.setIfHave(true);
					userManagementAuthes.add(level2);
				}
				if(level1.equals("blockChainHistory")){
					blockChainHistory.setIfHave(true);
					blockChainHistoryAuthes.add(level2);
				}
			}
		}
		
		if(identityManagement.isIfHave()){//身份管理
			Set<Object> set = new HashSet<Object>();
			if(account.isIfHave()){
				set.add(account);
				account.setAuthes(accountAuthes);
			}else{
				set.add(account);
			}
			if(role.isIfHave()){
				set.add(role);
				role.setAuthes(roleAuthes);
			}else{
				set.add(role);
			}
			if(auth.isIfHave()){
				set.add(auth);
				auth.setAuthes(authAuthes);
			}else{
				set.add(auth);
			}
			identityManagement.setArrays(set);
			map.put("identityManagement", identityManagement);
		}else{
			map.put("identityManagement", identityManagement);
		}
		if(ventureManagement.isIfHave()){//风控管理
			Set<Object> set = new HashSet<Object>();
			if(firstTrial.isIfHave()){
				set.add(firstTrial);
				firstTrial.setAuthes(firstTrialAuthes);
			}else{
				set.add(firstTrial);
			}
			if(lockupAndFinancingManagement.isIfHave()){
				set.add(lockupAndFinancingManagement);
				lockupAndFinancingManagement.setAuthes(lockupAndFinancingManagementAuthes);
			}else{
				set.add(lockupAndFinancingManagement);
			}
			if(creditLineManagement.isIfHave()){
				set.add(creditLineManagement);
				creditLineManagement.setAuthes(creditLineManagementAuthes);
			}else{
				set.add(creditLineManagement);
			}
			if(tongdunResultManagement.isIfHave()){
				set.add(tongdunResultManagement);
				tongdunResultManagement.setAuthes(tongdunResultManagementAuthes);
			}else{
				set.add(tongdunResultManagement);
			}
			ventureManagement.setArrays(set);
			map.put("ventureManagement", ventureManagement);
		}else{
			map.put("ventureManagement", ventureManagement);
		}
		if(financeManage.isIfHave()){//财务管理
			Set<Object> set = new HashSet<Object>();
			if(goldenAudit.isIfHave()){
				set.add(goldenAudit);
				goldenAudit.setAuthes(goldenAuditAuthes);
			}else{
				set.add(goldenAudit);
			}
			if(goldenRecord.isIfHave()){
				set.add(goldenRecord);
				goldenRecord.setAuthes(goldenRecordAuthes);
			}else{
				set.add(goldenRecord);
			}
			if(outGold.isIfHave()){
				set.add(outGold);
				outGold.setAuthes(outGoldAuthes);
			}else{
				set.add(outGold);
			}
			if(outGoldRecheck.isIfHave()){
				set.add(outGoldRecheck);
				outGoldRecheck.setAuthes(outGoldRecheckAuthes);
			}else{
				set.add(outGoldRecheck);
			}
			if(recordRefundAudit.isIfHave()){
				set.add(recordRefundAudit);
				recordRefundAudit.setAuthes(recordRefundAuditAuthes);
			}else{
				set.add(recordRefundAudit);
			}
			financeManage.setArrays(set);
			map.put("financeManage", financeManage);
		}else{
			map.put("financeManage", financeManage);
		}
		if(userCenter.isIfHave()){//用户中心
			userCenter.setAuthes(userCenterAuthes);
			map.put("userCenter", userCenter);
		}else{
			map.put("userCenter", userCenter);
		}
		if(blockChainHistory.isIfHave()){//区块链查询
			blockChainHistory.setAuthes(blockChainHistoryAuthes);
			map.put("blockChainHistory", blockChainHistory);
		}else{
			map.put("blockChainHistory", blockChainHistory);
		}
		if(userManagement.isIfHave()){//用户管理
			userManagement.setAuthes(userManagementAuthes);
			map.put("userManagement", userManagement);
		}else{
			map.put("userManagement", userManagement);
		}
		
		return map;
	}
	
	public static void main(String[] args) {
		AuthEnum[] values = AuthEnum.values();
		List<String> authList = new ArrayList<String>();
		for (int i = 0;i<values.length;i++) {
			authList.add(values[i].getAuthUrl());
		}
		Map<String, Object> authUrlToJson = authUrlToJson(authList);
		String jsonString = JSON.toJSONString(authUrlToJson,true);
		System.out.println(jsonString);
	}
}
