package com.fuzamei.web;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.fuzamei.constant.Auth;
import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.Authes;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.cros.CORSFilter;
import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.Back;
import com.fuzamei.pojo.po.Cinfig;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.po.FinanceBankPO;
import com.fuzamei.pojo.po.QueryBankFlowBean;
import com.fuzamei.pojo.po.Result;
import com.fuzamei.pojo.vo.BankResultVo;
import com.fuzamei.pojo.vo.BankVO;
import com.fuzamei.pojo.vo.FinanceBankExceOutGlodenlOkCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExceOutGlodenlVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenRecordOkVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenRecordVO;
import com.fuzamei.pojo.vo.FinanceBankExcelGoldenVO;
import com.fuzamei.pojo.vo.FinanceBankExcelToAlreadyCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExcelToCheckVO;
import com.fuzamei.pojo.vo.FinanceBankExcelVO;
import com.fuzamei.pojo.vo.FinanceVO;
import com.fuzamei.pojo.vo.IdCardVO;
import com.fuzamei.service.FinanceManageService;
import com.fuzamei.service.UserService;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.DateUtil;
import com.fuzamei.util.Execl;
import com.fuzamei.util.ExportExcel;
import com.fuzamei.util.HttpRequest;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController

@RequestMapping(value="/financeManage")

public class FinanceMangeAction {
	@Autowired
	public  FinanceManageService financeManageService;
	@Autowired
	private HttpServletRequest req;
	@Autowired
	private UserService userService;
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceMangeAction.class);
	private final static String pattern = "yyyy-MM-dd HH:mm:ss";
	/**
	 * (财务查看 入金管理【待审核】)
	 * @param financeBO
	 * @return
	  {
	 	"enterpriseName":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/goldenAudit/queryGolden",method=RequestMethod.POST)
	private ResultResp queryGolden(@RequestBody final FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.GOLDEN_TO_AUDIT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
		    ValidationUtil.checkNullAndAssignString(financeBO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryGolden(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	/**
	 * （财务查看入金管理【已审核（入金通过）】）
	 * {里面应该包括 入金通过 的和入金拒绝的}
	 * @param financeBO
	 * @return
	  {
	 	"enterpriseName":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/goldenAudit/queryGoldenOkCheck",method=RequestMethod.POST)
	private   ResultResp  queryGoldenOkCheck(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.GOLDEN_TO_GOLDEN.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
		    ValidationUtil.checkNullAndAssignString(financeBO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryGoldenOkCheck(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * (当入金得时候 后台财务审核  点击同意后要修改现金流表原先得状态改为[入金通过])      
	 *  同时给账户表得【总资产】加上去 和 可用金额加起来
	 * @param  financePO
	 * @return
		{
	      "enterpriseId":"1007777",
		  "moneyFlowNo":"1001"
	    } 
	 */
	@RequestMapping(value="/goldenAudit/approvebankMoneyFlow",method=RequestMethod.POST)
	private ResultResp approvebankMoneyFlow(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO=userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.GOLDEN_TO_AUDIT_AGREED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinanceBankPO fbp;
		try {
			ValidationUtil.checkAndAssignInt(financeBankPO.getEnterpriseId());
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo());
			fbp= financeManageService.querybankMoneyFlow(financeBankPO);
			if(fbp == null){
				throw new RuntimeException("该信息不存在");
			}
			if(fbp.getState()!=13 && fbp.getState()!=1){//为     这里13代表已划拨   TODO XXX  ？？？1代表待审核 13代表已划拨 列表有两个状态
				throw new RuntimeException("该入金流水单已经审核过了");
			}
			//上面这里
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			fbp.setEnterpriseId(financeBankPO.getEnterpriseId());
			fbp.setUserDetailDTO(userDetailDTO);
			fbp.setMoneyFlowNo(financeBankPO.getMoneyFlowNo());
			fbp.setEnterpriseName(financeBankPO.getEnterpriseName());
			financeManageService.approvebankMoneyFlow(fbp);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	/**
	 {
	   "moneyFlowNo":"1001"
     }
	 * (当入金得时候 后台财务审核  点击拒绝后要修改现金流表原先得状态改为[入金拒绝])【需要调接口】
	 * @param financePO
	 * @return
	 */
	@RequestMapping(value="/goldenAudit/rejectbankMoneyFlow",method=RequestMethod.POST)
	private ResultResp rejectbankMoneyFlow(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		UserDetailDTO userDetailDTO;
		FinanceBankPO fin;
		try {
			userDetailDTO=userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.GOLDEN_TO_AUDIT_REFUSED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		
		try {
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo());
		  fin= financeManageService.querybankMoneyFlow(financeBankPO);
			if(fin==null) {
				throw new RuntimeException("该流水编号不存在");
			}
			if(fin.getState()!=1 && fin.getState()!=13){//13代表已划拨  1代表待审核    如果当前状态不是已划拨说明审核过了   XXX
				throw new RuntimeException("该入金流水已经审核过了");
			}
			if(fin.getOperationType()!=1){
				throw new RuntimeException("该笔流水不是入金的");
			}
			financeBankPO.setMoneyFlowNo(fin.getMoneyFlowNo());
			financeBankPO.setAmount(fin.getAmount());
			financeBankPO.setTranflow(fin.getTranflow());   //银行的流水编号，lys要的
			financeBankPO.setAccno(fin.getAccno());         //银行卡号，lys要的
			financeBankPO.setAccnoname(fin.getAccnoname()); //大银行名称，lys要的
			String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("bank_flow_gloden"), JSON.toJSONString(financeBankPO));
			
			financeBankPO.setUserDetailDTO(userDetailDTO);
			@SuppressWarnings("rawtypes")
			Result res=JSON.parseObject(result,Result.class);//获取lys的数据
			if(!res.udun) {//如果这里请求lys接口 他那边请求不到建行（异常情况下）返给我false 代表就是有异常情况 我这里就不需要往下面执行了,然后网络要是好了  就不走这个if里面了，就正常走流程
		     throw new RuntimeException("请求建行数据网络异常");
	        }
            Back be=res.getData();
            financeBankPO.setSerialnumber(be.getSerialnumber());//返回的serialnumber SET进数据库
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			financeManageService.rejectbankMoneyFlow(financeBankPO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}

	
	/************************************以下出金功能块*********************************************************************************/
	/**
	 * 财务管理查看出金待审核的列表
	 * @param financeBO
	 * @return
	  {
	 	"enterpriseName":"杂",
	 	"rowNum":"10",
	 	"page":"1"
	  }
	 */
	@RequestMapping(value="/outGold/queryOutGold",method=RequestMethod.POST)
	private ResultResp queryOutGold(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
		    ValidationUtil.checkNullAndAssignString(financeBO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryOutGold(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}	
	}
	
	/**
	 * 财务管理查看出金 已审核的列表
	 * (包括初审通过和初审拒绝的)
	 * @param financeBO
	 * @return
     {
	 	"enterpriseName":"杂",
	 	"rowNum":"10",
	 	"page":"1"
	 } 
	 */
	@RequestMapping(value="/outGold/queryOutGoldOkCheck",method=RequestMethod.POST)
	private ResultResp queryOutGoldOkCheck(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_THE_APPROVED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
		    ValidationUtil.checkNullAndAssignString(financeBO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryOutGoldOkCheck(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	/**
	 * 财务管理查看  【出金第一次的待审核】做同意操作
	 * (XXX XXX)
	 * @param financeBankPO
	 * @return
	 {
	   "moneyFlowNo":"1001"
     }
	 */
	@RequestMapping(value="/outGold/approvebankMoneyFlowOutGolden",method=RequestMethod.POST)
	private ResultResp approvebankMoneyFlowOutGolden(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO=userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_AGREED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinanceBankPO fbp;
		try {
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo());
			if(financeBankPO.getMoneyFlowNo()==null){
				throw new RuntimeException("不能为空");
			}
			fbp= financeManageService.querybankMoneyFlowOutGolden(financeBankPO);
			if(fbp == null){
				throw new RuntimeException("该信息不存在");
			}
			if(fbp.getState()!=1){
				throw new RuntimeException("该出金流水已经审核过了");
			}
		
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			fbp.setUserDetailDTO(userDetailDTO);
			fbp.setMoneyFlowNo(financeBankPO.getMoneyFlowNo());
			fbp.setFirstCheckBy(Integer.parseInt(userId));//set初审得人
			financeManageService.approvebankMoneyFlowOutGolden(fbp);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * 财务管理查看  【出金第一次的待审核】做拒绝操作
	 * @param financeBankPO
	 * @return
	 {
	 	"enterpriseId":"1007777",
	    "moneyFlowNo":"1001"
     }
	 */
	@RequestMapping(value="/outGold/rejectbankMoneyFlowOutGolden",method=RequestMethod.POST)
	private ResultResp rejectbankMoneyFlowOutGolden(@RequestBody FinanceBankPO fin){
		String userId = req.getHeader("Authorization").split("&")[1];
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO=userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_REFUSED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinanceBankPO financeBankPO;
		try {
			ValidationUtil.checkAndAssignInt(fin.getEnterpriseId()); 
			ValidationUtil.checkBlankAndAssignString(fin.getMoneyFlowNo());
			if(fin.getMoneyFlowNo()==null){
				throw new RuntimeException("不能为空");
			}
			financeBankPO= financeManageService.querybankMoneyFlowOutGolden(fin);
			if(financeBankPO == null){
				throw new RuntimeException("该信息不存在");
			}
			if(financeBankPO.getState()!=1){
				throw new RuntimeException("该出金流水已经审核过了");
			} 
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			//financeBankPO.setEnterpriseId(fin.getEnterpriseId());//暂
			financeBankPO.setMoneyFlowNo(fin.getMoneyFlowNo());
			financeBankPO.setUserDetailDTO(userDetailDTO);
			financeBankPO.setFirstCheckBy(Integer.parseInt(userId));//set初审得人
			financeManageService.rejectbankMoneyFlowOutGolden(financeBankPO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/********以下是出金待复审的模块*******************************************************************************************/
	/**
	 * 财务主管来查询出金待复审列表
	 * @param financeBO
	 * @return
 	  {
	 	"enterpriseName":"",
	 	"rowNum":"10",
	 	"page":"1"
	  }
	 */
	@RequestMapping(value="/outGoldRecheck/queryToReview",method=RequestMethod.POST)
	private ResultResp queryToReview(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_THE_REVIEW.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
		    ValidationUtil.checkNullAndAssignString(financeBO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryToReview(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * 财务主管来查询出金已复审列表
	 * (包括复审通过和复审拒绝的)
	 * @param financeBO
	 * @return
	   {
	 	"enterpriseName":"杂",
	 	"rowNum":"10",
	 	"page":"1"
	   }
	 */
	@RequestMapping(value="/outGoldRecheck/queryHaveReview",method=RequestMethod.POST)
	private ResultResp queryHaveReview(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
		    ValidationUtil.checkNullAndAssignString(financeBO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryHaveReview(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * 【出金 】【财务主管】在待复审里点击同意操作【第二次复审】
	 *  状态要修改为(复审通过)
	 *  
	 * @param financeBankPO
	 * @return
	  {
       "enterpriseId":"1007777",NO，待改
	   "moneyFlowNo":"1001"
      }
	 * 
	 */
	@RequestMapping(value="/outGoldRecheck/approvebankMoneyFlowOutGoldenToReview",method=RequestMethod.POST)
	private synchronized ResultResp approvebankMoneyFlowOutGoldenToReview(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO=userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_THE_REVIEW_AGREED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinanceBankPO fbp;
		
		try {
			ValidationUtil.checkAndAssignInt(financeBankPO.getEnterpriseId());//企业id
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo());//流水编号
			fbp= financeManageService.querybankMoneyFlowOutGolden(financeBankPO);//待
			if(fbp == null){
				throw new RuntimeException("该信息不存在");
			}
			if(fbp.getState()!=5){//5代表是初审通过   如果不是5就是复审过了
				throw new RuntimeException("该入金流水单已经复审过了");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败2",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			System.out.println(financeBankPO.getEnterpriseId()+"hhh");
			//financeBankPO.setEnterpriseId(fbp.getEnterpriseId());///**暂**/  
			financeBankPO.setSecondCheckBy(Integer.parseInt(userId));//待复审里 点击同意 把复审得人set进数据库
			financeBankPO.setMoneyFlowNo(fbp.getMoneyFlowNo());
			financeBankPO.setAccno(fbp.getAccno());           //银行卡号 lys要的参数
			financeBankPO.setAccnoname(fbp.getAccnoname());   //大银行名称   lys要的参数
			financeBankPO.setUseof(fbp.getUseof());           //备注        lys要的参数
			financeBankPO.setAmount(fbp.getAmount());         //金额   lys要的参数
			financeBankPO.setPevvaccname(fbp.getPevvaccname());//账号名称   lys要的参数
			financeBankPO.setPecvopenaccdept(fbp.getPecvopenaccdept());//开户机构    lys要的参数
			System.out.println("*大银行名称"+fbp.getAccnoname()+"*卡号"+fbp.getAccno()+"*账号名称"+fbp.getPevvaccname()+"*金额"+fbp.getAmount()+"*备注"+fbp.getUseof()+"*开户机构"+fbp.getPecvopenaccdept());
			String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("out_golden"), JSON.toJSONString(financeBankPO));
			System.out.println(result+"返回结果kkkkk");
			@SuppressWarnings("rawtypes")
			Result res=JSON.parseObject(result,Result.class);
			if(!res.udun) {//如果这里请求lys接口 他那边请求不到建行（异常情况下）返给我false 代表就是有异常情况 我这里就不需要往下面执行了,然后网络要是好了  就不走这个if里面了，就正常走流程
			     throw new RuntimeException("请求建行数据网络异常");
		    }
            Back be=res.getData();
            financeBankPO.setUid(Integer.parseInt(userId));   //set 点击复审同意得操作人id
            System.out.println(be.getSerialnumber()+"请求id");
            financeBankPO.setSerialnumber(be.getSerialnumber());//返回的serialnumber SET数据库
            financeBankPO.setUserDetailDTO(userDetailDTO);
			financeManageService.approvebankMoneyFlowOutGoldenToReview(financeBankPO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	/**
	 * 【出金 】【财务主管】在待复审里点击拒绝操作【第二次复审】
	 *  状态要修改为(复审拒绝)
	 *  企业下对应的账户里的总资产 和 冻结金额  暂不修改 金额仍保持冻结中..... 拒绝的话只修改状态 
	 * @param financeBankPO
	 * @return
	  {
       "enterpriseId":"1007777",
	   "moneyFlowNo":"1001"
      }
	 * 
	 */
	@RequestMapping(value="/outGoldRecheck/rejectbankMoneyFlowOutGoldenToReview",method=RequestMethod.POST)
	private ResultResp rejectbankMoneyFlowOutGoldenToReview(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO=userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_THE_REVIEW_REFUSED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinanceBankPO fbp;
		try {
			ValidationUtil.checkAndAssignInt(financeBankPO.getEnterpriseId());//企业id
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo()); //流水编号
			fbp= financeManageService.querybankMoneyFlowOutGolden(financeBankPO);//待
			if(fbp == null){
				throw new RuntimeException("该信息不存在");
			}
			if(fbp.getState()!=5){//5代表是初审通过   如果不是5就是复审过了
				throw new RuntimeException("该入金流水单已经复审过了");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败2",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			
			fbp.setEnterpriseId(financeBankPO.getEnterpriseId());
			fbp.setUserDetailDTO(userDetailDTO);
			fbp.setSecondCheckBy(Integer.parseInt(userId));//待复审里 点击拒绝 set复审得人
			financeManageService.rejectbankMoneyFlowOutGoldenToReview(fbp);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	//---------------------------------------------入金待划拨的列表--------------------------------------------//
	
	/**
	 * (财务查看 入金管理【待划拨】的列表---初审)
	 * @param financeBO
	 * @return
	  {
	 	"startTime":"",
	 	"endTime":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/goldenRecord/queryAwaitTransfera",method=RequestMethod.POST)
	private ResultResp queryAwaitTransfera(@RequestBody final FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.TO_BE_ALLOCATED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getStartTime(),0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getEndTime(),Long.MAX_VALUE);
			financeBO.setStartTime(startTime);
			if(startTime > endTime) {
				financeBO.setEndTime(Long.MAX_VALUE);
			} 
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryAwaitTransfera(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * (财务查看 入金管理【已划拨】的列表)
	 * @param financeBO
	 * @return
	  {
	 	"startTime":"",
	 	"endTime":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/goldenRecord/queryHasBeenAllocated",method=RequestMethod.POST)
	private ResultResp queryHasBeenAllocated(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.HAS_BEEN_ALLOCATED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getStartTime(),0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getEndTime(),Long.MAX_VALUE);
			financeBO.setStartTime(startTime);
			if(startTime > endTime) {
				financeBO.setEndTime(Long.MAX_VALUE);
			} 
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryHasBeenAllocated(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**入金记录
	 * 点击 确认划拨操作  TODO
	 * 待--------待
	 * @param financeBankPO
	 * @return
	  {
	    "moneyFlowNo":"1001"
	    "enterpriseName":"复杂美"
       }
	 */
	@RequestMapping(value="/goldenRecord/awaitTransfera",method=RequestMethod.POST)
	private ResultResp awaitTransfera(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.AFFIRM_TRANSFER.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo());//校验前端传来的流水编号
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getEnterpriseName());//校验前端输入的划拨企业名称
			EnterpriseDTO enterpriseDTO = financeManageService.queryEnterpriseNameBymoneyFlowNo(financeBankPO);//根据传来的企业名称查询是否有这个企业名称
			if(enterpriseDTO == null) {
				throw new RuntimeException("无法查询到该企业信息");
			}
			FinanceBankPO fin = financeManageService.querybankMoneyFlow(financeBankPO);
			if(fin == null){//判断流水是否存在
				throw new RuntimeException("该资金流水信息不存在");
			}
			if(fin.getState()!=11){//11是待划拨
				throw new RuntimeException("该入金划拨单已经审核过了");
			}
			financeBankPO.setEnterpriseId(enterpriseDTO.getEnterpriseId());//设置企业id
			financeBankPO.setEnterpriseName(enterpriseDTO.getEnterpriseName());//设置企业名称
			financeBankPO.setTransferEnterprise(enterpriseDTO.getEnterpriseName());//设置划拨企业名称(等于企业名称)
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			financeManageService.awaitTransfera(financeBankPO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * 
	 * @param fin
	 * @return
	 * 退款操作  等待复审退款  只做了修改状态,改为（退款中...）
	 *  {
	    "moneyFlowNo":"1001"
        }
	 */
	@RequestMapping(value="/goldenRecord/hasBeenAllocated",method=RequestMethod.POST)
	private ResultResp hasBeenAllocated(@RequestBody FinanceBankPO financeBankPO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.REFUND.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo());//流水号不能为空
			FinanceBankPO fin = financeManageService.querybankMoneyFlow(financeBankPO);
			if(fin == null){//判断流水是否存在
				throw new RuntimeException("该资金流水信息不存在");
			}
			if(fin.getState()!=11){//11是待划拨
				throw new RuntimeException("该入金划拨单已经审核过了");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			financeManageService.hasBeenAllocated(financeBankPO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * (财务查看 入金退款记录【待审核退款】的列表)
	 * @param financeBO
	 * @return
	  {
	 	"startTime":"",
	 	"endTime":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/recordRefundAudit/queryToAuditARefund",method=RequestMethod.POST)
	private ResultResp queryToAuditARefund(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.REFUNDED_FOR_REVIEW.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getStartTime(),0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getEndTime(),Long.MAX_VALUE);
			financeBO.setStartTime(startTime);
			if(startTime > endTime) {
				financeBO.setEndTime(Long.MAX_VALUE);
			} 
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryToAuditARefund(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	/**
	 * (财务查看 入金退款记录【已审核退款】的列表)状态12和15
	 * @param financeBO
	 * @return
	  {
	 	"startTime":"",
	 	"endTime":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/recordRefundAudit/queryhasARefund",method=RequestMethod.POST)
	private ResultResp queryhasARefund(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {                                                                         
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.HAS_REFUNDED_FOR_REVIEW.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(financeBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(financeBO.getRowNum(), 1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getStartTime(),0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(financeBO.getEndTime(),Long.MAX_VALUE);
			financeBO.setStartTime(startTime);
			if(startTime > endTime) {
				financeBO.setEndTime(Long.MAX_VALUE);
			} 
			financeBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = financeManageService.queryhasARefund(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * 入金记录退款审核   点击(同意)操作 同意退款 【需要调接口】
	 {
	 	"moneyFlowNo":"1001"
	 }
	 */
	@RequestMapping(value="/recordRefundAudit/approveArefund",method=RequestMethod.POST)
	private ResultResp approveArefund(@RequestBody FinanceBankPO financeBankPO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.AGREED_REFUND.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankAndAssignString(financeBankPO.getMoneyFlowNo()); //流水编号
			FinanceBankPO fin =financeManageService.querybankMoneyFlow(financeBankPO);
			if(fin==null) {
				throw new RuntimeException("该流水编号不存在");
			}
			if(fin.getState()!=14){//14是退款中      不等于14 说明已经审核退款了
				throw new RuntimeException("该入金退款单不为退款中状态");
			}
			if(fin.getOperationType()!=1){
				throw new RuntimeException("该笔流水不是入金的");
			}
			financeBankPO.setMoneyFlowNo(fin.getMoneyFlowNo());
			financeBankPO.setAmount(fin.getAmount());
			financeBankPO.setTranflow(fin.getTranflow());   //银行的流水编号，lys要的
			financeBankPO.setAccno(fin.getAccno());         //银行卡号，lys要的
			financeBankPO.setAccnoname(fin.getAccnoname()); //大银行名称，lys要的
			String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("bank_flow_gloden"), JSON.toJSONString(financeBankPO));
            @SuppressWarnings("rawtypes")
			Result res=JSON.parseObject(result,Result.class);//获取lys的数据
            if(!res.udun) {//如果这里请求lys接口 他那边请求不到建行（异常情况下）返给我false 代表就是有异常情况 我这里就不需要往下面执行了,然后网络要是好了  就不走这个if里面了，就正常走流程
   		     throw new RuntimeException("请求建行数据网络异常");
   	        }
            Back be=res.getData();
            financeBankPO.setSerialnumber(be.getSerialnumber());//返回的serialnumber SET进数据库
			financeManageService.approveArefund(financeBankPO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(),null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	//----------------------------------------------所有的导出数据到Excel-----------------------------------------//
	/**
	 * 入金待审核 导出Excel
	 */
	@RequestMapping(value="/goldenAudit/selectGoldenExportExcel",method=RequestMethod.GET)
	private ResultResp selectGoldenExportExcel(HttpServletResponse response){
	String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.GOLDEN_TO_AUDIT_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectGoldenExportExcel();
		       List<FinanceBankExcelVO> listMap = new ArrayList<FinanceBankExcelVO>();
			   for (LinkedHashMap<String, Object> map : list) {
				   String time = map.get("create_time")==null? "":DateUtil.getBeijingTime((Long)map.get("create_time"), pattern);
				   //String bankCard=map.get("bank_card")!=null?String.valueOf(map.get("bank_card")):null;
				   String bankCard=String.valueOf(map.get("bank_card"));
				   String enterpriseName=String.valueOf(map.get("enterprise_name"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   FinanceBankExcelVO  backFinanceBankExcel = new FinanceBankExcelVO(moneyFlowNo, enterpriseName, amount, time, bankCard);
				   listMap.add(backFinanceBankExcel);//待
				}
			
		    String[]  headers = {"流水编号","企业名称", "入金金额","申请时间","银行卡号"};  //表格的标题栏
			Execl.get("入金待审核汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * 入金已审核 导出Excel
	 */
	@RequestMapping(value="/goldenAudit/selectGoldenExportExcelCheckOk",method=RequestMethod.GET)
	private ResultResp selectGoldenExportExcelCheckOk(HttpServletResponse response){
	    String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.GOLDEN_TO_GOLDEN_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectGoldenExportExcelCheckOk();
			List<FinanceBankExcelGoldenVO> listMap= new ArrayList<FinanceBankExcelGoldenVO>();
			 for (LinkedHashMap<String, Object> map : list) {
				   String startTime = map.get("create_time")==null? "":DateUtil.getBeijingTime((Long)map.get("create_time"), pattern);
				   String endTime = map.get("end_time")==null? "":DateUtil.getBeijingTime((Long)map.get("end_time"), pattern);
				   System.out.println(endTime+"shijain");
				   String bankCard=String.valueOf(map.get("bank_card"));
				   String enterpriseName=String.valueOf(map.get("enterprise_name"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   String stateName=String.valueOf(map.get("state_name"));
				   FinanceBankExcelGoldenVO  backFinanceBankExcel = new FinanceBankExcelGoldenVO(moneyFlowNo, enterpriseName, amount, startTime, endTime, bankCard, stateName);
				   listMap.add(backFinanceBankExcel);
				}
			 String[]  headers = { "流水编号","企业名称","入金金额","申请时间","完成时间","银行卡号","状态"};  //表格的标题栏
			 
			Execl.get("入金已审核汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	/**
	 * 出金待审核 导出EXcel
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/outGold/selectGoldenOutExportExcel2",method=RequestMethod.GET)
	private ResultResp selectGoldenOutExportExcel2(HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectGoldenOutExportExcel2();
			List<FinanceBankExceOutGlodenlVO> listMap=new ArrayList<FinanceBankExceOutGlodenlVO>();
			 for (LinkedHashMap<String, Object> map : list) {
				   String time = map.get("create_time")==null? "":DateUtil.getBeijingTime((Long)map.get("create_time"), pattern);
				   String enterpriseName=String.valueOf(map.get("enterprise_name"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   FinanceBankExceOutGlodenlVO  backFinanceBankExcel = new FinanceBankExceOutGlodenlVO(moneyFlowNo, enterpriseName, amount, time);
				   listMap.add(backFinanceBankExcel);//待
				}
			
		    String[]  headers = { "流水编号","企业名称", "申请时间","出金金额"};  //表格的标题栏
			Execl.get("财务出金待审核汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}

	/**
	 * 出金已审核 导出EXcel
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/outGold/selectGoldenOutExportExcelOkCheck2",method=RequestMethod.GET)
	private ResultResp selectGoldenOutExportExcelOkCheck2(HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_THE_APPROVE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectGoldenOutExportExcelOkCheck2();
			List<FinanceBankExceOutGlodenlOkCheckVO> listMap=new ArrayList<FinanceBankExceOutGlodenlOkCheckVO>();
			 for (LinkedHashMap<String, Object> map : list) {
				   String startTime = map.get("create_time")==null? "":DateUtil.getBeijingTime((Long)map.get("create_time"), pattern);
				   String firstTime = map.get("first_time")==null? "":DateUtil.getBeijingTime((Long)map.get("first_time"), pattern);
				   String enterpriseName=String.valueOf(map.get("enterprise_name"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   String stateName=String.valueOf(map.get("state_name"));
				   FinanceBankExceOutGlodenlOkCheckVO  backFinanceBankExcel = new FinanceBankExceOutGlodenlOkCheckVO(moneyFlowNo, enterpriseName, startTime, amount, firstTime, stateName);
				   listMap.add(backFinanceBankExcel);//待
				}
		    String[]  headers = { "流水编号","企业名称", "申请时间","出金金额","完成时间","状态"};  //表格的标题栏
			Execl.get("财务出金已审核汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * 出金待复审 导出EXcel
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/outGoldRecheck/selectOutGlodenToReview",method=RequestMethod.GET)
	private ResultResp selectOutGlodenToReview(HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];                         
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_THE_REVIEW_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectOutGlodenToReview();
			List<FinanceBankExcelToCheckVO> listMap=new ArrayList<FinanceBankExcelToCheckVO>();
			 for (LinkedHashMap<String, Object> map : list) {
				   String firstTime = map.get("first_time")==null? "":DateUtil.getBeijingTime((Long)map.get("first_time"), pattern);
				   String enterpriseName=String.valueOf(map.get("enterprise_name"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   String chuShenName=String.valueOf(map.get("person_name"));
				   FinanceBankExcelToCheckVO  backFinanceBankExcel = new FinanceBankExcelToCheckVO(moneyFlowNo, enterpriseName, firstTime, amount, chuShenName);
				   listMap.add(backFinanceBankExcel);
				}
			
		    String[]  headers = { "流水编号","企业名称","出金金额","初审完成时间","初审人员"};  //表格的标题栏
			Execl.get("财务出金待复审汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * 出金已复审 导出EXcel
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/outGoldRecheck/selectOutGlodenToReviewOkCheck",method=RequestMethod.GET)
	private ResultResp selectOutGlodenToReviewOkCheck(HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.THE_GOLD_TO_AUDIT_HAS_BEEN_REVIEWED_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectOutGlodenToReviewOkCheck();
			List<FinanceBankExcelToAlreadyCheckVO> listMap=new ArrayList<FinanceBankExcelToAlreadyCheckVO>();
			for (LinkedHashMap<String, Object> map : list) {
				   String firstTime = map.get("first_time")==null? "":DateUtil.getBeijingTime((Long)map.get("first_time"), pattern);
				   String secondTime = map.get("second_time")==null? "":DateUtil.getBeijingTime((Long)map.get("second_time"), pattern);
				   String enterpriseName=String.valueOf(map.get("enterprise_name"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   String chuShenName=String.valueOf(map.get("chuShenName"));
				   String fuShenName=String.valueOf(map.get("fuShenName"));
				   String stateName=String.valueOf(map.get("state_name"));
				   FinanceBankExcelToAlreadyCheckVO  backFinanceBankExcel = new FinanceBankExcelToAlreadyCheckVO(moneyFlowNo, enterpriseName, firstTime, secondTime, amount, chuShenName, fuShenName, stateName);
				   listMap.add(backFinanceBankExcel);//待
				}
		    String[]  headers = { "流水编号","企业名称","出金金额","初审完成时间","初审人员","复审人员","复审完成时间","状态"};  //表格的标题栏
			Execl.get("财务出金已复审汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	/**
	 * 入金待划拨 导出EXcel
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/goldenRecord/selectAwaitTransfera",method=RequestMethod.GET)
	private ResultResp selectAwaitTransfera(HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.TO_BE_ALLOCATED_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectAwaitTransfera();
			List<FinanceBankExcelGoldenRecordVO> listMap=new ArrayList<FinanceBankExcelGoldenRecordVO>();
			for (LinkedHashMap<String, Object> map : list) {
				   String startTime = map.get("create_time")==null? "":DateUtil.getBeijingTime((Long)map.get("create_time"), pattern);
				   String createBy=String.valueOf(map.get("create_by"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   String bankCard=String.valueOf(map.get("bank_card"));
				   String memorandum=String.valueOf(map.get("memorandum"));
				   FinanceBankExcelGoldenRecordVO  backFinanceBankExcel = new FinanceBankExcelGoldenRecordVO(moneyFlowNo, createBy, memorandum, amount, bankCard, startTime);
				   listMap.add(backFinanceBankExcel);//待
				}
			
		    String[]  headers = { "流水编号","名称","备注信息","入金金额","申请时间","银行卡号"};  //表格的标题栏
			Execl.get("财务入金待划拨汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	/**
	 * 入金已划拨 导出EXcel
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/goldenRecord/selectHasBeenAllocated",method=RequestMethod.GET)
	private ResultResp selectHasBeenAllocated(HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, AuthEnum.HAS_BEEN_ALLOCATED_EXPORT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		try {
			List<LinkedHashMap<String, Object>> list=financeManageService.selectHasBeenAllocated();
			List<FinanceBankExcelGoldenRecordOkVO> listMap=new ArrayList<FinanceBankExcelGoldenRecordOkVO>();
			for (LinkedHashMap<String, Object> map : list) {
				   String startTime = map.get("create_time")==null? "":DateUtil.getBeijingTime((Long)map.get("create_time"), pattern);
				   String endTime = map.get("end_time")==null? "":DateUtil.getBeijingTime((Long)map.get("end_time"), pattern);
				   String createBy=String.valueOf(map.get("create_by"));
				   String moneyFlowNo=String.valueOf(map.get("money_flow_no"));
				   Double amount=(Double)map.get("amount");
				   String bankCard=String.valueOf(map.get("bank_card"));
				   String transferEnterprise=String.valueOf(map.get("transfer_enterprise"));
				   String stateName=String.valueOf(map.get("state_name"));
				   FinanceBankExcelGoldenRecordOkVO  backFinanceBankExcel = new FinanceBankExcelGoldenRecordOkVO(moneyFlowNo, createBy, transferEnterprise, amount, startTime, bankCard, endTime, stateName);
				   listMap.add(backFinanceBankExcel);//待
				}
			
		    String[]  headers = { "流水编号","名称","划拨企业","入金金额","申请时间","完成时间","银行卡号","状态"};  //表格的标题栏
			Execl.get("财务入金已划拨汇总", headers, listMap, response);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * 查询企业表的所有的企业名称给前端    前端做入金划拨企业用  暂时 没用上这个方法
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/selectAllEnterpriseName",method=RequestMethod.POST)
	private ResultResp selectAllEnterpriseName(){
		try {
			Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_STATE.getAuthUrl());
			List<EnterprisePO> allEnterpriseName = financeManageService.selectAllEnterpriseName();
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), allEnterpriseName);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
}
