package com.fuzamei.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Update;

import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.dto.FinanceDTO;
import com.fuzamei.pojo.po.AccountPO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.po.FinancePO;
import com.fuzamei.pojo.vo.FinanceVO;

public interface FinanceManagementMapper {
	//入金（待审核）（已审核）
	public List<FinanceDTO> queryGolden(FinanceBO financeBO);
	public  int queryGoldenCount(FinanceBO financeBO);
	public List<FinanceDTO> queryGoldenOKCheck(FinanceBO financeBO);
	public  int queryGoldenOKCheckCount(FinanceBO financeBO);
	
	//出金 （待审核）（已审核）
	public List<FinanceDTO> queryOutGold(FinanceBO financeBO);
	public  int queryOutGoldCount(FinanceBO financeBO);
	public List<FinanceDTO> queryOutGoldOkCheck(FinanceBO financeBO);
	public  int queryOutGoldCountOkCheckCount(FinanceBO financeBO);
	
	//入金 同意和拒绝
	public FinancePO  querycashFlow(FinancePO financePO);//77
	@Update("update cash_flow set status=#{status}   where  transaction_id=#{transactionId} and  state_id=1  and manusl_automatic=1")
	public  void   okCashFlow(FinancePO financePO);
	public void   updateGoldenCashFlowStatus(FinancePO financePO);//入金  同意后修改状态
	public  int  updateAccountSumMoney(FinancePO financePO);//入金  同意后修改状态同时给账户增加资产
	public FinancePO  queryAccountTotalAssetByEnterpriseId(FinancePO financePO);//根据企业id查询企业对应的账户的总资产
	
	//出金  同意和拒绝
	public FinancePO  querycashFlowOutGold(FinancePO financePO);//88
	@Update("update cash_flow set status=#{status}   where  transaction_id=#{transactionId} and  state_id=2")
	public  void   okCashFlowOutGold(FinancePO financePO);
	public void   updateGoldenOutGoldCashFlowStatus(FinancePO financePO);//出金  同意后修改状态

	
	public List<FinanceVO> selectFinanceVOBean(HashMap<String, Object> map);//TODO 待
}
