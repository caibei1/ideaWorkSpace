package com.fuzamei.service.impl;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.Statuses;
import com.fuzamei.mapper.FinanceManagementMapper;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.dto.FinanceDTO;
import com.fuzamei.pojo.po.FinancePO;
import com.fuzamei.pojo.vo.FinanceVO;
import com.fuzamei.service.FinanceManagementService;
import com.fuzamei.util.PageDTO;

@Service
public class FinanceManagementServiceImpl implements FinanceManagementService{
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceManagementServiceImpl.class);
	@Autowired
	private FinanceManagementMapper financeManagementMapper;
	/**
	 * 入金 待审核
	 */
	@Override
	public PageDTO queryGolden(FinanceBO financeBO) {
		//financeBO.setStatus(Statuses.CHECK_NO);//要选定5代表未审核
		List<FinanceDTO> FinanceDTOList=financeManagementMapper.queryGolden(financeBO);
		int total = financeManagementMapper.queryGoldenCount(financeBO);
		return PageDTO.getPagination(total, FinanceDTOList);
		
	}
	
	/**
	 * 入金 已审核（状态是包括同意和拒绝的）
	 */
	@Override
	public PageDTO queryGoldenOKCheck(FinanceBO financeBO) {
		//financeBO.setStatus(Statuses.CHECK_NO);//排除不是5待审核的（就是已审核的）
		//long currentTime = System.currentTimeMillis();
		List<FinanceDTO> FinanceDTOList=financeManagementMapper.queryGoldenOKCheck(financeBO);
		int total = financeManagementMapper.queryGoldenOKCheckCount(financeBO);
		return PageDTO.getPagination(total, FinanceDTOList);
		
	}
	
	@Override
	public FinancePO querycashFlow(FinancePO financePO) {
		return  financeManagementMapper.querycashFlow(financePO);
	}
	//  入金 同意
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void approveCashFlow(FinancePO financePO) {
		financePO.setStatus(Statuses.CONSENT);//审核同意————>状态改为已同意
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financePO.setEndTime(currentTime);//点击同意操作后把当前完成得时间set进去————
		financeManagementMapper.okCashFlow(financePO);
		
		Double amount=financePO.getAmount();//得到前台充值（入金）得金额 
		FinancePO money= financeManagementMapper.queryAccountTotalAssetByEnterpriseId(financePO);
		Double sumTotalAsset=amount+money.getTotalAsset();//总资产+充值得金额
		Double sumUsableMoney=amount+money.getUsableMoney();//充值得金额+可用金额
		financePO.setTotalAsset(sumTotalAsset);//把入金得金额  加起来后set进总账户总资产里面=总资产金额
		financePO.setUsableMoney(sumUsableMoney);//把原可用金额+充值得金额=可用金额
		financeManagementMapper.updateAccountSumMoney(financePO);	
	}
	//入金 拒绝
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void rejectCashFlow(FinancePO financePO) {
		financePO.setStatus(Statuses.REFUSE);//审核拒绝-->状态改为拒绝
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financePO.setEndTime(currentTime);//点击拒绝操作后把当前完成得时间set进去————
		financeManagementMapper.okCashFlow(financePO);
	}
	
	
	/**
	 * 
	 *  当入金审核得时候，财务人员点击同意修改现金流表里得状态改为（已同意） 同时也给账户表得【总资产】加上去
	 *              财务人员点击拒绝修改现金流表里得状态改为（已拒绝）
	 */
	/*@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateGoldenCashFlowStatus(FinancePO financePO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		Integer status=financePO.getStatus(); 
		Double amount=financePO.getAmount();//得到前台充值（入金）得金额
		FinancePO money= (FinancePO) financeManagementMapper.queryAccountTotalAssetByEnterpriseId(financePO);
		Double sumTotalAsset=amount+money.getTotalAsset();//总资产+充值得金额
		Double sumUsableMoney=amount+money.getUsableMoney();//充值金额+可用金额
		if(status.equals(Statuses.CONSENT)) {//如果点击同意 就去修改状态和插入入金金额到账户总资产里面   if(status==3)  ？？？？
			//financePO.setTransactionId(financePO.getTransactionId());
			financePO.setEndTime(currentTime);//同意后把当前完成得时间set进去————》  在已审核里面在要取这个时间
			financePO.setStatus(Statuses.CONSENT);//把状态改为同意
			financeManagementMapper.updateGoldenCashFlowStatus(financePO);
			
			financePO.setTotalAsset(sumTotalAsset);//把入金得金额  加起来后set进总账户总资产里面=总资产金额
			financePO.setUsableMoney(sumUsableMoney);//把原可用金额+充值得金额=可用金额
			financeManagementMapper.updateAccountSumMoney(financePO);
		}
		if(status.equals(Statuses.REFUSE)){// if(status==4)？？？？
			financePO.setStatus(Statuses.REFUSE);//就把状态设置成为已拒绝   
			financeManagementMapper.updateGoldenCashFlowStatus(financePO);
			
		}
		
	}*/
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 出金 待审核
	 */
	@Override
	public PageDTO queryOutGold(FinanceBO financeBO) {
		//financeBO.setStatus(Statuses.CHECK_NO);//要选定5代表出金未审核
		List<FinanceDTO> FinanceDTOList=financeManagementMapper.queryOutGold(financeBO);
		int total = financeManagementMapper.queryOutGoldCount(financeBO);
		return PageDTO.getPagination(total, FinanceDTOList);
		
	}
	
	/**
	 * 出金 已审核（状态是包括同意和拒绝的）
	 */
	@Override
	public PageDTO queryOutGoldOkCheck(FinanceBO financeBO) {
		//financeBO.setStatus(Statuses.CHECK_NO);//选定（排除）不是5 出金待审核的
		List<FinanceDTO> FinanceDTOList=financeManagementMapper.queryOutGoldOkCheck(financeBO);
		int total = financeManagementMapper.queryOutGoldCountOkCheckCount(financeBO);
		return PageDTO.getPagination(total, FinanceDTOList);
		
	}
   
	@Override
	public FinancePO querycashFlowOutGold(FinancePO financePO) {
		return  financeManagementMapper.querycashFlowOutGold(financePO);
	}
	
	/**
	 * 【财务出纳  出金同意】审核
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void approveCashFlowOutGold(FinancePO financePO) {
		financePO.setStatus(Statuses.APPROVE_TRIAL);//审核同意————>状态改为  同意初审
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financePO.setEndTime(currentTime);//点击拒绝操作后把当前完成得时间set进去————
		financeManagementMapper.okCashFlowOutGold(financePO);
		
		
	}
	/**
	 * 【财务出纳  出金拒绝】审核
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void rejectCashFlowOutGold(FinancePO financePO) {
		financePO.setStatus(Statuses.REJECT_TRIAL);//审核拒绝————>状态改为  拒绝初审
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financePO.setEndTime(currentTime);//点击拒绝操作后把当前完成得时间set进去————
		financeManagementMapper.okCashFlowOutGold(financePO);
	}

	
	
	
	
	
	//待 TODO   XXX
	@Override
	public List<FinanceVO> selectFinanceVOBean(HashMap<String, Object> map) {
		return financeManagementMapper.selectFinanceVOBean(map);
	}




	

	
}
