package com.fuzamei.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.fuzamei.pojo.dto.BackRoleDTO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.vo.BackUserVO;
import com.fuzamei.pojo.vo.EnterpriseVO;
import com.fuzamei.pojo.vo.WsjVo;
import com.fuzamei.util.PageDTO;

public interface WsjTestService {

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询用户管理界面的用户信息  
	 **/
	PageDTO findEnterprise(EnterpriseDTO enterpriseDTO);
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询用户权限
	 **/
	boolean validateAuth(int userId,int auth);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ： //入金/出金   待审核/已审核等  列表查询
	 **/
	PageDTO queryGold(EnterpriseDTO enterpriseDTO);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  同意入金  
	 * @return 
	 **/
	 void aggreeInGold(int userId, String moneyFlowNo);
	 
	 
	 /**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  改变bankMoneyFlow的状态
	 * 传入参数  ：交易编号    和待改变状态
	 * 注意  ：  线程安全
	 **/
	void changeBankMoneyFlowState(String moneyFlowNo,int state);

	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  入金拒绝  
	 **/
	void refuseInGold(int userId, String moneyFlowNo);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  初审通过
	 **/
	void agreeOutGoldFirst(int userId, String moneyFlowNo);

	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  复审通过
	 **/
	void agreeOutGoldSecond(int userId, String moneyFlowNo);
	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  初审拒绝
	 **/
	void refuseOutGoldFirst(int userId, String moneyFlowNo);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  复审拒绝
	 **/
	void refuseOutGoldSecond(int userId, String moneyFlowNo);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  导出excel 
	 * @param enterpriseDTO 
	 **/
	void creatExcel(PageDTO pageDTO, HttpServletResponse response, EnterpriseDTO enterpriseDTO);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：    查询风控初审等数据：  
	 **/
	PageDTO findWindFirstCheck(WsjVo wsjVo);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  上传尽调报告 
	 * @param request 
	 **/
	void uploadReport(int userId, MultipartFile[] files, Integer billId, HttpServletRequest request);

	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查找尽调报告文件路径：  
	 **/
	List<String> findReportFile(Integer billId);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  管理查看页面数据加载
	 **/
	PageDTO findManagerSee(EnterpriseDTO enterpriseDTO);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  更改授信额度
	 */
	void setLimit(String enterpriseId, Integer creditLine);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  判断企业是否存在
	 */
	void findEnterpriseExist(String enterpriseId);

	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询用户账户信息
	 **/
	PageDTO findBackUser(BackUserVO backUserVO);

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  分配角色
	 **/
	void distributionRole(Integer userId, List<Integer> roleIds);

	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询所有用户角色，并判断是否具有 该角色
	 **/
	List<BackRoleDTO> FindUserRole(Integer userId);
}
