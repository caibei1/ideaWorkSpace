package com.fuzamei.service;

import java.util.HashMap;
import java.util.List;

import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.po.FinancePO;
import com.fuzamei.pojo.vo.FinanceVO;
import com.fuzamei.util.PageDTO;

public interface FinanceManagementService {
    //入金
	public  PageDTO queryGolden(FinanceBO financeBO);
	public  PageDTO queryGoldenOKCheck(FinanceBO financeBO);
	
	//出金
	public  PageDTO queryOutGold(FinanceBO financeBO);
	public  PageDTO queryOutGoldOkCheck(FinanceBO financeBO);
	public List<FinanceVO> selectFinanceVOBean(HashMap<String, Object> map);//TODO
	//暂注释
	//public void   updateGoldenCashFlowStatus(FinancePO financePO);//入金  同意后修改状态   //入金  同意后修改状态同时给账户增加资产
	//public AccountPO  queryAccountTotalAssetByEnterpriseId(FinancePO  financePO);//根据企业id查询账户表的总资产
	
    public FinancePO  querycashFlow(FinancePO financePO);//77
    public void approveCashFlow(FinancePO financePO);
    public void rejectCashFlow(FinancePO financePO);
	  
	  
    public FinancePO  querycashFlowOutGold(FinancePO financePO);//88
    public void approveCashFlowOutGold(FinancePO financePO);
    public void rejectCashFlowOutGold(FinancePO financePO);
}
