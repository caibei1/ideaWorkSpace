package com.fuzamei.mapper;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

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

public interface FinanceManageMapper {
	//查询所有的企业名字给前端   前端做划拨的时候拿到这些企业名字用
	public List<EnterprisePO> selectAllEnterpriseName();
	
    //入金
	public List<FinanceBankPO> queryGolden(FinanceBO financeBO);       //查询入金待审核的列表
	public int queryGoldenCount(FinanceBO financeBO);              //查询入金待审核的列表总条数
	public List<FinanceBankPO> queryGoldenOkCheck(FinanceBO financeBO);//查询入金已经审核的列表（就是入金通过的）
	public int queryGoldenOkCheckCount(FinanceBO financeBO);       //查询入金已经审核的列表总条数（就是入金通过的）
	
	@Update(" update bank_money_flow set state=#{state},end_time=#{endTime} where  money_flow_no=#{moneyFlowNo} and operation_type=1")
	public  void   okOrNobankMoneyFlow(FinanceBankPO financeBankPO);   //入金待审核点击同意
	@Update(" update bank_money_flow set state=#{state},end_time=#{endTime},serialnumber=#{serialnumber} where money_flow_no=#{moneyFlowNo}")
	public  void   okOrNobankMoneyFlow2(FinanceBankPO financeBankPO);   //入金待审核点击拒绝
	public FinanceBankPO queryAccountTotalAssetByEnterpriseId(FinanceBankPO financeBankPO);
	public void updateAccountSumMoney(FinanceBankPO financeBankPO);
	public FinanceBankPO  querybankMoneyFlow(FinanceBankPO financeBankPO);
	
    //////////
	//出金
	public FinanceBankPO queryAccountTotalAssetByEnterpriseId2(FinanceBankPO financeBankPO);//出金第一次审核拒绝操作 修改冻结金额和可用余额
	public List<FinanceBankPO> queryOutGold(FinanceBO financeBO);            	//查询出金待审核的列表
	public int queryOutGoldCount(FinanceBO financeBO);                   	//查询出金待审核的列表总条数
	public List<FinanceBankPO> queryOutGoldOkCheck(FinanceBO financeBO);     	//查询出金已审核的列表
	public int queryOutGoldOkCheckCount(FinanceBO financeBO);            	//查询出金已审核的列表总条数
	@Update(" update bank_money_flow set state=#{state},first_time=#{firstTime},first_check_by=#{firstCheckBy}  where  money_flow_no=#{moneyFlowNo} and  state=1  and operation_type=2")
	public  void   okOrNoOutGoldenbankMoneyFlow(FinanceBankPO financeBankPO);	//出金待审核点击同意和拒绝操作
	public FinanceBankPO  querybankMoneyFlowOutGolden(FinanceBankPO financeBankPO);
	public void updateFreezeMoneyUsableMoney(FinanceBankPO financeBankPO);
	
	////////////
	//出金 待复审
	public List<FinanceBankPO> queryToReview(FinanceBO financeBO);             //查询出金待复审的列表
	public int queryToReviewCount(FinanceBO financeBO);                    //查询出金待复审的总条数
	public List<FinanceBankPO> queryHaveReview(FinanceBO financeBO);           //查询出金已复审的列表
	public int queryHaveReviewCount(FinanceBO financeBO);                  //查询出金已复审的总条数
	@Update("update bank_money_flow set state=#{state},second_time=#{secondTime},second_check_by=#{secondCheckBy},uid=#{uid},serialnumber=#{serialnumber}  where  money_flow_no=#{moneyFlowNo} and  state=5  and operation_type=2")
	public  int okOrNoToReview(FinanceBankPO financeBankPO);                  //出金待复审点击同意和拒绝操作
	public void approvebankMoneyFlowOutGoldenToReviewAccountMoney(FinanceBankPO financeBankPO);
	public void approvebankMoneyFlowOutGoldenToReviewAccountMoney2(FinanceBankPO financeBankPO);
	public FinanceBankPO queryAccountTotalAssetOrFreezeMoneyByEnterpriseId(FinanceBankPO financeBankPO);
	
	///////////
	//所有的导出 Excel 
	public List<LinkedHashMap<String, Object>> selectGoldenExportExcel();                		//入金待审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenExportExcelCheckOk();   				//入金已审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenOutExportExcel2();   			    //出金待审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectGoldenOutExportExcelOkCheck2();            //出金已审核列表导出Excel
	public List<LinkedHashMap<String, Object>> selectOutGlodenToReview();               		//出金待复审列表导出Excel
	public List<LinkedHashMap<String, Object>> selectOutGlodenToReviewOkCheck();                //出金已复审列表导出Excel
	public List<LinkedHashMap<String, Object>> selectAwaitTransfera();                          //入金待划拨列表导出Excel
	public List<LinkedHashMap<String, Object>> selectHasBeenAllocated();                     //入金已划拨列表导出Excel
	
	///////////
	//入金 待划拨 已划拨
	public List<FinanceBankPO> queryAwaitTransfera(FinanceBO financeBO);      //查询入金待划拨的
	public int queryAwaitTransferaCount(FinanceBO financeBO);                 //查询入金待划拨的列表总条数
	public List<FinanceBankPO> queryHasBeenAllocated(FinanceBO financeBO);    //查询入金已划拨的
	public int queryHasBeenAllocatedCount(FinanceBO financeBO);               //查询入金已划拨的列表总条数
	//划拨操作
	@Update("update bank_money_flow set state=#{state},end_time=#{endTime},enterprise_id=#{enterpriseId},"
			+ "enterprise_name=#{enterpriseName},transfer_enterprise=#{transferEnterprise} "
			+ "where money_flow_no=#{moneyFlowNo} and state=11 and operation_type=1")
	public void okTransfer(FinanceBankPO financeBankPO); 
	public EnterpriseDTO queryEnterpriseNameBymoneyFlowNo(FinanceBankPO po);//根据传来的企业名称查询是否有这个企业名称
	@Update("update bank_money_flow set state=#{state},end_time=#{endTime} where money_flow_no=#{moneyFlowNo} and state=11 and operation_type=1")
	public void transferOrrefund(FinanceBankPO financeBankPO); 
	//////////
	//退款需要再次审核
	public List<FinanceBankPO> queryToAuditARefund(FinanceBO financeBO);      //查询待审核退款的列表
	public int queryToAuditARefundCount(FinanceBO financeBO);                 //查询待审核退款列表的总条数
	public List<FinanceBankPO> queryhasARefund(FinanceBO financeBO);          //查询已审核退款的列表
	public int queryhasARefundCount(FinanceBO financeBO);                     //查询已审核退款列表的总条数
	@Update("update bank_money_flow set state=#{state},end_time=#{endTime},serialnumber=#{serialnumber} "
			+ "where money_flow_no=#{moneyFlowNo}")//14 到时候待改
	public void updateAgreetoArefund(FinanceBankPO financeBankPO); 
	
	/////////
	//public  void  updateSerialnumber(FinanceBankPO financeBankPO);  //根据银行卡号和企业id修改serialnumber
	public List<FinanceBankPO> selectSerialnumberBankCard();
	public List<FinanceBankPO> selectOutGoldenAgree();
	
	public void  updateState(FinanceBankPO financeBankPO);
	public void  updateState2(FinanceBankPO financeBankPO);
	@Update("update bank_money_flow set state=#{state} where serialnumber=#{serialnumber}") //出金同意  成功的and operation_type=2
	public void  updateOutGoldenState(FinanceBankPO financeBankPO);
	
	@Update("update bank_money_flow set state=#{state} where serialnumber=#{serialnumber}") //出金同意  失败的and operation_type=2   
	public void  updateOutGoldenState2(FinanceBankPO financeBankPO);
	public void  updateFlagOrSignBySerialNumber(FinanceBankPO financeBankPO);//根据要请求得id set进去flag和sign
	
	//<!--以下Sql是宋东洋要我这边出金，入金，成功和失败后 给他cash_flow表插入的数据  -->
	public void insertGoldenGoCashFlowSucces(FinanceBankPO financeBankPO);
	public void insertGoldenGoCashFlow(FinanceBankPO financeBankPO);
} 
