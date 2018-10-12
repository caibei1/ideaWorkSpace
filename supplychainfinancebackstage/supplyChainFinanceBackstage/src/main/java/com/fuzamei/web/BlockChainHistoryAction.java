package com.fuzamei.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.service.BlockChainHistoryService;
import com.fuzamei.service.UserService;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController
@RequestMapping(value="/blockChainHistory")
public class BlockChainHistoryAction {
	
	@Autowired
	private BlockChainHistoryService blockChainHistoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HttpServletRequest req;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainHistoryAction.class);
	
	/**
	 * @Title queryHistory
	 * @Description: TODO(查询区块链上链的操作信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"page":1,
	 	"rowNum":10,
	 	"operationTypeId":"",
	 	"startTime":"",
	 	"endTime":""
	 }
	 */
	@RequestMapping(value="/queryHistory",method = RequestMethod.POST)
	private ResultResp queryHistory(@RequestBody BlockChainHistoryBO bloclBlockChainHistoryBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_HISTORY.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(bloclBlockChainHistoryBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(bloclBlockChainHistoryBO.getRowNum(), 1);
			bloclBlockChainHistoryBO.setStartPage((page-1)*rowNum);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(bloclBlockChainHistoryBO.getStartTime(), 0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(bloclBlockChainHistoryBO.getEndTime(), Long.MAX_VALUE);
			bloclBlockChainHistoryBO.setStartTime(startTime);
			if(startTime>endTime) bloclBlockChainHistoryBO.setEndTime(Long.MAX_VALUE);
			else bloclBlockChainHistoryBO.setEndTime(endTime);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = blockChainHistoryService.queryHistory(bloclBlockChainHistoryBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
}
