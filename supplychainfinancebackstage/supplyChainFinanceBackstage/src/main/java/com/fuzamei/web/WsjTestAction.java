package com.fuzamei.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.core.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.HintMSG;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.constant.States;
import com.fuzamei.http.Request;
import com.fuzamei.pojo.dto.BackRoleDTO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.BackUserPO;
import com.fuzamei.pojo.po.BankMoneyFlowPO;
import com.fuzamei.pojo.vo.BackUserVO;
import com.fuzamei.pojo.vo.WsjVo;
import com.fuzamei.service.impl.WsjTestService;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;


@RestController
@RequestMapping(value="wsj")
public class WsjTestAction {
	
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private WsjTestService wsjTestService;
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UserManagementAction.class);
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询用户管理页面显示的企业信息
	 * 2018年4月23日 
	 * 下午1:44:54
	 * 传入参数  ：  startTime  endTime  enterpriseName  rowNum  page//当前页
	 * 
	 * 返回结果  ：  
	 * 注意  ：  只显示通过的企业
	 **/
	
	@RequestMapping(value="findEnterprise", method=RequestMethod.POST)
	private ResultResp findEnterprise(@RequestBody EnterpriseDTO enterpriseDTO){
		
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		//权限验证
		try{
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_QUERY_USERMANAGE.getAuthId());
		}catch (RuntimeException e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//参数校验
		try {
			ValidationUtil.checkNullAndAssignString(enterpriseDTO.getEnterpriseName());
			long startTime = ValidationUtil.checkAndAssignDefaultLong(enterpriseDTO.getStartTime(),0);
			long endTime = ValidationUtil.checkAndAssignDefaultLong(enterpriseDTO.getEndTime(), Long.MAX_VALUE);
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getPage(), 1);
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getRowNum(), 1);
			enterpriseDTO.setStartTime(startTime);
			enterpriseDTO.setEndTime(endTime);
			if(startTime>endTime){
				enterpriseDTO.setEndTime(Long.MAX_VALUE);
			}
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), true, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//进行查询
		PageDTO pageDTO = null;
		try {
			pageDTO = wsjTestService.findEnterprise(enterpriseDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
		
		return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
	}
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查看入金待审核列表
	 * 2018年4月23日 
	 * 下午7:42:59
	 * 传入参数  ：  {
	 * 		"page":"",
	 * 		"rowNum":"",
	 * 		"states":"",  // 入金待审核  1/13   //入金已审核   2/3    // 出金待审核  1  //出金初审通过或者待复审  2  //出金复审通过   7   //  出账成功   9  //出账失败  10
	 * 		"operation":"",   //1代表入金    2代表出金
	 * 		"auth" : ""  //权限    //17,"风控查看待审核信息"   //18,"风控查看已待审核信息"    //45,"财务查看出金已审核列表
	 *	 }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value="inGold/queryInGold",method = RequestMethod.POST)
	public ResultResp queryInGold(@RequestBody EnterpriseDTO enterpriseDTO){
		//权限操作
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, enterpriseDTO.getAuth());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try {
			ValidationUtil.checkNullAndAssignString(enterpriseDTO.getEnterpriseName());
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getPage(), 1);
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getRowNum(), 1);
			for(int state : enterpriseDTO.getStates()){
				ValidationUtil.checkMinAndAssignInt(state, 1);
				ValidationUtil.checkMaxAndAssignInt(state, 20);
			}
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getOperationType(), 1);
			ValidationUtil.checkMaxAndAssignInt(enterpriseDTO.getOperationType(), 2);
			
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//查询列表
		try {
//			List<Integer> states = new ArrayList<>();
//			states.add(States.CHECK_NO);//1
//			states.add(States.TRANSFERA_AMOUNT);//13
			PageDTO pageDTO = wsjTestService.queryGold(enterpriseDTO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  入金同意
	 * 2018年4月24日 
	 * 下午12:30:23
	 * 传入参数  ：  交易编号
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	
	@RequestMapping(value="inGold/inGold",method = RequestMethod.POST)
	public ResultResp inGold(@RequestBody BankMoneyFlowPO bankMoneyFlowPO){
		String moneyFlowNo = bankMoneyFlowPO.getMoneyFlowNo();
		//权限查询
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_GOLDEN_TO_AUDIT_AGREED.getAuthId());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//校验参数
		try {
			ValidationUtil.checkNullAndAssignString(moneyFlowNo);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//入金同意
		try {
			wsjTestService.aggreeInGold(userId,moneyFlowNo);
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			else{
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
			}
		}
		return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
	}
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  入金拒绝
	 * 2018年4月26日 
	 * 下午2:35:22
	 * 传入参数  ：  moneyFlowNo
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "inGold/refuseInGold",  method = RequestMethod.POST)
	public ResultResp refuseInGold(@RequestBody BankMoneyFlowPO bankMoneyFlowPO){
		//权限验证
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_GOLDEN_TO_AUDIT_REFUSED.getAuthId());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//校验参数
		try {
			ValidationUtil.checkNullAndAssignString(bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//拒绝入金
		try {
			wsjTestService.refuseInGold(userId,bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			else{
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
			}
		}
		return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  初审同意出金
	 * 2018年4月26日 
	 * 下午3:10:02
	 * 传入参数  ：  moneyFlowNo
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "outGole/agreeOutGoldFirst",method = RequestMethod.POST)
	public ResultResp agreeOutGoldFirst(@RequestBody BankMoneyFlowPO bankMoneyFlowPO){
		//验证权限
		//权限验证
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_THE_GOLD_TO_AUDIT_AGREED.getAuthId());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//校验参数
		try {
			ValidationUtil.checkNullAndAssignString(bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//同意出金  初审通过为5
		try {
			wsjTestService.agreeOutGoldFirst(userId,bankMoneyFlowPO.getMoneyFlowNo());
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			else{
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
			}
		}
		
	}
	
	
	
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  复审同意出金
	 * 2018年4月26日 
	 * 下午3:10:02
	 * 传入参数  ：  moneyFlowNo
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "outGole/agreeOutGoldSecond",method = RequestMethod.POST)
	public ResultResp agreeOutGoldSecond(@RequestBody BankMoneyFlowPO bankMoneyFlowPO){
		//验证权限
		//权限验证
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_THE_GOLD_TO_AUDIT_THE_REVIEW_AGREED.getAuthId());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//校验参数
		try {
			ValidationUtil.checkNullAndAssignString(bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//同意出金  复审通过为7
		try {
			wsjTestService.agreeOutGoldSecond(userId,bankMoneyFlowPO.getMoneyFlowNo());
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			else{
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
			}
		}
		
	}
	
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  初审拒绝出金
	 * 2018年4月26日 
	 * 下午2:35:22
	 * 传入参数  ：  moneyFlowNo
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "outGold/refuseOutGoldFirst",  method = RequestMethod.POST)
	public ResultResp refuseOutGoldFirst(@RequestBody BankMoneyFlowPO bankMoneyFlowPO){
		//权限验证
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_THE_GOLD_TO_AUDIT_REFUSED.getAuthId());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//校验参数
		try {
			ValidationUtil.checkNullAndAssignString(bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//初审拒绝出金
		try {
			wsjTestService.refuseOutGoldFirst(userId,bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			else{
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
			}
		}
		return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  初审拒绝出金
	 * 2018年4月26日 
	 * 下午2:35:22
	 * 传入参数  ：  moneyFlowNo
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "outGold/refuseOutGoldSecond",  method = RequestMethod.POST)
	public ResultResp refuseOutGoldSecond(@RequestBody BankMoneyFlowPO bankMoneyFlowPO){
		//权限验证
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.WSJ_THE_GOLD_TO_AUDIT_THE_REVIEW_REFUSED.getAuthId());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//校验参数
		try {
			ValidationUtil.checkNullAndAssignString(bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//初审拒绝出金
		try {
			wsjTestService.refuseOutGoldSecond(userId,bankMoneyFlowPO.getMoneyFlowNo());
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			else{
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
			}
		}
		return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
	}
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  
	 * 2018年4月28日 
	 * 下午2:30:15
	 * 传入参数  ：  {
	 * 		"page":"",
	 * 		"rowNum":"",
	 * 		"states":"",  // 入金待审核  1/13   //入金已审核   2/3    // 出金待审核  1  //出金初审通过或者待复审  2  //出金复审通过   7   //  出账成功   9  //出账失败  10
	 * 		"operation":"",   //1代表入金    2代表出金
	 * 		"auth" : "",  //权限    //17,"风控查看待审核信息"   //18,"风控查看已待审核信息"    //45,"财务查看出金已审核列表
	 *	 }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "exportExcel",method = RequestMethod.POST)
	public ResultResp exportExcel(@RequestBody EnterpriseDTO enterpriseDTO){
		//权限操作
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, enterpriseDTO.getAuth());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try {
			ValidationUtil.checkNullAndAssignString(enterpriseDTO.getEnterpriseName());
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getPage(), 1);
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getRowNum(), 1);
			for(int state : enterpriseDTO.getStates()){
				ValidationUtil.checkMinAndAssignInt(state, 1);
				ValidationUtil.checkMaxAndAssignInt(state, 20);
			}
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getOperationType(), 1);
			ValidationUtil.checkMaxAndAssignInt(enterpriseDTO.getOperationType(), 2);
			
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//查询列表
		try {
			PageDTO pageDTO = wsjTestService.queryGold(enterpriseDTO);
			//封装excel
			wsjTestService.creatExcel(pageDTO,response,enterpriseDTO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.QUERY_FAIL.getHintMsg(), null);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
	} 
	
	///****************************************风控初审***********************************************************//
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询风控初审下面的待审核列表
	 * 2018年5月2日 
	 * 下午3:29:41
	* 传入参数  ：  {
	* 		"enterpriseName":"",
	 * 		"page":"",
	 * 		"rowNum":"",
	 * 		"status":"",  //  0 待审核  1 已通过  2 已拒绝  3已作废    13已完成
	 * 		"auth" : ""  //权限    
	 *	 }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value="/wind/firstCheck",method = RequestMethod.POST)
	public ResultResp findWindFirstCheck(@RequestBody WsjVo wsjVo){
		//权限查询
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, wsjVo.getAuth());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//入参校验
		try {
			ValidationUtil.checkMinAndAssignInt(wsjVo.getPage(), 0);
			ValidationUtil.checkMinAndAssignInt(wsjVo.getRowNum(), 0);
			ValidationUtil.checkAndAssignInt(wsjVo.getStatus());
			ValidationUtil.checkNullAndAssignString(wsjVo.getEnterpriseName());
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//进行查询
		try {
			PageDTO pageDto =  wsjTestService.findWindFirstCheck(wsjVo);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  上传尽调报告
	 * 2018年5月7日 
	 * 上午10:22:22
	 * 传入参数  ：  {
	 * 		billId : ""
	 * }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "uploadReport",method = RequestMethod.POST)
	public ResultResp uploadReport(@RequestParam(value="files") MultipartFile[] files,@RequestParam(value = "billId") Integer billId){
		//验证权限
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]) ;
		try {
			wsjTestService.validateAuth(userId, AuthEnum.UPLOAD_RESPOSIBLE_REPORTS.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkAndAssignInt(billId);
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//上传文件
		try {
			wsjTestService.uploadReport(userId,files,billId,request);
		} catch (Exception e) {
			if(e instanceof RuntimeException)
				return ResultResp.getResult(HintEnum.UPLOAD_FAIL.getCode(), false, e.getMessage(), null);
			else
				return ResultResp.getResult(HintEnum.UPLOAD_FAIL.getCode(), false, HintEnum.UPLOAD_FAIL.getHintMsg(), null);
		}
		return ResultResp.getResult(HintEnum.UPLOAD_SUCCESS.getCode(), true, HintEnum.UPLOAD_SUCCESS.getHintMsg(), null);
	}
	
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  尽调报告下载
	 * 2018年5月7日 
	 * 下午2:12:48
	 * 传入参数  ：  billId
	 * 返回结果  ：  
	 * 注意  ：  无法多文件下载   应传入附件id
	 **/
	@RequestMapping(value = "downloadReport", method = RequestMethod.POST)
	public ResultResp downloadReport(@RequestParam(value = "billId") Integer billId){
		//验证权限
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.DOWNLOAD_RESPOSIBLE_REPORTS.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkAndAssignInt(billId);
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//下载文件
		try {
			//查询文件
			List<String> paths = wsjTestService.findReportFile(billId);
			for(String path : paths){
				response.setContentType("application/force-download");//下载而不是解析
				response.addHeader("Content-Disposition",  "attachment;fileName=" + path.substring(path.lastIndexOf("/")).substring(1));//设置文件名
				File file = new File(path);
				InputStream in = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(in);
				ServletOutputStream outputStream = response.getOutputStream();
				byte[] b = new byte[1024];
				int len = 0;
				while((len = bis.read(b))!=-1){
					outputStream.write(b,0,len);
				}
				in.close();
				bis.close();
				outputStream.close();
			}
			return ResultResp.getResult(HintEnum.DOWNLOAD_SUCCESS.getCode(), true, HintEnum.DOWNLOAD_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResultResp.getResult(HintEnum.DOWNLOAD_FAIL.getCode(), false, HintEnum.DOWNLOAD_FAIL.getHintMsg(), null);
		}
	}
	
	//**********************************************管理查看***********************************************************//
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  管理查看页面的数据显示
	 * 2018年5月8日 
	 * 下午12:03:30
	 * 传入参数  ：  {
	 * 		rowNum
	 * 		page
	 * 		enterpriseName
	 * }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "managerSee",method = RequestMethod.POST)
	public ResultResp managerSee(@RequestBody EnterpriseDTO enterpriseDTO ){
		//验证权限
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			//没找到对应权限
			//wsjTestService.validateAuth(userId, AuthEnum.DOWNLOAD_RESPOSIBLE_REPORTS.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkNullAndAssignString(enterpriseDTO.getEnterpriseName());
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getPage(), 1);
			ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getRowNum(), 1);
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//查询
		try{
			PageDTO pageDTO = wsjTestService.findManagerSee(enterpriseDTO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
		
	}
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  设置授信额度
	 * 2018年5月8日 
	 * 下午3:40:45
	 * 传入参数  ：  enterpriseId   creditLine
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "setLimit",method = RequestMethod.POST)
	public ResultResp setLimit(@RequestParam(value = "enterpriseId") String enterpriseId,@RequestParam(value = "creditLine") Integer creditLine){
		//验证权限
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.SET_CREDITLINE.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkNullAndAssignString(enterpriseId);
			ValidationUtil.checkMinAndAssignInt(creditLine, 1);
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		try {
			//授信额度不能大于宣传额度
			ValidationUtil.checkMaxAndAssignInt(creditLine, 1000000);
		} catch (Exception e) {
			return ResultResp.getResult(300, false, "授信额度不能大于宣传额度", null);
		}
		try {
			//查询企业是否存在
			wsjTestService.findEnterpriseExist(enterpriseId);
			wsjTestService.setLimit(enterpriseId,creditLine);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
		}
	}
	
	/********************************************账号管理******************************************************/
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询用户账户信息
	 * 2018年5月8日 
	 * 下午5:19:56
	 * 传入参数  ：  
	 * {
	 * "username":"",
	 * "rowNum":100,
	 * "page":1
	 * }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "findBackUser",method = RequestMethod.POST)
	public ResultResp findBackUser(@RequestBody BackUserVO backUserVO){
		//验证权限
		int userId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(userId, AuthEnum.QUERY_ACCOUNTS.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkNullAndAssignString(backUserVO.getUsername());
			ValidationUtil.checkMinAndAssignInt(backUserVO.getPage(), 1);
			ValidationUtil.checkMinAndAssignInt(backUserVO.getRowNum(), 1);
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//查询
		try {
			PageDTO pageDTO = wsjTestService.findBackUser(backUserVO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查找用户角色
	 * 2018年5月8日 
	 * 下午6:12:23
	 * 传入参数  ：  userId
	 * 返回结果  ：  返回role集合
	 * 注意  ：
	 **/
	
	@RequestMapping(value = "FindUserRole",method = RequestMethod.POST)
	public ResultResp findUserRole(@RequestParam(value = "userId") Integer userId){
		
		//验证权限
		int uId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(uId, AuthEnum.SHOW_ROLES.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkAndAssignInt(userId);
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		
		//查询
		try {
			List<BackRoleDTO> backRoleDTOs = wsjTestService.FindUserRole(userId);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), backRoleDTOs);
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, e.getMessage(), null);
			}
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg(), null);
		}
	}
	
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  分配角色
	 * 2018年5月8日 
	 * 下午5:28:53
	 * 传入参数  ：  userId   roleId 格式1,2,3
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "distributionRole",method = RequestMethod.POST)
	public ResultResp distributionRole(@RequestParam(value = "userId") Integer userId, @RequestParam(value = "roleId") List<Integer> roleIds){
		//验证权限
		int uId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(uId, AuthEnum.QUERY_ACCOUNTS.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			ValidationUtil.checkAndAssignInt(userId);
			for(int roleId : roleIds){
				ValidationUtil.checkAndAssignInt(roleId);
			}
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		//分配权限
		try {
			wsjTestService.distributionRole(userId,roleIds);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			if(e instanceof RuntimeException){
				return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, e.getMessage(), null);
			}
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg(), null);
		}
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  删除用户
	 * 2018年5月9日 
	 * 上午10:23:53
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  把isdelete改为0
	 **/
	@RequestMapping(value = "deleteBackUser",method=RequestMethod.POST)
	public ResultResp deleteBackUser(@RequestParam(value = "userId") Integer userId){
		return null;
	}
	

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  编辑用户  
	 * 2018年5月9日 
	 * 上午10:23:53
	 * 传入参数  ：  userId和改变后的用户名密码
	 * 返回结果  ：  
	 * 注意  ：  更新用户名和密码
	 **/
	@RequestMapping(value = "updateBackUser",method=RequestMethod.POST)
	public ResultResp updateBackUser(@RequestParam(value = "userId") Integer userId,@RequestParam(value = "username") String username,@RequestParam(value = "password") String password){
		return null;
	}
	
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：创建  账号
	 * 2018年5月9日 
	 * 上午10:23:53
	 * 传入参数  ：  {
	 * 		"username" : "",
	 * 		"password" : "",
	 * 		"idCardNum" : "",
	 * 		"personName" : ""
	 * }
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@RequestMapping(value = "createBackUser",method=RequestMethod.POST)
	public ResultResp updateBackUser(@RequestBody BackUserPO backUserPO ){
		//验证权限
		int uId = ValidationUtil.checkAndAssignInt(request.getHeader("Authorization").split("&")[1]);
		try {
			wsjTestService.validateAuth(uId, AuthEnum.CREATE_ACCOUNT.getAuthId());
		} catch (Exception e) {
			ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg(), null);
		}
		//验证入参
		try{
			//校验用户名
			ValidationUtil.checkEmptyAndAssignString(backUserPO.getUsername(), RegexConstant.USERNAME_REGEX);
			//校验密码
			ValidationUtil.checkEmptyAndAssignString(backUserPO.getPassword(), RegexConstant.PWD_REGEX);
			//检验身份证
			ValidationUtil.checkEmptyAndAssignString(backUserPO.getIdCardNum(), RegexConstant.ID_CARD);
			//验证姓名
			ValidationUtil.checkBlankAndAssignString(backUserPO.getPersonName());
		} catch(Exception e){
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg(), null);
		}
		
		return null;
	}
	
	
	
	//*******************************************角色管理************************************************************//
	//public findBackRole
	
	
 } 