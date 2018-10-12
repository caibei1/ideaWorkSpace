package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fuzamei.pojo.bo.BackAttachmentBO;
import com.fuzamei.pojo.dto.BackRoleDTO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.BackAttachmentBillPO;
import com.fuzamei.pojo.po.BackUserPO;
import com.fuzamei.pojo.po.BankMoneyFlowPO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.po.WsjCashFlowPO;
import com.fuzamei.pojo.vo.BackUserVO;
import com.fuzamei.pojo.vo.WsjVo;

public interface WsjTestMapper {

	//根据用户id和权限id查询是否有权限
	int validateAuth(@Param(value = "userId") int userId,@Param(value = "auth") int auth);

	//按照条件查询
	List<EnterpriseDTO> findEnterprise(EnterpriseDTO enterpriseDTO);

	//入金/出金   待审核/已审核等  列表查询
	List<BankMoneyFlowPO> queryGold(@Param(value = "enterpriseDTO") EnterpriseDTO enterpriseDTO, @Param(value = "states")List<Integer> states,@Param(value = "operateType") Integer operateType);

	//查询记录数
	int queryGoldNum(@Param(value = "enterpriseDTO") EnterpriseDTO enterpriseDTO, @Param(value = "states")List<Integer> states,@Param(value = "operateType") Integer operateType);
	
	// 改变bankMoneyFlow的状态
	void changeBankMoneyFlowState(@Param(value = "moneyFlowNo") String moneyFlowNo, @Param(value = "state") int state);

	//查询bankMoneyFlow的状态
	int findBankMoneyFlowStateBymoneyFlowNo(String moneyFlowNo);

	//插入出金初审人信息和时间
	void updateBankMoneyFlowFirstCheckByAndFirstTimeBymoneyFlowNo(@Param(value = "userId") int userId,@Param(value = "moneyFlowNo") String moneyFlowNo,
			@Param(value = "firstTime") long currentTimeMillis);

	//根据moneyFlowNo查找记录
	BankMoneyFlowPO findBankMoneyFlowBymoneyFlowNo(String moneyFlowNo);

	//更新账户可用余额和总资产
	void updateAccountUsableAndTotalByAccountId(@Param(value = "amount") double amount, @Param(value = "accountId") int accountId, @Param(value = "currentTimeMillis") long currentTimeMillis);

	//记录入金审核数据
	void updateBankMoneyEndTimeBymoneyFlowNo(@Param(value = "moneyFlowNo") String moneyFlowNo, @Param(value = "endTime") long currentTimeMillis);

	//插入出金复审人信息和时间
	void updateBankMoneyFlowSecondCheckByAndSecondTimeBymoneyFlowNo(@Param(value = "userId") int userId, @Param(value = "moneyFlowNo") String moneyFlowNo,
			@Param(value = "secondTime") long currentTimeMillis);

	//更新账户冻结金额和总资产
	void updateAccountFreezeAndTotalByAccountId(@Param(value = "amount") double amount,@Param(value = "accountId") int accountId,@Param(value = "currentTimeMillis") long currentTimeMillis);

	//更新账户冻结金额和可用资产
	void updateAccountFreezeAndUsableByAccountId(@Param(value = "amount") double amount, @Param(value = "accountId") int accountId,@Param(value = "currentTimeMillis") long currentTimeMillis);
	
	//添加cashflow
	void addCashFlow(WsjCashFlowPO wsjCashFlowPO);

	//查询风控初审
	List<BillDTO> findWindFirstCheckByState(WsjVo wsjVo);

	//查询风控初审记录条数
	int findWindFirstCheckByStateCount(WsjVo wsjVo);

	//插入进度报告数据
	void addBackAttachments(List<BackAttachmentBO> backAttachmentBOs);
	void addBackAttachmentBills(List<BackAttachmentBillPO> backAttachmentBillPOs);

	//查找进度报告路径
	List<String> findReportFileByBillId(Integer billId);

	//查询企业额度信息
	List<EnterprisePO> findEnterpriseLimmit(EnterpriseDTO enterpriseDTO);
	
	//查询企业额度信息记录条数
	int findEnterpriseLimitNum(EnterpriseDTO enterpriseDTO);

	//更改企业授信额度
	void changeEnterpriseCreditLine(@Param(value = "enterpriseId")String enterpriseId,@Param(value = "creditLine") Integer creditLine);

	//查找企业是否存在
	int findEnterpriseExist(@Param(value = "enterpriseId") String enterpriseId);

	//查询用户
	List<BackUserPO> findBackUser(BackUserVO backUserVO);
	
	//查询用户数
	int findBackUserNum(BackUserVO backUserVO);

	//删除用户角色
	void deleteBackUserRole(Integer userId);
	
	//添加用户角色
	void addBackUserRole(@Param(value = "userId")Integer userId, @Param(value = "roleIds")List<Integer> roleIds);

	//查询是否存在该用户
	int findBackBackUserExist(Integer userId);

	//查询是否存在该角色
	int findBackRoleExist(int roleId);

	//查询所有角色
	List<BackRoleDTO> findAllBackRole();

	//查询用户具有的权限
	List<Integer> findBackUserRole(Integer userId);

	
}
