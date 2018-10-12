package com.fuzamei.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.fuzamei.constant.OperationHistoryEnum;
import com.fuzamei.constant.Path;
import com.fuzamei.constant.StateEnum;
import com.fuzamei.constant.Statuses;
import com.fuzamei.mapper.BackAttachmentMapper;
import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.mapper.VentureManagementMapper;
import com.fuzamei.pojo.bo.BackAttachmentBO;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.bo.BillOrderBO;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.bo.CreditBO;
import com.fuzamei.pojo.bo.TongdunBO;
import com.fuzamei.pojo.dto.BackAttachmentDTO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.BillOrderPO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.pojo.tongdun.PersonalInfoTD;
import com.fuzamei.pojo.tongdun.TongdunResultTD;
import com.fuzamei.pojo.vo.BillOrderVO;
import com.fuzamei.pojo.vo.BillVO;
import com.fuzamei.pojo.vo.CreditVO;
import com.fuzamei.pojo.vo.TongdunVO;
import com.fuzamei.service.VentureManagementService;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.FileTransferUtil;
import com.fuzamei.util.HashUtil;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ProtoBuf4SM2Util;
import com.fuzamei.util.RandomUtil;
import com.fuzamei.util.TongdunUtil;
import com.fuzamei.util.ZipUtil;
@Service
public class VentureManagementServiceImpl implements VentureManagementService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VentureManagementServiceImpl.class);
	
	private static final ReentrantLock LOCK = new ReentrantLock();
	
	private static final String BASE_PATH = Path.BASE_PATH;//文件根路径
	
	private static final String RESPONSEBLE_ZIP_PATH = Path.RESPONSEBLE_ZIP_PATH;////尽职调查报告压缩文件的文件夹路径
	
	@Autowired
	private VentureManagementMapper ventureManagementMapper;
	
	@Autowired
	private BackAttachmentMapper backAttachmentMapper;
	
	@Autowired
	private BlockChainHistoryMapper blockChainHistoryMapper;
	
	/**
	 * 资产管理模块中查询待审核信息
	 * 未审核的信息包括未审核状态0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageDTO queryToBeVerified(BillBO billBO) {
		billBO.setStatusId(Statuses.UNVERIFIED);//sql查询要选定0这个状态
		List<Integer> billIds = ventureManagementMapper.queryToBeVerifiedBillIds(billBO);//根据条件查询分页的前几条数据的billIds
		if(billIds.size()==0){
			return PageDTO.getPagination(0, new ArrayList());
		}
		billBO.setBillIds(billIds);
		List<BillVO> billVOList=ventureManagementMapper.queryToBeVerified(billBO);
		int total = ventureManagementMapper.queryToBeVerifiedCount(billBO);
		return PageDTO.getPagination(total, billVOList);
	}
	
	/**
	 * 资产管理模块中查询已审核信息
	 * 已审核的信息包括已通过状态1和已拒绝状态2
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageDTO queryVerified(BillBO billBO) {
		billBO.setStatusId(Statuses.UNVERIFIED);//sql查询要排除0的这个状态
		List<Integer> billIds = ventureManagementMapper.queryVerifiedBillIds(billBO);//根据条件查询分页的前几条数据的billIds
		if(billIds.size()==0){
			return PageDTO.getPagination(0, new ArrayList());
		}
		billBO.setBillIds(billIds);
		List<BillVO> billVOList=ventureManagementMapper.queryVerified(billBO);
		int total = ventureManagementMapper.queryVerifiedCount(billBO);
		return PageDTO.getPagination(total, billVOList);
	}

	/**
	 * 质押融资管理模块中通过状态id和付款企业名称两个条件查询信息
	 */
	@Override
	public PageDTO queryBillOrder(BillOrderBO billOrderBO) {
		List<BillOrderVO> billOrderVOList = ventureManagementMapper.queryBillOrder(billOrderBO);
		int total = ventureManagementMapper.queryBillOrderCount(billOrderBO);
		for (BillOrderVO billOrderVO : billOrderVOList) {//把状态名设置上
			Integer status = billOrderVO.getStatus();
			StateEnum[] values = StateEnum.values();
			for (StateEnum stateEnum : values) {
				if(stateEnum.getStatus()==status){
					billOrderVO.setStatusName(stateEnum.getStatusName());
				}
			}
		}
		return PageDTO.getPagination(total, billOrderVOList);
	}

	@Override
	public BillDTO queryBillById(BillBO billBO) {
		return ventureManagementMapper.queryBillById(billBO);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void approveBill(BillBO billBO) {
		long currentTime = System.currentTimeMillis();
		billBO.setStatusId(Statuses.APPROVED);//表示审核同意-->状态改为已同意
		int version = ventureManagementMapper.queryBillVersionById(billBO);//查询版本号version
		billBO.setVersion(version);//版本号
		int success = ventureManagementMapper.verifyBillById(billBO);//bill表改状态
		if(success==0){
			throw new RuntimeException("同意操作失败");//可以提高并发量
		}
		//生成一个随机的billOrderId，但是要在bill_order表中存在不重复的id号
		int billOrderId = RandomUtil.getRandomInteger(9);
		int count = ventureManagementMapper.checkIfHaveBillOrderById(billOrderId);
		if(count!=0){
			throw new RuntimeException("bill_order_id出现重复");
		}
		BillDTO billDTO = billBO.getBillDTO();
		BillOrderPO billOrderPO = new BillOrderPO();//将BillDTO转储到BillOrderPO中，再传到数据库的bill_order表中
		billOrderPO.setApproveMoney(billBO.getApproveMoney());
		billOrderPO.setBillId(billDTO.getBillId());
		billOrderPO.setBillMoney(billDTO.getBillMoney());
		billOrderPO.setBillOrderId(billOrderId);
		billOrderPO.setCreateTime(currentTime);
		billOrderPO.setUpdateTime(currentTime);
		billOrderPO.setEndTime(billDTO.getEndTime());
		billOrderPO.setInterestRate(billDTO.getInterestRate());
		billOrderPO.setPayedEnterpriseId(billDTO.getPayedEnterpriseId());
		billOrderPO.setPayedEnterpriseName(billDTO.getPayedEnterpriseName());
		billOrderPO.setReceivedEnterpriseId(billDTO.getReceivedEnterpriseId());
		billOrderPO.setReceivedEnterpriseName(billDTO.getReceivedEnterpriseName());
		billOrderPO.setStatus(3);//3表示持有中
		billOrderPO.setBillOrderHash(billDTO.getBillHash());//插入billHash
		int countToBillOrder = ventureManagementMapper.insertToBillOrder(billOrderPO);//将bill表中的信息转储到bill_order表中
		if(countToBillOrder==0){
			throw new RuntimeException("插入billOrder数据失败");
		}
		Integer accountId = ventureManagementMapper.queryAccountIdByEnterpriseId(billBO);//查询企业下面唯一的一个账户id(后面有可能一个账户下会有多个账户id)
		if(accountId==null){
			throw new RuntimeException("账户id不存在");
		}
		billBO.setAccountId(accountId);
		int countToAsset = ventureManagementMapper.insert2BillAsset(billBO);//将approveMoney加到BillAsset中的useable_order_money字段
		if(countToAsset==0){
			throw new RuntimeException("更新可用余额失败");
		}
		//获取所有附件信息，先压缩，后计算压缩包的文件hash值(先将压缩文件插入数据库)
		Integer billId = billBO.getBillId();
		BackAttachmentBO backAttachmentBO = new BackAttachmentBO();//用于插入附件关联表创建的对象
		backAttachmentBO.setAttachmentId(billId+""+currentTime);
		backAttachmentBO.setAttachmentName("尽调报告附件汇总.zip");
		backAttachmentBO.setAttachmentType(".zip");
		backAttachmentBO.setAttachmentUrl(RESPONSEBLE_ZIP_PATH+"/"+backAttachmentBO.getAttachmentId()+backAttachmentBO.getAttachmentType());
		backAttachmentBO.setCreateTime(currentTime);
		backAttachmentBO.setOperatorId(billBO.getUserDetailDTO().getUserId());
		backAttachmentBO.setUpdateTime(currentTime);
		backAttachmentMapper.insert2BackAttachmentBill(billId, Arrays.asList(new BackAttachmentBO[]{backAttachmentBO}));//(压缩文件的关联信息)插入关联表
		backAttachmentMapper.insert2BackAttachment(Arrays.asList(new BackAttachmentBO[]{backAttachmentBO}));//(压缩文件信息)插入附件表
		//将所有附件压缩到指定目录的压缩文件中
		List<BackAttachmentDTO> attachmentList = billBO.getAttachmentList();
		List<File> fileList = new ArrayList<File>();
		for (BackAttachmentDTO attachmentInfo : attachmentList) {
			String filepath = BASE_PATH + attachmentInfo.getAttachmentUrl();
			File file = new File(filepath);
			fileList.add(file);
		}
		ZipUtil.compressFiles(fileList.toArray(new File[0]), BASE_PATH + backAttachmentBO.getAttachmentUrl(), "");//压缩所有billId下的文件
		String zipFileHash = HashUtil.getFileMD5(BASE_PATH + backAttachmentBO.getAttachmentUrl());//计算出压缩文件的文件hash值
		//【准备上区块链】
		UserDetailDTO userDetailDTO = billBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String assetId = String.valueOf(billId);
		Long validCredit = (long)(billBO.getApproveMoney()*10000);
		String docHash = zipFileHash;
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.examineAsset(operatorId,operatorPubKey, operatorPriKey, assetId,true,validCredit, docHash);
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.EXAMINEASSET.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.EXAMINEASSET.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			new File(BASE_PATH + backAttachmentBO.getAttachmentUrl()).delete();//出错将压缩文件删除，防止出现垃圾文件
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void rejectBill(BillBO billBO) {
		long currentTime = System.currentTimeMillis();
		billBO.setStatusId(Statuses.REJECTED);//表示审核拒绝-->状态改为已拒绝
		int version = ventureManagementMapper.queryBillVersionById(billBO);//查询版本号version
		billBO.setVersion(version);//版本号
		int success = ventureManagementMapper.rejectBillById(billBO);
		if(success==0){
			throw new RuntimeException("拒绝操作失败");//可以提高并发量
		}
		//【准备上区块链】
		UserDetailDTO userDetailDTO = billBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String assetId = String.valueOf(billBO.getBillId());
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.examineAsset(operatorId,operatorPubKey, operatorPriKey, assetId, false, null, null);
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.EXAMINEASSET.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.EXAMINEASSET.getType());
		blockChainHistoryBO.setCreateTime(currentTime);
		blockChainHistoryBO.setHash(protoBufBean.getInstructionId());
		blockChainHistoryMapper.createHistory(blockChainHistoryBO);
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(!checkResult){
			throw new RuntimeException(BlockChainUtil.getErrorMessage(result));//抛出具体的区块链错误信息
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void insertResponsible2Attachment(List<BackAttachmentBO> attachmentList, List<File> attachmentFileList,
			MultipartFile[] files, Integer userId, Integer billId, String directoryPath) {
		backAttachmentMapper.insert2BackAttachment(attachmentList);//先插入附件表
		backAttachmentMapper.insert2BackAttachmentBill(billId,attachmentList);//插入附件单据关联表
		FileTransferUtil.transferMultiple(files, attachmentFileList, directoryPath);
	}

	@Override
	public int checkIfHaveBill(Integer billId) {
		return ventureManagementMapper.checkIfHaveBill(billId);
	}

	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public void setEnterpriseCreditLine(CreditBO creditBO) {
		long currentTime = System.currentTimeMillis();
		creditBO.setAuthorizeTime(currentTime);
		EnterprisePO enterprisePO = ventureManagementMapper.queryEnterpriseCreditInfoById(creditBO);//先查询企业的授信信息
		if(enterprisePO==null){
			throw new RuntimeException("该企业信息不存在");
		}
		if(enterprisePO.getCreditLine()==null){//说明企业第一次设置授信额度
			ventureManagementMapper.setEnterpriseCreditLine4FirstTime(creditBO);
		}else{
			ventureManagementMapper.setEnterpriseCreditLine(creditBO);//说明是再次给企业设定额度
		}
		//【准备上区块链：设置企业用户信用总额度】
		UserDetailDTO userDetailDTO = creditBO.getUserDetailDTO();
		String operatorId = String.valueOf(userDetailDTO.getUserId());
		String operatorPubKey = userDetailDTO.getPublicKey();
		String operatorPriKey = userDetailDTO.getPrivateKey();
		String userUid = String.valueOf(creditBO.getReceivedEnterpriseId());
		long totalAmount = (long) (creditBO.getCreditLine()*100);
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.SetCredit(operatorId,operatorPubKey, operatorPriKey, userUid, totalAmount);
		//【插入操作记录表】
		BlockChainHistoryBO blockChainHistoryBO = new BlockChainHistoryBO();
		blockChainHistoryBO.setOperatorId(userDetailDTO.getUserId());
		blockChainHistoryBO.setOperationTypeId(OperationHistoryEnum.SETCREDIT.getTypeId());
		blockChainHistoryBO.setOperationType(OperationHistoryEnum.SETCREDIT.getType());
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
	 * 从数据库查询同盾结果的service方法，如果改良过的json查不到，再去找原始json
	 */
	@Override
	@Transactional(rollbackFor=Exception.class,timeout=30)
	public TongdunResultTD queryTongdunResult(TongdunBO tongdunBO) {
		TongdunVO prettyTongdunVO = ventureManagementMapper.queryPrettyTongdunResult(tongdunBO);
		if(prettyTongdunVO==null) {
			throw new RuntimeException("该借款企业公司的同盾信息不存在");
		}
		if(prettyTongdunVO.getPrettyJson()==null || "".equals(prettyTongdunVO.getPrettyJson().trim())){
			//如果改良json不存在，再去找原始的json去解析
			TongdunVO uglyTongdunVO = ventureManagementMapper.queryUglyTongdunResult(tongdunBO);
			String uglyJSON = uglyTongdunVO.getTongdunResult();//获取原始json
			//个人信息对象创建好
			PersonalInfoTD personalInfoTD = new PersonalInfoTD();
			personalInfoTD.setEnterpriseName(uglyTongdunVO.getEnterpriseName());
			personalInfoTD.setIdCardNum(uglyTongdunVO.getIdCardNum());
			personalInfoTD.setPersonName(uglyTongdunVO.getPersonName());
			personalInfoTD.setPhoneNum(uglyTongdunVO.getPhoneNum());
			
			TongdunResultTD tongdunResultTD = TongdunUtil.parseToJSON(uglyJSON, personalInfoTD, uglyTongdunVO.getReportTime());
			tongdunBO.setPrettyJson(JSON.toJSONString(tongdunResultTD));
			//将改良好的json插入数据库，方便下次直接使用
			ventureManagementMapper.insertPrettyTongdunResult(tongdunBO);
			return tongdunResultTD;
		}
		TongdunResultTD tongdunResultTD = JSON.parseObject(prettyTongdunVO.getPrettyJson(), TongdunResultTD.class);
		tongdunResultTD.setReportTime(prettyTongdunVO.getReportTime());//设置一个报告时间
		tongdunResultTD.getPersonalInfoTD().setEnterpriseName(prettyTongdunVO.getEnterpriseName());
		tongdunResultTD.getPersonalInfoTD().setIdCardNum(prettyTongdunVO.getIdCardNum());
		tongdunResultTD.getPersonalInfoTD().setPersonName(prettyTongdunVO.getPersonName());
		tongdunResultTD.getPersonalInfoTD().setPhoneNum(prettyTongdunVO.getPhoneNum());
		return tongdunResultTD;
	}

	/**
	 * 同盾结果管理的页面信息查询
	 */
	@Override
	public PageDTO queryAllEnterpriseTongdunInfo(TongdunBO tongdunBO) {
		List<TongdunVO> tongdunVOList = ventureManagementMapper.queryAllEnterpriseTongdunInfo(tongdunBO);
		int total = ventureManagementMapper.queryAllEnterpriseTongdunInfoCount(tongdunBO);
		return PageDTO.getPagination(total, tongdunVOList);
	}

	@Override
	public PageDTO queryEnterpriseCreditLine(CreditBO creditBO) {
		List<CreditVO> creditVOList = ventureManagementMapper.queryEnterpriseCreditLine(creditBO);
		for (CreditVO creditVO : creditVOList) {
			if(creditVO.getCreditLine()==null){
				creditVO.setCreditLine(0d);//授信额
				creditVO.setConsumedLoan(0d);//已使用额度
				creditVO.setTobePayedLoan(0d);//待还款额度
				creditVO.setUnconsumedLoan(0d);//未使用额度
				creditVO.setTotalRepayment(0d);//总还款额
			}else{
				Double creditLine = creditVO.getCreditLine()==null ? 0d : creditVO.getCreditLine();
				Double consumedLoan = creditVO.getConsumedLoan()==null ? 0d : creditVO.getConsumedLoan();
				creditVO.setUnconsumedLoan(creditLine-consumedLoan);//未使用额度
				creditVO.setTobePayedLoan(consumedLoan);//待还款额度
			}
		}
		int total = ventureManagementMapper.queryEnterpriseCreditLineCount(creditBO);
		return PageDTO.getPagination(total, creditVOList);
	}

	@Override
	public Double getBillAvailableTotalAmountById(BillBO billBO) {
		billBO.setCurrentTime(System.currentTimeMillis());//设置好当前时间
		return ventureManagementMapper.getBillAvailableTotalAmountById(billBO);
	}

	@Override
	public Double getUsedMoneyByEnterpriseId(CreditBO creditBO) {
		creditBO.setStatus(56);//状态是质押中
		return ventureManagementMapper.getUsedMoneyByEnterpriseId(creditBO);
	}

	@Override
	public Double getTotalReturnedMoneyByEnterpriseId(CreditBO creditBO) {
		creditBO.setStatus(60);//状态是已回购
		return ventureManagementMapper.getTotalReturnedMoneyByEnterpriseId(creditBO);
	}

	@Override
	public EnterprisePO queryLeftoverLoan(BillBO billBO) {
		return ventureManagementMapper.queryLeftoverLoan(billBO);
	}

	@Override
	public Integer queryBillStatusById(Integer billId) {
		return ventureManagementMapper.queryBillStatusById(billId);
	}
}
