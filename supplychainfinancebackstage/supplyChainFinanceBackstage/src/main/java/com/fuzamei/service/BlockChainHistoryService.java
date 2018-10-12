package com.fuzamei.service;

import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.util.PageDTO;

public interface BlockChainHistoryService {

	PageDTO queryHistory(BlockChainHistoryBO bloclBlockChainHistoryBO);

}
