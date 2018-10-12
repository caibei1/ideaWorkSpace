package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;

import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.vo.BlockChainHistoryVO;

public interface BlockChainHistoryMapper {
	
	@Insert("insert into back_blockchain_history(operator_id,operation_type_id,operation_type,hash,create_time) "
			+ "values(#{operatorId},#{operationTypeId},#{operationType},#{hash},#{createTime})")
	void createHistory(BlockChainHistoryBO blockChainHistoryBO);

	List<BlockChainHistoryVO> queryHistory(BlockChainHistoryBO bloclBlockChainHistoryBO);

	int queryHistoryCount(BlockChainHistoryBO bloclBlockChainHistoryBO);
	
}
