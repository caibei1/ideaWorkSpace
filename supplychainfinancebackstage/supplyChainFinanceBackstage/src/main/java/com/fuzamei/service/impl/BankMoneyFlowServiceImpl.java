package com.fuzamei.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fuzamei.constant.PubConstants.BankMoneyFlowOperationType;
import com.fuzamei.constant.PubConstants.BankMoneyFlowState;
import com.fuzamei.mapper.BankMoneyFlowMapper;
import com.fuzamei.pojo.bean.BankFlowDataBean;
import com.fuzamei.pojo.bean.BankFlowResultBean;
import com.fuzamei.pojo.po.BankMoneyFlowPO;
import com.fuzamei.pojo.vo.BankMoneyFlowVO;
import com.fuzamei.pojo.vo.EnterpriseVO;
import com.fuzamei.service.BankMoneyFlowService;
import com.fuzamei.service.EnterpriseService;
import com.fuzamei.util.DateUtil;
import com.fuzamei.util.blockChain.ProtobufBean;
import com.fuzamei.util.blockChain.ProtobufUtilsSM2;

@Service("BankMoneyFlowService")
public class BankMoneyFlowServiceImpl implements BankMoneyFlowService{
	
	private static final Logger logger = LoggerFactory.getLogger(BankMoneyFlowServiceImpl.class);

    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    BankMoneyFlowMapper bankMoneyFlowMapper;
	
    @Transactional
	public Integer insert(BankMoneyFlowVO bankMoneyFlowVO) {
		Integer result = 0 ;
		BankMoneyFlowPO bankMoneyFlowPO = new BankMoneyFlowPO();
		BeanUtils.copyProperties(bankMoneyFlowVO, bankMoneyFlowPO);
		try {
			result = bankMoneyFlowMapper.insert(bankMoneyFlowPO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return result ;
	}

    @Transactional(rollbackFor=Exception.class,timeout=30)
	public List<String> addBankRecharge(BankFlowResultBean bankFlowResultBean) {
    	List<BankFlowDataBean> list = new ArrayList<BankFlowDataBean>();
    	List<String> resultList = new ArrayList<String>();
//    	ProtobufBean protobufBean =  new ProtobufBean();
    	list  = bankFlowResultBean.getData();
    	if(list!=null){
    			//校验data是否为null
    		for(BankFlowDataBean bankFlowDataBean : list){
    			//校验出入金类型
    			if(("1").equals(bankFlowDataBean.getFlag1()) && bankFlowDataBean.getBflow()!=null){
    				BankMoneyFlowVO bankMoneyFlowVO = new BankMoneyFlowVO();//拿到建行所给数据
            		bankMoneyFlowVO.setBankCard(bankFlowDataBean.getAccno2());
            		bankMoneyFlowVO.setAmount(Double.parseDouble(bankFlowDataBean.getAmt()));
            		bankMoneyFlowVO.setBankTransactionNo(bankFlowDataBean.getTran_FLOW());
            		bankMoneyFlowVO.setMoneyFlowNo(String.valueOf(System.currentTimeMillis()*1000+this.getRandom(3)));
            		bankMoneyFlowVO.setBankName(bankFlowDataBean.getCadbank_Nm());
            		bankMoneyFlowVO.setCreateTime(Long.parseLong(DateUtil.date2TimeStamp(bankFlowDataBean.getCreattime(), "yyyy/MM/dd HH:mm:ss")));
            		bankMoneyFlowVO.setCreateBy(bankFlowDataBean.getAcc_NAME1());
            		bankMoneyFlowVO.setOperationType(BankMoneyFlowOperationType.RECHARGE);
            		bankMoneyFlowVO.setState(BankMoneyFlowState.CHECK_PENDING);
            		bankMoneyFlowVO.setMemorandum(bankFlowDataBean.getDet());
            		//校验记录是否存在
            		if(this.countByBankTransactionNo(bankMoneyFlowVO.getBankTransactionNo())>0){
            			logger.info(bankMoneyFlowVO.getBankTransactionNo()+"is exit");
            			resultList.add(bankMoneyFlowVO.getBankTransactionNo());
            		}else{
            		//自动划拨
            		EnterpriseVO enterprise = new EnterpriseVO();
            		try {
		            	enterprise.setEnterpriseName(bankFlowDataBean.getAcc_NAME1());
		            	enterprise = enterpriseService.selectEnterpriseByName(enterprise);
		            	if(enterprise==null){
		            		bankMoneyFlowVO.setState(BankMoneyFlowState.HUAPO_PENDING);
		            	}else{
		            		//签名
//	            			String publicKey = bankMoneyFlowMapper.getPublicKey(enterprise.getEnterpriseId());
//	            			protobufBean = ProtobufUtilsSM2.requestApplyDeposit(bankMoneyFlowVO, publicKey);
		            		bankMoneyFlowVO.setEnterpriseId(enterprise.getEnterpriseId());
		            		bankMoneyFlowVO.setEnterpriseName(bankFlowDataBean.getAcc_NAME1());
//		            		bankMoneyFlowVO.setCashFlowHash(String.valueOf(protobufBean.getInstructionId()));
		            	}
					} catch (Exception e) {
							bankMoneyFlowVO.setState(BankMoneyFlowState.HUAPO_PENDING);
						}
            		//添加充值申请
            		Integer result = 0;
            		result = this.insert(bankMoneyFlowVO);
            		if(result>0){
            			resultList.add(bankMoneyFlowVO.getBankTransactionNo());
//            			if(null!=bankMoneyFlowVO.getEnterpriseId()) {
//							this.sendBlockChain(protobufBean);
//							
//            			}
            		}
            		
            		}
    			}
        	}
    	}
    	return resultList;
	}
	

	public int countByBankTransactionNo(String bankTransactionNo) {
		Integer result = 0 ;
		try {
			result = bankMoneyFlowMapper.countByBankTransactionNo(bankTransactionNo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return result ;
	}


	public  int getRandom(int length){
		length = (int) Math.pow(10, length);
		int random = (int) ((Math.random() * 9 + 1) * length) ;
		return random;
	}


	/**
	 * 
	 * @Title: sendBlockChain  
	 * @Description: 发送区块链
	 * @param protobufBean
	 * @throws Exception 
	 */
	public  void sendBlockChain(ProtobufBean protobufBean){
		// 请求区块链
		String resultBlockChain = ProtobufUtilsSM2.sendPostParam(protobufBean);
		if (!ProtobufUtilsSM2.vilaResult(resultBlockChain)) {
			String errorMsg = ProtobufUtilsSM2.analysisBlockChainMsg(resultBlockChain);
			logger.error("send block chain fail msg : " + errorMsg);
			throw new RuntimeException();
		} 
	}
	
	
	
}
