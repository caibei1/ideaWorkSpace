package com.fuzamei.task;
import com.fuzamei.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fuzamei.pojo.bo.TaskBillOrderBO;
import com.fuzamei.pojo.bo.TaskTransactionFlowBO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.service.TaskTimeOutService;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.ProtoBuf4SM2Util;

public class TimeOutTask {

    @Autowired
    private TaskTimeOutService timeOut;

    private final static int[] billModifyState={};
    private final static int[] BillOrderModifyState={3,6};
    private final static int[] pledgeModifyState={56,74};
    private final static String  pattern = "yyyyMMdd";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    // TODO 申请质押预期
    private final static int applyPledgeOvedueState = 76;
    // TODO 回购逾期
    private final static int pledgeOverdueState = 75;
    // TODO 账单逾期
    private final static int OverdueState   = 42;

    public void taskInit() throws ParseException{
        System.out.println("定时器启动,启动时间:"+ DateUtil.getBeijingTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss sss"));
        long currentTime = DateUtil.getUTCTime(sdf.format(new Date()), pattern);
//		List<BillOrderPO> timeOutBillList = llService.getTimeOutBill(currentTime);
        List<TaskBillOrderBO> timeOutList = timeOut.TimeOutBill(currentTime);
//		Set<BillOrderPO> timeOutBillSet = new HashSet<BillOrderPO>(timeOutBillList.size()+timeOutList.size());
        Set<TaskBillOrderBO> timeOutPledgeSet = new HashSet<TaskBillOrderBO>(timeOutList.size());
//		Set<BillOrderPO> timeOutNotPledgeSet = new HashSet<BillOrderPO>(timeOutList.size());
//		Set<BillOrderPO> timeOutBillOrderSet = new HashSet<BillOrderPO>(timeOutList.size());
        
        if(timeOutList == null || timeOutList.size() == 0){
            return;
        }
        for (TaskBillOrderBO billOrder : timeOutList) {
            timeOutPledgeSet.add(billOrder);
        }
        try{
            updatePledgeState(timeOutPledgeSet);
        }catch (Exception e) {
            System.out.println("执行质押定时器出错");
            e.printStackTrace();
        }
//		for (int i = 0; i < timeOutList.size(); i++) {
//			Long billEndDate = timeOutList.get(i).getBillEndDate();
//			Long buyBackDate = timeOutList.get(i).getBuyBackDate();
//			Integer billOrderState = timeOutList.get(i).getbOrderStateId();
//
//			// TODO 质押
//			if(buyBackDate != null){
//				// TODO 质押被摘取
//				if(valid(billOrderState, pledgeModifyState))
//					timeOutPledgeSet.add(timeOutList.get(i));
//				// TODO 质押没有摘取
//				else
//					timeOutNotPledgeSet.add(timeOutList.get(i));
//				// TODO 判断回购日是否和承兑日相等
//				if(billEndDate == buyBackDate)
//					timeOutBillSet.add(timeOutList.get(i));
//			}
//			// TODO 持有
//			else if(valid(billOrderState, BillOrderModifyState)){
//				timeOutBillOrderSet.add(timeOutList.get(i));
//				timeOutBillSet.add(timeOutList.get(i));
//			}
//		}
//		for (BillOrderPO billOrder : timeOutBillList) {
//			timeOutBillSet.add(billOrder);
//		}
//		try{
//			updatePledgeState(timeOutPledgeSet);
//		}catch (Exception e) {
//			System.out.println("执行质押定时器出错");
//		}
//		try{
//			updateNotPledgeState(timeOutNotPledgeSet);
//		}catch (Exception e) {
//			System.out.println("执行申请质押定时器出错");
//		}
//		try{
//			updateBillOrderState(timeOutBillOrderSet);
//		}catch (Exception e) {
//			System.out.println("执行持有定时器出错");
//		}
//		try{
//			updateBillState(timeOutBillSet);
//		}catch (Exception e) {
//			System.out.println("执行审核中定时器出错");
//		}
        return;
    }


    /**
     * @Description: 质押被摘取定时器方法
     * @throws ParseException
     * @author WangBin
     */
    @Transactional(timeout=10)
    public void updatePledgeState(Set<TaskBillOrderBO> timeOutPledgeSet){
        Set<Integer> set = new HashSet<Integer>(timeOutPledgeSet.size());
        for (TaskBillOrderBO billOrder : timeOutPledgeSet) {
            set.add(billOrder.getBOrderId());
        }
        long instructionId = new SecureRandom().nextLong();
        timeOut.saveTaskPledgeState(pledgeOverdueState, set, instructionId);
        timeOut.batchAddLogMessage(javaBeanTOJavaBean(timeOutPledgeSet, instructionId));
        ProtoBufBean protobufBean = ProtoBuf4SM2Util.overdue(set, instructionId);
        String blockChainResult = BlockChainUtil.sendPost(protobufBean);
        System.out.println("签名hash:"+protobufBean.getInstructionId());
        System.out.println("签名数据:" +protobufBean.getSignature());
        System.out.println("区块链返回的结果："+blockChainResult);
        boolean valiResult = BlockChainUtil.checkResult(blockChainResult);
        if(valiResult!=true)
            throw new RuntimeException();
		System.out.println("执行定时器成功:"+ DateUtil.getBeijingTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss sss"));
    }

    /**
     * @Description: 质押未被摘取定时器方法
     * @author WangBin
     */
    @Transactional(timeout=10)
    public void updateNotPledgeState(Set<TaskBillOrderBO> timeOutNotPledgeSet){
        Set<Integer> set = new HashSet<Integer>(timeOutNotPledgeSet.size());
        for (TaskBillOrderBO billOrder : timeOutNotPledgeSet) {
            set.add(billOrder.getBOrderId());
        }
        long instructionId = new SecureRandom().nextLong();
    }

    /**
     * @Description: 持有中定时器方法
     * @author WangBin
     */
    @Transactional(timeout=10)
    public void updateBillOrderState(Set<TaskBillOrderBO> timeOutBillOrderSet){
        Set<Integer> set = new HashSet<Integer>(timeOutBillOrderSet.size());
        for (TaskBillOrderBO billOrder : timeOutBillOrderSet) {
            set.add(billOrder.getBOrderId());
        }
        long instructionId = new SecureRandom().nextLong();
    }

    /**
     * @Description: 审核中的应收账定时器方法
     * @author WangBin
     */
    @Transactional(timeout=10)
    public void updateBillState(Set<TaskBillOrderBO> timeOutBillSet){
        Set<Integer> set = new HashSet<Integer>(timeOutBillSet.size());
        for (TaskBillOrderBO billOrder : timeOutBillSet) {
            set.add(billOrder.getBId());
        }
        long instructionId = new SecureRandom().nextLong();
    }

    /**
     * @Description: 质押中  po 转换操作
     * @param set
     * @param instructionId
     * @return
     * @author WangBin
     */
    private List<TaskTransactionFlowBO> javaBeanTOJavaBean(Set<TaskBillOrderBO> set, long instructionId){
        List<TaskTransactionFlowBO> transactionFlowList = new ArrayList<TaskTransactionFlowBO>(set.size());
        for (TaskBillOrderBO billOrder : set) {
            TaskTransactionFlowBO transactionFlow = new TaskTransactionFlowBO();
            transactionFlow.setTransactionFlowId(null);
            transactionFlow.setBId(billOrder.getBId());
            transactionFlow.setBOrderId(billOrder.getBOrderId());
            transactionFlow.setTransactionFlowOutEnterprise(billOrder.getBOrderOutEnterprise());
            transactionFlow.setTransactionFlowOutEnterpriseName(billOrder.getBOrderOutEnterpriseName());
            transactionFlow.setTransactionFlowInEnterprise(billOrder.getBOrderInEnterprise());
            transactionFlow.setTransactionFlowInEnterpriseName(billOrder.getBOrderInEnterpriseName());
            transactionFlow.setTransactionFlowMoney(billOrder.getBOrderMoney());
            transactionFlow.setTransactionFlowTransactionTime(billOrder.getBOrderCreateTime());
            transactionFlow.setTransactionFlowDiscountRate(billOrder.getBillOrderBorrowingRate());
            transactionFlow.setTransactionFlowDiscountCharges(billOrder.getDiscountCharges());
            transactionFlow.setTransactionFlowStateId(pledgeOverdueState);
            transactionFlow.setTransactionFlowBusinessId(billOrder.getBOrderOperatorType());
            transactionFlow.setTransactionFlowHash(String.valueOf(instructionId));
            transactionFlow.setTransactionFlowBuyBackDate(billOrder.getBuyBackDate());
            transactionFlowList.add(transactionFlow);
        }
        return transactionFlowList;
    }

    private boolean valid(int state, int[] states){
        for (int i = 0; i < states.length; i++) {
            if(state == states[i])
                return true;
        }
        return false;
    }
}
