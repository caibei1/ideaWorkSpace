package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.bo.BillOrderBO;
import com.fuzamei.pojo.bo.CreditBO;
import com.fuzamei.pojo.bo.TongdunBO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.po.BillOrderPO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.vo.BillOrderVO;
import com.fuzamei.pojo.vo.BillVO;
import com.fuzamei.pojo.vo.CreditVO;
import com.fuzamei.pojo.vo.TongdunVO;

public interface VentureManagementMapper {
	List<Integer> queryToBeVerifiedBillIds(BillBO billBO);

	List<BillVO> queryToBeVerified(BillBO billBO);

	int queryToBeVerifiedCount(BillBO billBO);
	
	List<Integer> queryVerifiedBillIds(BillBO billBO);

	List<BillVO> queryVerified(BillBO billBO);

	int queryVerifiedCount(BillBO billBO);

	List<BillOrderVO> queryBillOrder(BillOrderBO billOrderBO);

	int queryBillOrderCount(BillOrderBO billOrderBO);

	BillDTO queryBillById(BillBO billBO);

	@Update("update bill set status=#{statusId},approve_money=#{approveMoney},version=#{version}+1 where b_id=#{billId} and version=#{version}")
	int verifyBillById(BillBO billBO);
	
	@Update("update bill set status=#{statusId},version=#{version}+1 where b_id=#{billId} and version=#{version}")
	int rejectBillById(BillBO billBO);
	
	@Select("select version from bill where b_id=#{billId}")
	int queryBillVersionById(BillBO billBO);//查询单据的版本号

	
	@Select("select count(*) from bill where b_id=#{billId}")
	int checkIfHaveBill(Integer billId);

	@Update("update enterprise set credit_line=#{creditLine},authorize_time=#{authorizeTime} where enterprise_id=#{receivedEnterpriseId}")
	void setEnterpriseCreditLine(CreditBO creditBO);
	@Update("update enterprise set credit_line=#{creditLine},authorize_time=#{authorizeTime},consumed_loan=0,total_repayment=0 where enterprise_id=#{receivedEnterpriseId}")
	void setEnterpriseCreditLine4FirstTime(CreditBO creditBO);//初次给企业设定授信额度，要将已经消费的额度和总还款额设置为0
	
//	@Select("select * from tongdun where enterprise_id=#{receivedEnterpriseId}")
//	TongdunVO queryTongdunResult(TongdunBO tongdunBO);

	List<TongdunVO> queryAllEnterpriseTongdunInfo(TongdunBO tongdunBO);

	int queryAllEnterpriseTongdunInfoCount(TongdunBO tongdunBO);

	@Select("select enterprise_id, enterprise_name,person_name,phone_num,id_card_num,pretty_json,report_time from tongdun where enterprise_id=#{receivedEnterpriseId}")
	TongdunVO queryPrettyTongdunResult(TongdunBO tongdunBO);

	@Select("select enterprise_name,person_name,phone_num,id_card_num,tongdun_result,report_time from tongdun where enterprise_id=#{receivedEnterpriseId}")
	TongdunVO queryUglyTongdunResult(TongdunBO tongdunBO);
	
	@Update("update tongdun set pretty_json=#{prettyJson} where enterprise_id=#{receivedEnterpriseId}")
	void insertPrettyTongdunResult(TongdunBO tongdunBO);
	
	@Select("select sum(approve_money) from bill where bill_in_enterprise=#{enterpriseId} and bill_end_date>=#{currentTime} and status not in (0,2)")
	Double getBillAvailableTotalAmountById(BillBO billBO);//查询收账企业名下所有【未过期】的应收账总额

	int insertToBillOrder(BillOrderPO billOrderPO);//将bill表中的信息转储到bill_order中去

	@Select("select count(*) from bill_order where b_order_id=#{billOrderId} for update")
	int checkIfHaveBillOrderById(int billOrderId);//查询是否出现重复的bill_order_id(锁表查询)

	List<CreditVO> queryEnterpriseCreditLine(CreditBO creditBO);//额度管理模块中企业额度信息的查询

	int queryEnterpriseCreditLineCount(CreditBO creditBO);//额度管理模块中企业额度信息的查询(做分页用)
	
	/*以下两个方法中的b_order_out_enterprise和b_order_in_enterprise不是一个字段的问题主要是因为在质押过程中，b_order_out_enterprise变成了之前借款的公司
	而还款完成后，原来的借款公司又变回了b_order_in_enterprise，这个规定是胡斌他们确定的*/
	@Select("select sum(approve_money) from bill_order where status=#{status} and b_order_out_enterprise=#{receivedEnterpriseId}")
	Double getUsedMoneyByEnterpriseId(CreditBO creditBO);
	@Select("select sum(approve_money) from bill_order where status=#{status} and b_order_in_enterprise=#{receivedEnterpriseId}")
	Double getTotalReturnedMoneyByEnterpriseId(CreditBO creditBO);
	
	@Select("select enterprise_id,credit_line from enterprise where enterprise_id=#{receivedEnterpriseId}")
	EnterprisePO queryEnterpriseCreditInfoById(CreditBO creditBO);
	
	@Select("select account_id from enterprise_account where enterprise_id=#{enterpriseId}")
	Integer queryAccountIdByEnterpriseId(BillBO billBO);
	
	@Update("update bill_asset set useable_order_money=useable_order_money+#{approveMoney} where account_id=#{accountId}")
	int insert2BillAsset(BillBO billBO);
	
	@Select("select enterprise_id,credit_line,consumed_loan from enterprise where enterprise_id=#{enterpriseId}")
	EnterprisePO queryLeftoverLoan(BillBO billBO);
	
	@Select("select status from bill where b_id=#{billId}")
	int queryBillStatusById(@Param("billId")Integer billId);

}
