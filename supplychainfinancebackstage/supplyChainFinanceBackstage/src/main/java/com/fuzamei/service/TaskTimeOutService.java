package com.fuzamei.service;

import com.fuzamei.pojo.bo.TaskBillOrderBO;
import com.fuzamei.pojo.bo.TaskTransactionFlowBO;

import java.util.Set;
import java.util.List;

public interface TaskTimeOutService {

    /**
     * @Description: 获取为回购逾期的质押编号
     * @param currentTime 当前日的时间
     * @return
     * @author WangBin
     */
    List<TaskBillOrderBO> TimeOutBill(long currentTime);

    /**
     * @Description: 定时修改质押中的状态
     * @param pledgeOverdueState
     * @param billOrderId
     * @param hash
     * @return
     * @author WangBin
     */
    int saveTaskPledgeState(int pledgeOverdueState, Set<Integer> billOrderId, long hash);

    /**
     * @Description: 批量添加 TransactionFlow  Table 中的信息
     * @param transactionFlow 要批量插入的信息
     * @return
     * @author WangBin
     */
    int batchAddLogMessage(List<TaskTransactionFlowBO> transactionFlow);
}
