package com.fuzamei.service.impl;

import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.OperationHistoryEnum;
import com.fuzamei.constant.States;
import com.fuzamei.constant.Statuses;
import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.mapper.FinanceManageMapper;
import com.fuzamei.mapper.UserMapper;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.po.FinanceBankPO;
import com.fuzamei.pojo.po.FinancePO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.pojo.vo.FinanceBankExceOutGlodenlOkCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExceOutGlodenlVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenRecordOkVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenRecordVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenVO;
import com.fuzamei.pojo.vo.FinanceBankExcelToAlreadyCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExcelToCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExcelVO;
import com.fuzamei.service.FinanceManageService;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ProtoBuf4SM2Util;

import fzmsupply.Api;
import fzmsupply.Api.CashDeposit;
import fzmsupply.Api.CashRecord;
@Service
public class FinanceManageServiceImpl  implements  FinanceManageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceManageServiceImpl.class);
	@Autowired
	private FinanceManageMapper financeManageMapper;
	@Autowired
	private BlockChainHistoryMapper blockChainHistoryMapper;
	@Autowired
	private UserMapper userMapper;
	//查询所有企业表的企业名称
	@Override
	public List<EnterprisePO> selectAllEnterpriseName() {
		return financeManageMapper.selectAllEnterpriseName();
	}

	//查询入金待审核的列表带分页
	@Override
	public PageDTO queryGolden(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryGolden(financeBO);
		int total = financeManageMapper.queryGoldenCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
    
	//查询入金已审核的列表带分页
	@Override
	public PageDTO queryGoldenOkCheck(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryGoldenOkCheck(financeBO);
		int total = financeManageMapper.queryGoldenOkCheckCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	@Override
	public FinanceBankPO querybankMoneyFlow(FinanceBankPO financeBankPO) {
		return financeManageMapper.querybankMoneyFlow(financeBankPO);
	}

	//入金待审核点击 同意操作  同意后修改账户表的总资产加起来  和  可用额度加起来的动作    在往cash_flow表里插入一条数据
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void approvebankMoneyFlow(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setState(States.GOLDEN_PASS);//set状态为入金通过
		financeBankPO.setEndTime(currentTime);     //set进去完成时间
		financeManageMapper.okOrNobankMoneyFlow(financeBankPO);//修改状态
		
		Double amount=financeBankPO.getAmount();//得到前台充值（入金）得金额 
		FinanceBankPO money= financeManageMapper.queryAccountTotalAssetByEnterpriseId(financeBankPO);
		Double sumTotalAsset=amount+money.getTotalAsset();//总资产+充值得金额
		Double sumUsableMoney=amount+money.getUsableMoney();//充值得金额+可用金额
		financeBankPO.setTotalAsset(sumTotalAsset);//把入金得金额  加起来后set进总账户总资产里面=总资产金额
		financeBankPO.setUsableMoney(sumUsableMoney);//把原可用金额+充值得金额=可用金额
		financeManageMapper.updateAccountSumMoney(financeBankPO);
		
	
		//-查询入金审核同意得  上区块链-//
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(financeBankPO.getEnterpriseId());
		long rmb = (long) (financeBankPO.getAmount()*10000);//这里最后商量是
		System.out.println(uid+"企业id是");
		Api.CashDeposit.Builder builder = Api.CashDeposit.newBuilder();		
		builder.setRmb(rmb);//入金金额
		builder.setUid(uid);//企业id
		CashDeposit cashDeposit = builder.build();
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.examineDeposit(operatorId,operatorPubKey, operatorPriKey,cashDeposit);//？？？
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.EXAMINEDEPOSIT.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.EXAMINEDEPOSIT.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		
		//给宋东洋插入一条 入金同意得一条数据
    	FinanceBankPO fb=new FinanceBankPO();
		fb.setMoneyFlowNos((Long.parseLong(financeBankPO.getMoneyFlowNo())));
		fb.setEnterpriseId(financeBankPO.getEnterpriseId());
		fb.setAccountId(financeBankPO.getAccountId());//这个字段可以不要 
		fb.setEnterpriseName(financeBankPO.getEnterpriseName());
		fb.setAmount(amount);
		fb.setCreateTime(financeBankPO.getCreateTime());
		fb.setEndTime(currentTime);
		fb.setOperationType(1);//操作类型些的是死的  ，1代表入金操作
		fb.setBankCard(financeBankPO.getBankCard());
		fb.setCashFlowHash(String.valueOf(protoBufBean.getInstructionId()));
		fb.setState(States.CASHFLOW_SUCCED);
		financeManageMapper.insertGoldenGoCashFlowSucces(fb);
		
		//最后在上链
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}
	
	
	
	//入金待审核点击 拒绝操作  拒绝了【调接口】目前就只做修改状态动作    在往cash_flow表里插入一条数据
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void rejectbankMoneyFlow(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setState(States.INTHEPROCESSING); //审核拒绝-->状态改为  19处理中...待调银行接口查看返回的信息就在进行修改状态成（已退款）  XXX
		financeBankPO.setEndTime(currentTime);        //点击拒绝操作后把当前完成得时间set进去————
		financeManageMapper.okOrNobankMoneyFlow2(financeBankPO);
		
		FinanceBankPO fb=new FinanceBankPO();
		fb.setMoneyFlowNos((Long.parseLong(financeBankPO.getMoneyFlowNo())));
		fb.setEnterpriseId(financeBankPO.getEnterpriseId());
		fb.setAccountId(financeBankPO.getAccountId());
		fb.setEnterpriseName(financeBankPO.getEnterpriseName());
		fb.setAmount(financeBankPO.getAmount());
		fb.setCreateTime(financeBankPO.getCreateTime());
		fb.setEndTime(currentTime);
		fb.setOperationType(1);//操作类型是死的  ，1代表入金操作
		fb.setBankCard(financeBankPO.getBankCard());//计financeBankPO.accno()
		fb.setState(States.CASHFLOW_DEFEATED);
		financeManageMapper.insertGoldenGoCashFlow(fb);
		
	
		

	}
    ////以下出金功能///////////////////////////////////////////////////////////////////////////////////////////////
	
	//财务管理查看 出金 待审核 列表
	@Override
	public PageDTO queryOutGold(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryOutGold(financeBO);
		int total = financeManageMapper.queryOutGoldCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	//财务管理查看 出金 已审核 的列表
	@Override
	public PageDTO queryOutGoldOkCheck(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryOutGoldOkCheck(financeBO);
		int total = financeManageMapper.queryOutGoldOkCheckCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	@Override
	public FinanceBankPO querybankMoneyFlowOutGolden(FinanceBankPO financeBankPO) {
		return financeManageMapper.querybankMoneyFlowOutGolden(financeBankPO);
	}
	
	//财务管理 第一次出金审核  同意操作  只做修改状态动作 【上区块链】
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void approvebankMoneyFlowOutGolden(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setFirstTime(currentTime);      //set进去初审时间
		financeBankPO.setState(States.FIRST_PASS);    //set初审通过
		//financeBankPO.setFirstCheckBy(financeBankPO.getUserId());//set进去  初审的人
		financeManageMapper.okOrNoOutGoldenbankMoneyFlow(financeBankPO);
		
		//-查询出金初次审核同意得上区块链-//   待后面打开注释
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(financeBankPO.getEnterpriseId());
		System.out.println(uid+"88u企业iduuuuuuu");
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水号
		builder.setFlag(true);//
		CashRecord cashRecord = builder.build();
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.ExamineWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);//？？？
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.EXAMINEWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.EXAMINEWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}

	//财务管理 第一次出金审核  拒绝操作 做修改状态动作和修改冻结金额和可用的金额   总资产不变，在往cash_flow表里插入一条数据
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void rejectbankMoneyFlowOutGolden(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setFirstTime(currentTime);      //set进去  初审时间
		financeBankPO.setState(States.FIRST_REJECT);  //set进去  初审拒绝
		financeManageMapper.okOrNoOutGoldenbankMoneyFlow(financeBankPO);
		
		Double amount=financeBankPO.getAmount();//得到前台提现（出金）得金额 
		System.out.println(amount+"金额");
		FinanceBankPO money= financeManageMapper.queryAccountTotalAssetByEnterpriseId2(financeBankPO);
		Double dongjiemoney=money.getFreezeMoney();//得到冻结金额
		Double keyongmoney=money.getUsableMoney(); //得到可用金额
		System.out.println(dongjiemoney+"llllm"+keyongmoney);
		Double sumDongJieMoney=dongjiemoney-amount;//冻结-要准备提走的金额
		Double  minkeyongMoney=keyongmoney+amount;//可用金额+准备提现提走金额
		financeBankPO.setFreezeMoney(sumDongJieMoney);//
		financeBankPO.setUsableMoney(minkeyongMoney);
		financeManageMapper.updateFreezeMoneyUsableMoney(financeBankPO);//修改冻结的钱和可用的钱	 总资产不变
		
		//出金 初审得拒绝  上区块链   待后面打开注释
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(financeBankPO.getEnterpriseId());
		System.out.println(uid+"企业id是");
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水号
		builder.setFlag(false);//拒绝是false
		CashRecord cashRecord = builder.build();
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.ExamineWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);//？？？
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.EXAMINEWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.EXAMINEWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		
		//第一次拒绝也给宋东洋插入一条数据  待后面打开注释
		FinanceBankPO fb=new FinanceBankPO();
		fb.setMoneyFlowNos((Long.parseLong(financeBankPO.getMoneyFlowNo())));
		fb.setEnterpriseId(financeBankPO.getEnterpriseId());
		fb.setAccountId(financeBankPO.getAccountId());
		fb.setEnterpriseName(financeBankPO.getEnterpriseName());
		fb.setAmount(financeBankPO.getAmount());
		fb.setCreateTime(financeBankPO.getCreateTime());
		fb.setEndTime(currentTime);
		fb.setOperationType(2);//操作类型些的是死的  ，2代表出金操作
		System.out.println("dfdf"+financeBankPO.getAccno());
		fb.setBankCard(financeBankPO.getAccno());
		fb.setCashFlowHash(String.valueOf(protoBufBean.getInstructionId()));
		fb.setState(States.CASHFLOW_DEFEATED);
		financeManageMapper.insertGoldenGoCashFlowSucces(fb);
		
		//最后在上链   待后面打开注释
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}
	
	//以下是出金待复审操作//////////////////////////////////////////////////////////////////////////////******************
	
	//由【财务主管】来查询（待复审）列表
	@Override
	public PageDTO queryToReview(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryToReview(financeBO);
		int total = financeManageMapper.queryToReviewCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}

	//由【财务主管】来查询(已复审列表)
	@Override
	public PageDTO queryHaveReview(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryHaveReview(financeBO);
		int total = financeManageMapper.queryHaveReviewCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	//由【财务主管】来点击待复审列表里的（同意）按钮操作  第二次复审【同意掉接口 所有金额不变，只做修改状态（改为复审通过）】   等银行返回信息了才能把平台上钱减掉
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void approvebankMoneyFlowOutGoldenToReview(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setSecondTime(currentTime);
		//financeBankPO.setSecondCheckBy(financeBankPO.getUserId());//set进去  复审的人
		financeBankPO.setState(States.SECOND_PASS);
		financeManageMapper.okOrNoToReview(financeBankPO);
		
		//点击同意操作后也要上链（4.13新加） //先注释 待后面打开
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(financeBankPO.getEnterpriseId());
		System.out.println(uid+"企业id是");
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水编号
		builder.setFlag(true);//同意是true
		CashRecord cashRecord = builder.build();
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.reviewWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);//？？？
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.REVIEWWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.REVIEWWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
		
	}
	
	//由【财务主管】来点击待复审列表里的（拒绝）按钮操作   第二次复审  拒绝了钱冻结中也给减走   可用金额给他加起来  总资产不变，在往cash_flow表里插入一条数据
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void rejectbankMoneyFlowOutGoldenToReview(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setSecondTime(currentTime);
		financeBankPO.setState(States.SECOND_REJECT);
		int i = financeManageMapper.okOrNoToReview(financeBankPO);
		System.out.println("i:"+i);
		Double amount=financeBankPO.getAmount();//得到前台提现（出金）得金额 
		FinanceBankPO money= financeManageMapper.queryAccountTotalAssetByEnterpriseId2(financeBankPO);
		Double dongjiemoney=money.getFreezeMoney();//得到冻结金额
		Double keyongmoney=money.getUsableMoney(); //得到可用金额
		Double sumDongJieMoney=dongjiemoney-amount;//冻结-要准备提走的金额
		Double  minkeyongMoney=keyongmoney+amount;//可用金额要+准备提现金额
		financeBankPO.setFreezeMoney(sumDongJieMoney);//
		financeBankPO.setUsableMoney(minkeyongMoney);
		financeManageMapper.updateFreezeMoneyUsableMoney(financeBankPO);//修改冻结的钱和可用的钱	
		
		//出金复审 拒绝得 上区块链   待后面打开注释
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String uid = String.valueOf(financeBankPO.getEnterpriseId());
		System.out.println(uid+"企业id是");
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水号
		builder.setFlag(false);//拒绝是false
		CashRecord cashRecord = builder.build();
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.reviewWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);//？？？
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.REVIEWWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.REVIEWWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		
		//给宋东洋插入一条数据  待后面打开注释
		FinanceBankPO fb=new FinanceBankPO();
		//fb.setMoneyFlowNo(financeBankPO.getMoneyFlowNo());
		fb.setMoneyFlowNos((Long.parseLong(financeBankPO.getMoneyFlowNo())));
		fb.setEnterpriseId(financeBankPO.getEnterpriseId());
		fb.setAccountId(financeBankPO.getAccountId());
		fb.setEnterpriseName(financeBankPO.getEnterpriseName());
		fb.setAmount(financeBankPO.getAmount());
		fb.setCreateTime(financeBankPO.getCreateTime());
		fb.setEndTime(currentTime);
		fb.setOperationType(2);//操作类型些的是死的  ，2代表出金操作
		fb.setBankCard(financeBankPO.getBankCard());
		fb.setCashFlowHash(String.valueOf(protoBufBean.getInstructionId()));
		fb.setState(States.CASHFLOW_DEFEATED);
		financeManageMapper.insertGoldenGoCashFlowSucces(fb);
		
		
		//最后在上链    待后面打开注释
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}
	
    ///////////////////////////////////////////////////////////////////以下是导出Excel
	@Override
	public List<LinkedHashMap<String, Object>> selectGoldenExportExcel() {
		return financeManageMapper.selectGoldenExportExcel();
	}

	@Override
	public List<LinkedHashMap<String, Object>> selectGoldenOutExportExcel2() {
		return financeManageMapper.selectGoldenOutExportExcel2();
	}

	@Override
	public List<LinkedHashMap<String, Object>> selectGoldenExportExcelCheckOk() {
		return financeManageMapper.selectGoldenExportExcelCheckOk();
	}

	@Override
	public List<LinkedHashMap<String, Object>> selectGoldenOutExportExcelOkCheck2() {
		return financeManageMapper.selectGoldenOutExportExcelOkCheck2();
	}

	@Override
	public List<LinkedHashMap<String, Object>> selectOutGlodenToReview() {
		return financeManageMapper.selectOutGlodenToReview();
	}

	@Override
	public List<LinkedHashMap<String, Object>> selectOutGlodenToReviewOkCheck() {
		return financeManageMapper.selectOutGlodenToReviewOkCheck();
	}
	@Override
	public List<LinkedHashMap<String, Object>> selectAwaitTransfera() {
		return financeManageMapper.selectAwaitTransfera();
	}
	@Override
	public List<LinkedHashMap<String, Object>> selectHasBeenAllocated() {
		return financeManageMapper.selectHasBeenAllocated();
	}
	//----------------------------------------------------------------------------------------------------
	
	//财务管理来查看 入金管理待划拨的
	@Override
	public PageDTO queryAwaitTransfera(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryAwaitTransfera(financeBO);
		int total = financeManageMapper.queryAwaitTransferaCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	//财务管理查看 入金管理已划拨的
	@Override
	public PageDTO queryHasBeenAllocated(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryHasBeenAllocated(financeBO);
		int total = financeManageMapper.queryHasBeenAllocatedCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	//点击  划拨操作  去入金审核里审核
	//备：这里也要在上一条入金申请上链
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void awaitTransfera(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();  //得到当前系统时间
		financeBankPO.setEndTime(currentTime);//设置完成时间
		financeBankPO.setState(States.TRANSFERA_AMOUNT);//确认划拨后状态改为 已划拨13
		financeManageMapper.okTransfer(financeBankPO);
	
	}
	 
	//根据传来的企业名称查询是否有这个企业名称
	@Override
	public EnterpriseDTO queryEnterpriseNameBymoneyFlowNo(FinanceBankPO   po) {
		return financeManageMapper.queryEnterpriseNameBymoneyFlowNo(po);
	}
	//点击  退款操作
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void hasBeenAllocated(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setEndTime(currentTime);
		financeBankPO.setState(States.A_REFUND);//点击退款后状态改为 退款中   还需要在审核的
		financeManageMapper.transferOrrefund(financeBankPO);
	}

    //入金 退款待审核的列
	@Override
	public PageDTO queryToAuditARefund(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryToAuditARefund(financeBO);
		int total = financeManageMapper.queryToAuditARefundCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	//入金 退款已审核的列
	@Override
	public PageDTO queryhasARefund(FinanceBO financeBO) {
		List<FinanceBankPO> financeBankPOList=financeManageMapper.queryhasARefund(financeBO);
		int total = financeManageMapper.queryhasARefundCount(financeBO);
		return PageDTO.getPagination(total, financeBankPOList);
	}
	
	//入金记录退款审核   点击同意退款
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void approveArefund(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setEndTime(currentTime);
		financeBankPO.setState(States.PROCESSED);//点击同意后状态改为 【等待处理】,待定时器那边查回后在修改成已退款
		financeManageMapper.updateAgreetoArefund(financeBankPO);
		
		
	/*不要 Double amount=financeBankPO.getAmount();//拿到到前台充值（（无法识别的账）入金）得金额 
		FinanceBankPO money= financeManageMapper.queryAccountTotalAssetByEnterpriseId(financeBankPO);
		Double totalAsset=money.getTotalAsset();  //账户总资产
		Double usableMoney=money.getUsableMoney();//可用金额
		Double minTotalAsset=totalAsset-amount;   //总资产-原先充值进来的金额
		Double minUsableMoney=usableMoney-amount;  //可用余额-原先充值进来的金额
		financeBankPO.setTotalAsset(minTotalAsset);
		financeBankPO.setUsableMoney(minUsableMoney);
		financeManageMapper.updateAccountSumMoney(financeBankPO);*/
	}
	@Override
	public FinanceBankPO queryAccountTotalAssetByEnterpriseId2(FinanceBankPO financeBankPO) {//查询账户总资产，可用金额，冻结金额
		return financeManageMapper.queryAccountTotalAssetByEnterpriseId2(financeBankPO);
	}
	@Override
	public FinanceBankPO queryAccountTotalAssetOrFreezeMoneyByEnterpriseId(FinanceBankPO financeBankPO) {
		return financeManageMapper.queryAccountTotalAssetOrFreezeMoneyByEnterpriseId(financeBankPO);
	}
	/**
	 * 查询卡号和返回的id
	 */
	@Override
	public  List<FinanceBankPO>  selectSerialnumberBankCard() {
		return financeManageMapper.selectSerialnumberBankCard();
	}
	
	/**
	 * 退款 银行返回状态时根据流水编号修改状态
	 * 入金退款同意，银行返回过来的再次操作  
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateState(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setEndTime(currentTime);//新加的4.8日
		financeBankPO.setState(States.REFUNDED_AMOUNT);//状态改为已退款
		financeManageMapper.updateState(financeBankPO);
	}
	
	/**
	 * 退款 银行返回状态时根据流水编号修改状态
	 * 入金拒绝，银行返回过来的再次操作   入金审核拒绝不上区块链
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateState2(FinanceBankPO financeBankPO) {
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		financeBankPO.setEndTime(currentTime);//新加的4.8日
		financeBankPO.setState(States.GOLDEN_REJECT);//状态改为入金拒绝
		financeManageMapper.updateState2(financeBankPO);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<FinanceBankPO> selectOutGoldenAgree() {
		return financeManageMapper.selectOutGoldenAgree();
	
	}
	
	/**
	 * 只有银行返回过来（成功了）得信息才着这里修改状态和修改掉金额
	 *【 出金同意调接口修改状态
	 * 也要修改冻结金额减掉，总资产也要减掉，（在往cash_flow表里插入一条数据）】
	 * 2.3.4是成功的
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateOutGoldenState (FinanceBankPO financeBankPO) {
		Double amount=financeBankPO.getAmount();//拿到到前台准备提现（出金）得金额 
		System.out.println(amount+"提金额kkdkkdkjcjnc空");
		FinanceBankPO money= financeManageMapper.queryAccountTotalAssetOrFreezeMoneyByEnterpriseId(financeBankPO);//根据流水编号查询得
		Double totalAsset=money.getTotalAsset();
		System.out.println("总"+totalAsset);
		Double freezeMoney=money.getFreezeMoney();
		System.out.println("冻"+freezeMoney);
		Double minTotalAsset=totalAsset-amount;
		Double minFreezeMoney=freezeMoney-amount;
		financeBankPO.setTotalAsset(minTotalAsset);
		financeBankPO.setFreezeMoney(minFreezeMoney);
		financeManageMapper.approvebankMoneyFlowOutGoldenToReviewAccountMoney(financeBankPO);//根据企业id修改金额得
		
		//lys返回为2.3.4后，金额也修改好了   在把状态改为 出账成功
		financeBankPO.setState(States.OUT_ACCOUNT_SUCCEED);//状态改为：出账成功9
		System.out.println(financeBankPO.getSerialnumber()+"ssssssssssssssss"+financeBankPO.getState());
		financeManageMapper.updateOutGoldenState(financeBankPO);
		
		//银行返回2.3.4成功后---先修改钱在上区块链
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		System.out.println("对象"+userDetailDTO);
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水号
		builder.setFlag(true);
		CashRecord cashRecord = builder.build();  //这里是出金成功后得上链
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.confirmWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.REVIEWWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.REVIEWWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		
	
		/*if(!checkResult){这里不抛异常 下面上链失败就可以set=flag和sign
		 throw new RuntimeException(BlockChainUtil.getErrorMessage(result+"前面返回结果+后面签名"+protoBufBean.getSignature()));//抛出具体的区块链错误信息+签名
		}*/
		/*System.out.println(result+"结果");
		System.out.println(financeBankPO.getSerialnumber()+"jsjsjsjj");
		System.out.println(protoBufBean.getSignature()+"签名");
		System.out.println(checkResult+"上链结果");*/

		//出金复审同意  掉接口返回成功时 也给宋东洋插入一条数据到cash_flow表
		FinanceBankPO fb=new FinanceBankPO();
		fb.setMoneyFlowNos((Long.parseLong(financeBankPO.getMoneyFlowNo())));
		fb.setEnterpriseId(financeBankPO.getEnterpriseId());
		fb.setAccountId(financeBankPO.getAccountId());
		fb.setEnterpriseName(financeBankPO.getEnterpriseName());//空值 待改
		fb.setAmount(financeBankPO.getAmount());
		fb.setCreateTime(financeBankPO.getCreateTime());
		fb.setEndTime(currentTime);
		fb.setOperationType(2);//操作类型些的是死的  ，2代表出金操作
		fb.setBankCard(financeBankPO.getBankCard());
		fb.setState(States.CASHFLOW_SUCCED);
		fb.setCashFlowHash(String.valueOf(protoBufBean.getInstructionId()));
		financeManageMapper.insertGoldenGoCashFlowSucces(fb);
		//最后动作才上链
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(checkResult=false) {//如果上链不成功得话  就把签名和标识成 1 set到数据库里    下次定时器实时刷新出来只做上链操作不用在修改金额了
			financeBankPO.setSign(protoBufBean.getSignature());//签名
			financeBankPO.setFlag(1);//1表示失败
			System.out.println(financeBankPO.getSerialnumber()+"健康");
			financeManageMapper.updateFlagOrSignBySerialNumber(financeBankPO);//区块链不成功都把签名存进数据库  但是flag 1表示失败
		}

	}
	
	
	
	/**
	 * 实时查询 出金已经成功了，但区块链没上成功得
	 * 这里需要重新在上一次区块链
	 */
	@Override
	public void againOnTheChain(FinanceBankPO financeBankPO) {
		//再次重新只上区块链  别的不操作
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		System.out.println(userDetailDTO+"对象打印");
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水号
		builder.setFlag(true);
		CashRecord cashRecord = builder.build();
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.confirmWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.REVIEWWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.REVIEWWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
		
	}
	
	
	/**
	 * 银行返回失败的                     
	 * 5和A是确认失败
	 * 如果是失败把冻结金额还给可用金额上面+
	 * 总金额在加起来+准备提走的金额
	 * 
	 * 失败也上区块链
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateOutGoldenState2(FinanceBankPO financeBankPO) {
		Double amount=financeBankPO.getAmount();//拿到到前台准备提现（出金）得金额 
		FinanceBankPO money= financeManageMapper.queryAccountTotalAssetOrFreezeMoneyByEnterpriseId(financeBankPO);
		Double usableMoney=money.getUsableMoney();
		Double freezeMoney=money.getFreezeMoney();
		
		Double sumUsableMoney=usableMoney+amount;//失败了 就把要提走的金额+可用的金额上面
		Double minFreezeMoney=freezeMoney-amount;//失败了 就把冻结的金额减掉-要提现的金额
		financeBankPO.setUsableMoney(sumUsableMoney);
		financeBankPO.setFreezeMoney(minFreezeMoney);
		financeManageMapper.updateFreezeMoneyUsableMoney(financeBankPO);
		
		
		financeBankPO.setState(States.OUT_ACCOUNT_DEFEAT);//状态改为：出账失败 10
		System.out.println(financeBankPO.getSerialnumber()+"eeeeeeeee"+financeBankPO.getState());
	    financeManageMapper.updateOutGoldenState(financeBankPO);
		
		//银行返回结果提现失败信息  也上区块链  传个false给陈茂北  说明是提款失败
		Long currentTime = System.currentTimeMillis();//得到当前系统时间
		UserDetailDTO userDetailDTO = financeBankPO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
		builder.setRecordId(financeBankPO.getMoneyFlowNo());//流水号
		builder.setFlag(false);
		CashRecord cashRecord = builder.build();     //这里是出金失败后得上链
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.confirmWithdraw(operatorId,operatorPubKey, operatorPriKey,cashRecord);
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.REVIEWWITHDRAW.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.REVIEWWITHDRAW.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
		
		
	}





	



	
	
	
}
