package com.fuzamei.service;


import java.util.LinkedHashMap;
import java.util.List;

import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.po.FinanceBankPO;
import com.fuzamei.pojo.vo.FinanceBankExceOutGlodenlOkCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExceOutGlodenlVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenRecordOkVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenRecordVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenVO;
import com.fuzamei.pojo.vo.FinanceBankExcelToAlreadyCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExcelToCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExcelVO;
import com.fuzamei.util.PageDTO;

public interface FinanceManageService {
	//查询所有的企业名字给前端   前端做划拨的时候拿到这些企业名字用
	public List<EnterprisePO> selectAllEnterpriseName();
	
	////入金
	public PageDTO queryGolden(FinanceBO financeBO);                                //查询入金待审核的列表带分页
	public PageDTO queryGoldenOkCheck(FinanceBO financeBO);                         //查询入金已审核的列表带分页
	public void approvebankMoneyFlow(FinanceBankPO financeBankPO );                 //入金待审核  点击同意
	public void rejectbankMoneyFlow( FinanceBankPO financeBankPO);                  //入金待审核  点击拒绝
	public FinanceBankPO querybankMoneyFlow(FinanceBankPO financeBankPO);
	
	////出金
	public PageDTO queryOutGold(FinanceBO financeBO);                               //查询出金待审核的列表带分页
	public PageDTO queryOutGoldOkCheck(FinanceBO financeBO);                        //查询出金已审核的列表带分页
	public void approvebankMoneyFlowOutGolden(FinanceBankPO financeBankPO );        //出金第一次待审核  点击同意
	public void rejectbankMoneyFlowOutGolden( FinanceBankPO financeBankPO);         //出金第一次待审核  点击拒绝
	public FinanceBankPO  querybankMoneyFlowOutGolden(FinanceBankPO financeBankPO);
	
	
	//出金 待复审
	public PageDTO queryToReview(FinanceBO financeBO);                              //查询出金待复审的列表待分页-
	public PageDTO queryHaveReview(FinanceBO financeBO);                            //查询出金已复审的列表待分页
	public void approvebankMoneyFlowOutGoldenToReview(FinanceBankPO financeBankPO );//出金第2次待复审  点击同意
	public void rejectbankMoneyFlowOutGoldenToReview(FinanceBankPO financeBankPO);  //出金第2次待复审  点击拒绝
	
	
	////导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenExportExcel();                //入金待审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenExportExcelCheckOk();         //入金已审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenOutExportExcel2();            //出金待审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenOutExportExcelOkCheck2();     //出金已审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectOutGlodenToReview();                //出金待复审列表导出Excel
	public List<LinkedHashMap<String, Object>> selectOutGlodenToReviewOkCheck();         //出金已复审列表导出Excel
	public List<LinkedHashMap<String, Object>> selectAwaitTransfera();                   //入金待划拨列表导出Excel
	public List<LinkedHashMap<String, Object>> selectHasBeenAllocated();                 //入金已划拨列表导出Excel
	
	//入金待划拨的
	public PageDTO queryAwaitTransfera(FinanceBO financeBO);                             //入金 待划拨的列表带分页
	public PageDTO queryHasBeenAllocated(FinanceBO financeBO);                           //入金 已划拨的列表带分页
	public void awaitTransfera(FinanceBankPO financeBankPO );                            //点击 确认划拨
	public void hasBeenAllocated(FinanceBankPO financeBankPO);                           //点击 退款 改变状态去审核
	public EnterpriseDTO queryEnterpriseNameBymoneyFlowNo(FinanceBankPO  po);      //根据传来的企业名称查询是否有这个企业名称
	
	//入金退款的审核
	public PageDTO queryToAuditARefund(FinanceBO financeBO);                            //入金 退款待审核的列表带分页
	public PageDTO queryhasARefund(FinanceBO financeBO);                                //入金 退款已审核列表带分页
	public void approveArefund(FinanceBankPO financeBankPO);                            //退款点击同意 调接口 把充值进来的钱原路给退回去
	
	//
	public  List<FinanceBankPO> selectSerialnumberBankCard();//入金退款入金拒绝
	public  void updateState(FinanceBankPO financeBankPO);//退款时根据流水编号修改状态为15
	public  void updateState2(FinanceBankPO financeBankPO);//入金拒绝时根据流水编号修改状态为19
	//
	public List<FinanceBankPO> selectOutGoldenAgree();   //出金同意  接口
	public  void updateOutGoldenState(FinanceBankPO financeBankPO); //出账成功的
	public void  updateOutGoldenState2(FinanceBankPO financeBankPO);//出账失败的

	public FinanceBankPO queryAccountTotalAssetByEnterpriseId2(FinanceBankPO financeBankPO);//查询账户总资产，可用金额，冻结金额
	public FinanceBankPO queryAccountTotalAssetOrFreezeMoneyByEnterpriseId(FinanceBankPO financeBankPO);

	public void againOnTheChain(FinanceBankPO financeBankPO);
	
	
	//**///***///
	
	
	
}
