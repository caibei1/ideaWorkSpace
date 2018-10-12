package com.fuzamei.mapper;

import com.fuzamei.pojo.bo.TaskBillOrderBO;
import com.fuzamei.pojo.bo.TaskTransactionFlowBO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

public interface TaskManagermentMapper {

    /**
     * @Description: 获取为回购逾期的质押编号
     * @param currentTime 当前日的时间
     * @return
     * @author WangBin
     */
    List<TaskBillOrderBO> TimeOutBill(@Param("currentTime") long currentTime);

    /**
     * @Description: 定时修改质押中的状态
     * @param pledgeOverdueState
     * @param billOrderId
     * @param hash
     * @return
     * @author WangBin
     */
    int saveTaskPledgeState(@Param("pledgeOverdueState")int pledgeOverdueState,
                            @Param("billOrderId")Set<Integer> billOrderId,
                            @Param("hash")long hash);

    /**
     * @Description: 批量添加 TransactionFlow  Table 中的信息
     * @param transactionFlow 要批量插入的信息
     * @return
     * @author WangBin
     */
    int batchAddLogMessage(@Param("transactionFlow") List<TaskTransactionFlowBO> transactionFlow);
}
