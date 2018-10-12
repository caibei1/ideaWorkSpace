package com.fuzamei.web;


import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fuzamei.constant.Authes;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.constant.Statuses;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.bo.FinanceBO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.dto.FinanceDTO;
import com.fuzamei.pojo.po.AccountPO;
import com.fuzamei.pojo.po.FinancePO;
import com.fuzamei.pojo.vo.FinanceVO;
import com.fuzamei.service.FinanceManagementService;
import com.fuzamei.service.UserService;
import com.fuzamei.util.ExportExcel;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController
@RequestMapping(value="/financeManagement")
public class FinanceManagementAction {
	@Autowired
	private HttpServletRequest req;
	@Autowired
	private FinanceManagementService financeManagementService;
	@Autowired
	private UserService userService;
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceManagementAction.class);
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
	@RequestMapping(value="/queryGolden",method=RequestMethod.POST)
	private ResultResp queryGolden(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
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
			PageDTO pageDTO = financeManagementService.queryGolden(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * （财务查看入金管理【已审核】）
	 * {里面应该包括已同意的和已拒绝的}
	 * @param financeBO
	 * @return
	  {
	 	"enterpriseName":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/queryGoldenOKCheck",method=RequestMethod.POST)
	private ResultResp queryGoldenOKCheck(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
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
			PageDTO pageDTO = financeManagementService.queryGoldenOKCheck(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * (当入金得时候 后台财务审核  点击同意后要修改现金流表原先得状态改为[同意])
	 *  同时给账户表得【总资产】加上去
	 * @param  financeBO
	 * @return
		{
	      "enterpriseId":"1007777",
	 	  "accountId":"1007777",
		  "transactionId":"1001"
	    } 
	 */
	@RequestMapping(value="/approveCashFlow",method=RequestMethod.POST)
	private ResultResp approveCashFlow(@RequestBody FinancePO financePO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinancePO fin;
		try {
			ValidationUtil.checkAndAssignInt(financePO.getEnterpriseId());
			ValidationUtil.checkAndAssignInt(financePO.getAccountId());
			ValidationUtil.checkAndAssignInt(financePO.getTransactionId());
			if(financePO.getTransactionId()==null && financePO.getAccountId()==null){
				throw new RuntimeException("不能为空");
			}
			fin= financeManagementService.querycashFlow(financePO);
			if(fin.getStatus()!=Statuses.CHECK_NO){//为5是待审核
				throw new RuntimeException("该入金流水单已经审核过了");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败2",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			
			fin.setEnterpriseId(financePO.getEnterpriseId());
			fin.setAccountId(financePO.getAccountId());
			financeManagementService.approveCashFlow(fin);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 {
       "enterpriseId":"1007777",
	   "transactionId":"1001"
      }
	 * (当入金得时候 后台财务审核  点击拒绝后要修改现金流表原先得状态改为[拒绝])
	 * @param financePO
	 * @return
	 */
	@RequestMapping(value="/rejectCashFlow",method=RequestMethod.POST)
	private ResultResp rejectCashFlow(@RequestBody FinancePO financePO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinancePO fin;
		try {
			if(financePO.getTransactionId()==null){
				throw new RuntimeException("不能为空");
			}
			fin= financeManagementService.querycashFlow(financePO);
			if(fin.getStatus()!=5){
				throw new RuntimeException("该入金流水已经审核过了");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			fin.setEnterpriseId(financePO.getEnterpriseId());
			fin.setTransactionId(financePO.getTransactionId());
			financeManagementService.rejectCashFlow(fin);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	/**
	 * --暂注释--
	 * (当入金得时候 后台财务审核  点击同意后要修改现金流表原先得状态改为[同意])
	 * 当入金审核得时候，财务点击同意修改现金流表里得状态改为（同意） 同时给账户表得【总资产】加上去
	 * @param 
	 * @return
	 * 待----
	 */
	/*@RequestMapping(value="/updateGoldenCashFlowStatus",method=RequestMethod.POST)
	private ResultResp updateGoldenCashFlowStatus(@RequestBody FinancePO financePO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
			ValidationUtil.checkAndAssignInt(financePO.getStatus());
			ValidationUtil.checkAndAssignInt(financePO.getTransactionId());//交易流水编号
			ValidationUtil.checkAndAssignInt(financePO.getAccountId());//账号id
			financeManagementService.updateGoldenCashFlowStatus(financePO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}*/
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 
	 * 财务管查看（出金）(待审核)的
	 */
	@RequestMapping(value="/queryOutGold",method=RequestMethod.POST)
	private ResultResp queryOutGold(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
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
			PageDTO pageDTO = financeManagementService.queryOutGold(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}	
	}
	
	/**
	 * 财务管理查看出金(已审核)
	 * @param financeBO
	 * @return
	 */
	@RequestMapping(value="/queryOutGoldOkCheck",method=RequestMethod.POST)
	private ResultResp queryOutGoldOkCheck(@RequestBody FinanceBO financeBO) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
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
			PageDTO pageDTO = financeManagementService.queryOutGoldOkCheck(financeBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * (当出金得时候 后台财务审核  点击同意后要修改现金流表原先得状态改为[同意])
	 *  这时不能给账户表得【总资产】和可用金额减去
	 * @param  financeBO
	 * @return
		{
	      "enterpriseId":"1007777",
		  "transactionId":"1010"
	    } 
	 */
	@RequestMapping(value="/approveCashFlowOutGold",method=RequestMethod.POST)
	private ResultResp approveCashFlowOutGold(@RequestBody FinancePO financePO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		FinancePO fin;
		try {
			ValidationUtil.checkAndAssignInt(financePO.getEnterpriseId());
			//ValidationUtil.checkAndAssignInt(financePO.getAccountId());
			ValidationUtil.checkAndAssignInt(financePO.getTransactionId());
			if(financePO.getTransactionId()==null){
				throw new RuntimeException("不能为空");
			}
			fin= financeManagementService.querycashFlowOutGold(financePO);
			if(fin.getStatus()!=5){//5代表是待审核
				throw new RuntimeException("该出金流水单已经审核过了");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败3",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			
			fin.setEnterpriseId(financePO.getEnterpriseId());
			//fin.setAccountId(financePO.getAccountId());
			financeManagementService.approveCashFlow(fin);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	/**
	 * 出金  待审核时  财务点击同意或拒绝 
	 */
	@RequestMapping(value="/updateOutGoldOkCheck",method=RequestMethod.POST)
	private ResultResp updateOutGoldOkCheck(@RequestBody FinanceBO financeBO) {
		
		
		
		
		
		return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 导出数据到Excel表格里
	 * (待)
	 * @param financeDTO
	 * @return
	 */
	@RequestMapping(value="/exportExcel",method=RequestMethod.POST)
	private void Export(HttpServletResponse response,Integer group,String department,String type) throws UnsupportedEncodingException{
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.FINANCIAL_MANAGE);
		} catch (Exception e) {
			LOGGER.error("财务:{}-->权限验证失败",userId);
		}
		String filename= new String("财务管理审核汇总表.xls".getBytes(),"iso-8859-1");//中文文件名必须使用此句话
	
        response.setContentType("application/octet-stream");
        response.setContentType("application/OCTET-STREAM;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename="+filename );
        String[] headers = { "流水编号","企业名称", "入金金额", "申请时间", "操作"};  //表格的标题栏
        HashMap<String,Object> map = new HashMap<>();  //组合查询条件，返回数据
        map.put("group", group);
        map.put("department", department);
        map.put("type", type);
        
        List<FinanceVO> list = financeManagementService.selectFinanceVOBean(map); // TODO  待。。。？？？？？？
        //WorkloadExport 为将要导出的类，也就是表格的一行记录，里面的所有字段都不能为空，必须生成set get方法
        //导出列顺序和类中成员顺序一致
        try {
        	ExportExcel<FinanceVO> ex = new ExportExcel<FinanceVO>();  //构造导出类

            OutputStream  out = new BufferedOutputStream(response.getOutputStream());

            String  title = list.get(0).getEnterpriseName();  //title需要自己指定 比如写Sheet  --TODO 待。。。？？？？
            ex.exportExcel(title, headers, list, out);  //title是excel表中底部显示的表格名，如Sheet  ---TODO  待。。。？？？？
            out.close();
            //JOptionPane.showMessageDialog(null, "导出成功!");
            //System.out.println("excel导出成功！");
        } catch (FileNotFoundException  e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
}
