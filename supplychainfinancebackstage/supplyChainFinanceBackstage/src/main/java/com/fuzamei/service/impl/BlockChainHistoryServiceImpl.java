package com.fuzamei.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.vo.BlockChainHistoryVO;
import com.fuzamei.service.BlockChainHistoryService;
import com.fuzamei.util.PageDTO;

@Service
public class BlockChainHistoryServiceImpl implements BlockChainHistoryService {
	
	@Autowired
	private BlockChainHistoryMapper blockChainHistoryMapper;

	@Override
	public PageDTO queryHistory(BlockChainHistoryBO bloclBlockChainHistoryBO) {
		List<BlockChainHistoryVO> blockChainHistoryVOList =  blockChainHistoryMapper.queryHistory(bloclBlockChainHistoryBO);
		int count = blockChainHistoryMapper.queryHistoryCount(bloclBlockChainHistoryBO);
		return PageDTO.getPagination(count, blockChainHistoryVOList);
	}
	
	
}
