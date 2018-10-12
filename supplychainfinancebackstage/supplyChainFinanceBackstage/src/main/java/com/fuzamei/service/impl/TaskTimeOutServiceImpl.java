package com.fuzamei.service.impl;

import com.fuzamei.mapper.TaskManagermentMapper;
import com.fuzamei.pojo.bo.TaskBillOrderBO;
import com.fuzamei.pojo.bo.TaskTransactionFlowBO;
import com.fuzamei.service.TaskTimeOutService;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskTimeOutServiceImpl implements TaskTimeOutService{

    @Autowired
    private TaskManagermentMapper taskMapper;

    @Override
    public List<TaskBillOrderBO> TimeOutBill(long currentTime) {
        return taskMapper.TimeOutBill(currentTime);
    }

    @Override
    public int saveTaskPledgeState(int pledgeOverdueState, Set<Integer> billOrderId, long hash) {
        int ROWCOUNT = -1;
        try{
            ROWCOUNT = taskMapper.saveTaskPledgeState(pledgeOverdueState, billOrderId, hash);
        }catch (Exception e){
            throw new RuntimeException();
        }
        if(ROWCOUNT < billOrderId.size())
            throw new RuntimeException();
        return ROWCOUNT;
    }

    @Override
    public int batchAddLogMessage(List<TaskTransactionFlowBO> transactionFlow) {
        int ROWCOUNT = -1;
        try{
            ROWCOUNT = taskMapper.batchAddLogMessage(transactionFlow);
        }catch (Exception e){
            throw new RuntimeException();
        }
        if(ROWCOUNT < transactionFlow.size())
            throw new RuntimeException();
        return ROWCOUNT;
    }
}
