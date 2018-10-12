package com.fuzamei.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fuzamei.constant.HintMSG;
import com.fuzamei.constant.Path;
import com.fuzamei.constant.States;
import com.fuzamei.mapper.WsjTestMapper;
import com.fuzamei.pojo.bo.BackAttachmentBO;
import com.fuzamei.pojo.dto.BackRoleDTO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.BackAttachmentBillPO;
import com.fuzamei.pojo.po.BackUserPO;
import com.fuzamei.pojo.po.BankMoneyFlowPO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.po.WsjCashFlowPO;
import com.fuzamei.pojo.vo.BackUserVO;
import com.fuzamei.pojo.vo.EnterpriseVO;
import com.fuzamei.pojo.vo.WsjVo;
import com.fuzamei.util.PageDTO;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

@Service
public class WsjTestServiceImpl implements WsjTestService {

	
	private static final String RESPONSEBLE_PATH = Path.RESPONSEBLE_PATH;//尽职调查报告的文件夹路径
	
	@Autowired
	private WsjTestMapper wsjTestMapper;

	@Override
	// 验证权限
	public boolean validateAuth(int userId, int auth) {
		int count = wsjTestMapper.validateAuth(userId, auth);
		if (count == 0) {
			throw new RuntimeException(HintMSG.NO_AUTH);
		}
		return true;
	}

	@Override
	public PageDTO findEnterprise(EnterpriseDTO enterpriseDTO) {
		enterpriseDTO.setStartPage(enterpriseDTO.getPage() * enterpriseDTO.getRowNum() - enterpriseDTO.getRowNum());
		// 查询
		List<EnterpriseDTO> enterpriseDTOs = wsjTestMapper.findEnterprise(enterpriseDTO);
		PageDTO pageDTOs = PageDTO.getPagination(enterpriseDTOs.size(), enterpriseDTOs);
		return pageDTOs;
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： //入金/出金 待审核/已审核等 列表查询 2018年4月24日 上午10:44:53 传入参数 ： 返回结果
	 * ： 注意 ：
	 **/
	@Override
	public PageDTO queryGold(EnterpriseDTO enterpriseDTO) {
		enterpriseDTO.setStartPage(enterpriseDTO.getPage() * enterpriseDTO.getRowNum() - enterpriseDTO.getRowNum());
		List<BankMoneyFlowPO> bankMoneyFlowPOs = wsjTestMapper.queryGold(enterpriseDTO, enterpriseDTO.getStates(),
				enterpriseDTO.getOperationType());
		int num = wsjTestMapper.queryGoldNum(enterpriseDTO, enterpriseDTO.getStates(),
				enterpriseDTO.getOperationType());
		PageDTO p = PageDTO.getPagination(num, bankMoneyFlowPOs);
		return p;
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 同意入金 2018年4月24日 下午2:12:02 传入参数 ： 返回结果 ： 先判断是否已经同意 注意并发
	 * 注意 ：
	 **/
	@Override
	@Transactional
	public synchronized void aggreeInGold(int userId, String moneyFlowNo) {
		// 先查询状态
		int state = wsjTestMapper.findBankMoneyFlowStateBymoneyFlowNo(moneyFlowNo);
		if (state != States.CHECK_NO && state != States.TRANSFERA_AMOUNT) {
			throw new RuntimeException("该条记录已被其他管理员处理了");
		}
		// 改变状态
		changeBankMoneyFlowState(moneyFlowNo, States.GOLDEN_PASS);
		// 记录初审人员数据
		wsjTestMapper.updateBankMoneyEndTimeBymoneyFlowNo(moneyFlowNo, System.currentTimeMillis());

		// 查询整条记录
		// 获得账户id
		// 改变前台用户的金额数据
		BankMoneyFlowPO bankMoneyFlowPO = wsjTestMapper.findBankMoneyFlowBymoneyFlowNo(moneyFlowNo);
		double amount = bankMoneyFlowPO.getAmount();
		int accountId = bankMoneyFlowPO.getAccountId();
		wsjTestMapper.updateAccountUsableAndTotalByAccountId(amount, accountId, System.currentTimeMillis());

		// cashflow插入数据
		WsjCashFlowPO cashFlowPO = new WsjCashFlowPO();
		cashFlowPO.setTransactionFlowId(Long.parseLong(bankMoneyFlowPO.getMoneyFlowNo()));
		cashFlowPO.setEnterpriseId(bankMoneyFlowPO.getEnterpriseId());
		cashFlowPO.setAccountId(bankMoneyFlowPO.getAccountId());
		cashFlowPO.setEnterpriseName(bankMoneyFlowPO.getEnterpriseName());
		cashFlowPO.setAmount(bankMoneyFlowPO.getAmount());
		cashFlowPO.setStateId(1);
		cashFlowPO.setStatus(States.CASHFLOW_SUCCED);
		cashFlowPO.setCreateTime(bankMoneyFlowPO.getCreateTime());
		cashFlowPO.setEndTime(bankMoneyFlowPO.getEndTime());
		cashFlowPO.setBankCardId(bankMoneyFlowPO.getBankCard());
		wsjTestMapper.addCashFlow(cashFlowPO);
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 改变bankMoneyFlow的状态 2018年4月24日 下午2:12:02 传入参数 ： 交易编号
	 * 和待改变状态 注意 ：
	 **/
	@Override
	public void changeBankMoneyFlowState(String moneyFlowNo, int state) {
		wsjTestMapper.changeBankMoneyFlowState(moneyFlowNo, state);
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 入金拒绝 2018年4月26日 下午2:46:32 传入参数 ： 返回结果 ： 注意 ：
	 **/
	@Override
	@Transactional
	public void refuseInGold(int userId, String moneyFlowNo) {
		// 先查询状态
		int state = wsjTestMapper.findBankMoneyFlowStateBymoneyFlowNo(moneyFlowNo);
		if (state != States.CHECK_NO && state != States.TRANSFERA_AMOUNT) {
			throw new RuntimeException("该条记录已被其他管理员处理了");
		}
		// 改变状态
		changeBankMoneyFlowState(moneyFlowNo, States.GOLDEN_REJECT);
		// 记录审人员数据
		wsjTestMapper.updateBankMoneyFlowFirstCheckByAndFirstTimeBymoneyFlowNo(userId, moneyFlowNo,
				System.currentTimeMillis());

		BankMoneyFlowPO bankMoneyFlowPO = wsjTestMapper.findBankMoneyFlowBymoneyFlowNo(moneyFlowNo);
		// cashflow插入数据
		WsjCashFlowPO cashFlowPO = new WsjCashFlowPO();
		cashFlowPO.setTransactionFlowId(Long.parseLong(bankMoneyFlowPO.getMoneyFlowNo()));
		cashFlowPO.setEnterpriseId(bankMoneyFlowPO.getEnterpriseId());
		cashFlowPO.setAccountId(bankMoneyFlowPO.getAccountId());
		cashFlowPO.setEnterpriseName(bankMoneyFlowPO.getEnterpriseName());
		cashFlowPO.setAmount(bankMoneyFlowPO.getAmount());
		cashFlowPO.setStateId(1);
		cashFlowPO.setStatus(States.CASHFLOW_DEFEATED);
		cashFlowPO.setCreateTime(bankMoneyFlowPO.getCreateTime());
		cashFlowPO.setEndTime(bankMoneyFlowPO.getEndTime());
		cashFlowPO.setBankCardId(bankMoneyFlowPO.getBankCard());
		wsjTestMapper.addCashFlow(cashFlowPO);
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 初审通过 2018年4月26日 下午3:21:03 传入参数 ： 返回结果 ： 注意 ： 状态改为5
	 * 插入初审人员和时间
	 **/
	@Override
	@Transactional
	public void agreeOutGoldFirst(int userId, String moneyFlowNo) {
		// 先查询状态
		int state = wsjTestMapper.findBankMoneyFlowStateBymoneyFlowNo(moneyFlowNo);
		if (state != States.CHECK_NO) {
			throw new RuntimeException("该条记录已被其他管理员处理了");
		}
		// 改变状态
		changeBankMoneyFlowState(moneyFlowNo, States.FIRST_PASS);
		// 记录初审人员数据
		wsjTestMapper.updateBankMoneyFlowFirstCheckByAndFirstTimeBymoneyFlowNo(userId, moneyFlowNo,
				System.currentTimeMillis());

	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 复审通过 2018年4月26日 下午3:21:03 传入参数 ： 返回结果 ： 注意 ： 状态改为7
	 * 插入复审人员和时间
	 **/
	@Override
	@Transactional
	public void agreeOutGoldSecond(int userId, String moneyFlowNo) {
		// 先查询状态
		int state = wsjTestMapper.findBankMoneyFlowStateBymoneyFlowNo(moneyFlowNo);
		if (state != States.FIRST_PASS) {
			throw new RuntimeException("该条记录已被其他管理员处理了");
		}
		// 改变状态
		changeBankMoneyFlowState(moneyFlowNo, States.SECOND_PASS);
		// 记录复审人员数据
		wsjTestMapper.updateBankMoneyFlowSecondCheckByAndSecondTimeBymoneyFlowNo(userId, moneyFlowNo,
				System.currentTimeMillis());

		// 改变账户冻结金额和总金额
		// 查询整条记录
		// 获得账户id
		// 改变前台用户的金额数据
		BankMoneyFlowPO bankMoneyFlowPO = wsjTestMapper.findBankMoneyFlowBymoneyFlowNo(moneyFlowNo);
		double amount = bankMoneyFlowPO.getAmount();
		int accountId = bankMoneyFlowPO.getAccountId();
		wsjTestMapper.updateAccountFreezeAndTotalByAccountId(amount, accountId, System.currentTimeMillis());

		// cashflow插入数据
		WsjCashFlowPO cashFlowPO = new WsjCashFlowPO();
		cashFlowPO.setTransactionFlowId(Long.parseLong(bankMoneyFlowPO.getMoneyFlowNo()));
		cashFlowPO.setEnterpriseId(bankMoneyFlowPO.getEnterpriseId());
		cashFlowPO.setAccountId(bankMoneyFlowPO.getAccountId());
		cashFlowPO.setEnterpriseName(bankMoneyFlowPO.getEnterpriseName());
		cashFlowPO.setAmount(bankMoneyFlowPO.getAmount());
		cashFlowPO.setStateId(2);
		cashFlowPO.setStatus(States.CASHFLOW_SUCCED);
		cashFlowPO.setCreateTime(bankMoneyFlowPO.getCreateTime());
		cashFlowPO.setReviewTime(bankMoneyFlowPO.getSecondTime());
		cashFlowPO.setBankCardId(bankMoneyFlowPO.getBankCard());
		wsjTestMapper.addCashFlow(cashFlowPO);
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 初审拒绝 2018年4月26日 下午3:21:03 传入参数 ： 返回结果 ： 注意 ： 状态改为6
	 * 插入初审人员和时间
	 **/
	@Override
	@Transactional
	public void refuseOutGoldFirst(int userId, String moneyFlowNo) {
		// 先查询状态
		int state = wsjTestMapper.findBankMoneyFlowStateBymoneyFlowNo(moneyFlowNo);
		if (state != States.CHECK_NO) {
			throw new RuntimeException("该条记录已被其他管理员处理了");
		}
		// 改变状态
		changeBankMoneyFlowState(moneyFlowNo, States.FIRST_REJECT);
		// 记录初审人员数据
		wsjTestMapper.updateBankMoneyFlowFirstCheckByAndFirstTimeBymoneyFlowNo(userId, moneyFlowNo,
				System.currentTimeMillis());
		// 更改冻结金额和可用金额
		// 查询整条记录
		// 获得账户id
		// 改变前台用户的金额数据
		BankMoneyFlowPO bankMoneyFlowPO = wsjTestMapper.findBankMoneyFlowBymoneyFlowNo(moneyFlowNo);
		double amount = bankMoneyFlowPO.getAmount();
		int accountId = bankMoneyFlowPO.getAccountId();
		wsjTestMapper.updateAccountFreezeAndUsableByAccountId(amount, accountId, System.currentTimeMillis());

		// cashflow插入数据
		WsjCashFlowPO cashFlowPO = new WsjCashFlowPO();
		cashFlowPO.setTransactionFlowId(Long.parseLong(bankMoneyFlowPO.getMoneyFlowNo()));
		cashFlowPO.setEnterpriseId(bankMoneyFlowPO.getEnterpriseId());
		cashFlowPO.setAccountId(bankMoneyFlowPO.getAccountId());
		cashFlowPO.setEnterpriseName(bankMoneyFlowPO.getEnterpriseName());
		cashFlowPO.setAmount(bankMoneyFlowPO.getAmount());
		cashFlowPO.setStateId(2);
		cashFlowPO.setStatus(States.CASHFLOW_DEFEATED);
		cashFlowPO.setCreateTime(bankMoneyFlowPO.getCreateTime());
		cashFlowPO.setEndTime(bankMoneyFlowPO.getFirstTime());
		cashFlowPO.setBankCardId(bankMoneyFlowPO.getBankCard());
		wsjTestMapper.addCashFlow(cashFlowPO);
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 复审拒绝 2018年4月26日 下午3:21:03 传入参数 ： 返回结果 ： 注意 ： 状态改为8
	 * 插入复审人员和时间
	 **/
	@Override
	@Transactional
	public void refuseOutGoldSecond(int userId, String moneyFlowNo) {
		// 先查询状态
		int state = wsjTestMapper.findBankMoneyFlowStateBymoneyFlowNo(moneyFlowNo);
		if (state != States.FIRST_PASS) {
			throw new RuntimeException("该条记录已被其他管理员处理了");
		}
		// 改变状态
		changeBankMoneyFlowState(moneyFlowNo, States.SECOND_REJECT);
		// 记录复审人员数据
		wsjTestMapper.updateBankMoneyFlowSecondCheckByAndSecondTimeBymoneyFlowNo(userId, moneyFlowNo,
				System.currentTimeMillis());

		// 更改冻结金额和可用金额
		// 查询整条记录
		// 获得账户id
		// 改变前台用户的金额数据
		BankMoneyFlowPO bankMoneyFlowPO = wsjTestMapper.findBankMoneyFlowBymoneyFlowNo(moneyFlowNo);
		double amount = bankMoneyFlowPO.getAmount();
		int accountId = bankMoneyFlowPO.getAccountId();
		wsjTestMapper.updateAccountFreezeAndUsableByAccountId(amount, accountId, System.currentTimeMillis());

		// cashflow插入数据
		WsjCashFlowPO cashFlowPO = new WsjCashFlowPO();
		cashFlowPO.setTransactionFlowId(Long.parseLong(bankMoneyFlowPO.getMoneyFlowNo()));
		cashFlowPO.setEnterpriseId(bankMoneyFlowPO.getEnterpriseId());
		cashFlowPO.setAccountId(bankMoneyFlowPO.getAccountId());
		cashFlowPO.setEnterpriseName(bankMoneyFlowPO.getEnterpriseName());
		cashFlowPO.setAmount(bankMoneyFlowPO.getAmount());
		cashFlowPO.setStateId(2);
		cashFlowPO.setStatus(States.CASHFLOW_DEFEATED);
		cashFlowPO.setCreateTime(bankMoneyFlowPO.getCreateTime());
		cashFlowPO.setReviewTime(bankMoneyFlowPO.getSecondTime());
		cashFlowPO.setBankCardId(bankMoneyFlowPO.getBankCard());
		wsjTestMapper.addCashFlow(cashFlowPO);
	}

	/**
	 * 创建人 ： 吴少杰 方法用途 ： 导出excel 2018年5月2日 下午1:07:47 传入参数 ： 返回结果 ： 注意 ：
	 **/
	@Override
	public void creatExcel(PageDTO pageDTO, HttpServletResponse response, EnterpriseDTO enterpriseDTO) {
		String uuid = UUID.randomUUID().toString();
		String name = uuid + ".xls";
		// File fiel = new File(name);
		response.setContentType("application/force-download");// 下载而不是解析
		response.addHeader("Content-Disposition", "attachment;fileName=" + name);// 设置文件名
		response.setCharacterEncoding("UTF-8");
		try {
			OutputStream os = response.getOutputStream();
			WritableWorkbook book = Workbook.createWorkbook(os);
			WritableSheet sheet = book.createSheet("sheet1", 0);
			WritableFont font1 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat cellFormat1 = new WritableCellFormat(font1);
			sheet.setRowView(0, 400);// 行高
			// 设置样式
			WritableFont font2 = new WritableFont(WritableFont.ARIAL, 13, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat cellFormat2 = new WritableCellFormat(font2);
			// 设置内容

			////////////////////////////////// 入金待审核
			List<String> headers = new ArrayList<>();
			if ((enterpriseDTO.getStates().contains(1) || enterpriseDTO.getStates().contains(13))
					&& enterpriseDTO.getOperationType() == 1) {
				// 设置标题
				headers.add(0, "流水编号");
				headers.add(1, "企业名称");
				headers.add(2, "入金金额");
				headers.add(3, "申请时间");
				headers.add(4, "银行卡号");
				for (int i = 0; i < headers.size(); i++) {
					sheet.setColumnView(i, 40);// 列宽
					Label label = new Label(i, 0, headers.get(i), cellFormat1);
					sheet.addCell(label);
				}
				List<BankMoneyFlowPO> bankMoneyFlowPOs = pageDTO.getRows();
				// 设置内容
				for (int i = 0; i < bankMoneyFlowPOs.size(); i++) {
					sheet.setRowView(i + 1, 400);// 行高
					Label label11;
					Label label22;
					Label label33;
					Label label44;
					Label label55;
					if (bankMoneyFlowPOs.get(i).getBankTransactionNo() != null
							&& !bankMoneyFlowPOs.get(i).getBankTransactionNo().equals("")) {
						label11 = new Label(0, i + 1, bankMoneyFlowPOs.get(i).getBankTransactionNo(), cellFormat2);
					} else {
						label11 = new Label(0, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getEnterpriseName() != null
							&& !bankMoneyFlowPOs.get(i).getEnterpriseName().equals("")) {
						label22 = new Label(1, i + 1, bankMoneyFlowPOs.get(i).getEnterpriseName(), cellFormat2);
					} else {
						label22 = new Label(1, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getAmount() != null) {
						label33 = new Label(2, i + 1, bankMoneyFlowPOs.get(i).getAmount().toString(), cellFormat2);
					} else {
						label33 = new Label(2, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getCreateTime() != null) {
						Date date = new Date(bankMoneyFlowPOs.get(i).getCreateTime());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						String d = format.format(date);
						label44 = new Label(3, i + 1, d, cellFormat2);
					} else {
						label44 = new Label(3, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getBankCard() != null
							&& !bankMoneyFlowPOs.get(i).getBankCard().equals("")) {
						label55 = new Label(4, i + 1, bankMoneyFlowPOs.get(i).getBankCard().toString(), cellFormat2);
					} else {
						label55 = new Label(4, i + 1, "数据缺失", cellFormat2);
					}

					sheet.addCell(label11);
					sheet.addCell(label22);
					sheet.addCell(label33);
					sheet.addCell(label44);
					sheet.addCell(label55);
				}

			}

			////////////////////////////////// 入金已审核
			if ((enterpriseDTO.getStates().contains(2) || enterpriseDTO.getStates().contains(3))
					&& enterpriseDTO.getOperationType() == 1) {
				// 设置标题
				headers.add(0, "流水编号");
				headers.add(1, "企业名称");
				headers.add(2, "入金金额");
				headers.add(3, "申请时间");
				headers.add(4, "完成时间");
				headers.add(5, "银行卡号");
				headers.add(6, "状态");
				for (int i = 0; i < headers.size(); i++) {
					sheet.setColumnView(i, 40);// 列宽
					Label label = new Label(i, 0, headers.get(i), cellFormat1);
					sheet.addCell(label);
				}
				List<BankMoneyFlowPO> bankMoneyFlowPOs = pageDTO.getRows();
				// 设置内容
				for (int i = 0; i < bankMoneyFlowPOs.size(); i++) {
					sheet.setRowView(i + 1, 400);// 行高
					Label label11;
					Label label22;
					Label label33;
					Label label44;
					Label label55;
					Label label66;
					Label label77;
					if (bankMoneyFlowPOs.get(i).getBankTransactionNo() != null
							&& !bankMoneyFlowPOs.get(i).getBankTransactionNo().equals("")) {
						label11 = new Label(0, i + 1, bankMoneyFlowPOs.get(i).getBankTransactionNo(), cellFormat2);
					} else {
						label11 = new Label(0, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getEnterpriseName() != null
							&& !bankMoneyFlowPOs.get(i).getEnterpriseName().equals("")) {
						label22 = new Label(1, i + 1, bankMoneyFlowPOs.get(i).getEnterpriseName(), cellFormat2);
					} else {
						label22 = new Label(1, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getAmount() != null) {
						label33 = new Label(2, i + 1, bankMoneyFlowPOs.get(i).getAmount().toString(), cellFormat2);
					} else {
						label33 = new Label(2, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getCreateTime() != null) {
						Date date = new Date(bankMoneyFlowPOs.get(i).getCreateTime());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						String d = format.format(date);
						label44 = new Label(3, i + 1, d, cellFormat2);
					} else {
						label44 = new Label(3, i + 1, "数据缺失", cellFormat2);
					}
					
					if (bankMoneyFlowPOs.get(i).getEndTime() != null) {
						Date date = new Date(bankMoneyFlowPOs.get(i).getEndTime());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						String d = format.format(date);
						label55 = new Label(4, i + 1, d, cellFormat2);
					} else {
						label55 = new Label(4, i + 1, "数据缺失", cellFormat2);
					}

					if (bankMoneyFlowPOs.get(i).getBankCard() != null
							&& !bankMoneyFlowPOs.get(i).getBankCard().equals("")) {
						label66 = new Label(5, i + 1, bankMoneyFlowPOs.get(i).getBankCard().toString(), cellFormat2);
					} else {
						label66 = new Label(5, i + 1, "数据缺失", cellFormat2);
					}
					
					if (bankMoneyFlowPOs.get(i).getState() == 3) {
						label77 = new Label(6, i + 1, "入金拒绝", cellFormat2);
					} else if(bankMoneyFlowPOs.get(i).getState() == 2){
						label77 = new Label(6, i + 1, "入金通过", cellFormat2);
					}else{
						label77 = new Label(6, i + 1, "其他状态", cellFormat2);
					}

					
					sheet.addCell(label11);
					sheet.addCell(label22);
					sheet.addCell(label33);
					sheet.addCell(label44);
					sheet.addCell(label55);
					sheet.addCell(label66);
					sheet.addCell(label77);
				}

			}
			book.write();
			book.close();
		} catch (Exception e) {
			throw new RuntimeException("excel生成失败");
		}
	}

	
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询风控初审等数据
	 * 2018年5月4日 
	 * 下午2:13:57
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@Override
	public PageDTO findWindFirstCheck(WsjVo wsjVo) {
		int count = wsjTestMapper.findWindFirstCheckByStateCount(wsjVo);
		wsjVo.setStartPage((wsjVo.getPage()-1)*wsjVo.getRowNum());
		List<BillDTO> bills = wsjTestMapper.findWindFirstCheckByState(wsjVo);
		PageDTO p = PageDTO.getPagination(count, bills);
		return p;
	}

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  上传尽调报告 
	 * 2018年5月7日 
	 * 上午10:36:34
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@Transactional(readOnly=false)
	@Override
	public void uploadReport(int userId, MultipartFile[] files, Integer billId, HttpServletRequest request) {
		
		//查询是否已经被同意  status是否==0
		//懒得写
		List<BackAttachmentBO> backAttachmentBOs = new ArrayList<>();
		List<BackAttachmentBillPO> backAttachmentBillPOs = new ArrayList<>();
		//上传文件
		for(int i = 0;i<files.length;i++){
			MultipartFile file = files[i];
			//获取后缀名
			String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			//路径
			String path = "D:/复杂美"+RESPONSEBLE_PATH+"/";
			//系统时间
			long time = System.currentTimeMillis();
			//文件名
			String fileName = ""+time+billId+i;
			
			//路径加文件名
			File f = new File(path,fileName+suffixName);
			if(!f.exists()){
				f.mkdirs();
			}
			//上传
			try {
				file.transferTo(f);
			} catch (IllegalStateException e1) {
				System.out.println(e1.getMessage());
				throw new RuntimeException("文件上传失败");
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				throw new RuntimeException("文件上传失败");
			}
			
			//封装实体
			BackAttachmentBO backAttachmentBO = new BackAttachmentBO();
			backAttachmentBO.setAttachmentId(""+time+billId+i);
			backAttachmentBO.setAttachmentName(file.getOriginalFilename());
			backAttachmentBO.setAttachmentType(suffixName);
			backAttachmentBO.setAttachmentUrl(path+fileName+suffixName);
			backAttachmentBO.setCreateTime(time);
			backAttachmentBO.setOperatorId(userId);
			backAttachmentBO.setUpdateTime(time);
			backAttachmentBOs.add(backAttachmentBO);
			//封装实体
			BackAttachmentBillPO backAttachmentBillPO = new BackAttachmentBillPO();
			backAttachmentBillPO.setAttachmentId(""+time+billId+i);
			backAttachmentBillPO.setBillId(billId);
			backAttachmentBillPOs.add(backAttachmentBillPO);
		}
		//数据库插入数据
		try{
			wsjTestMapper.addBackAttachments(backAttachmentBOs);
			wsjTestMapper.addBackAttachmentBills(backAttachmentBillPOs);
		} catch(Exception e1){
			System.out.println(e1.getMessage());
			throw new RuntimeException("插入数据失败");
		}
	}

	@Override
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查找尽调报告文件路径
	 * 2018年5月7日 
	 * 下午2:27:01
	 * 传入参数  ：  billId
	 * 返回结果  ：  文件路径集合
	 * 注意  ：  
	 **/
	public List<String> findReportFile(Integer billId) {
		List<String> paths = wsjTestMapper.findReportFileByBillId(billId);
		return paths;
	}

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  管理查看页面数据加载
	 * 2018年5月8日 
	 * 下午1:29:31
	 * 传入参数  ：  enterpriseDTO
	 * 返回结果  ：  PageDTO
	 * 注意  ：  
	 **/
	@Override
	public PageDTO findManagerSee(EnterpriseDTO enterpriseDTO) {
		enterpriseDTO.setStartPage(enterpriseDTO.getPage() * enterpriseDTO.getRowNum() - enterpriseDTO.getRowNum());
		List<EnterprisePO> enterprisePOs =  wsjTestMapper.findEnterpriseLimmit(enterpriseDTO);
		int total =  wsjTestMapper.findEnterpriseLimitNum(enterpriseDTO);
		PageDTO pageDTO = PageDTO.getPagination(total, enterprisePOs);
		return pageDTO;
	}

	@Override
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  更改授信额度
	 * 2018年5月8日 
	 * 下午3:52:00
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	public void setLimit(String enterpriseId, Integer creditLine) {
		wsjTestMapper.changeEnterpriseCreditLine(enterpriseId,creditLine);
		
	}

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询企业是否存在
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@Override
	public void findEnterpriseExist(String enterpriseId) {
		int count = wsjTestMapper.findEnterpriseExist(enterpriseId);
		if(count == 0){
			throw new RuntimeException("该企业不存在");
		}
	}

	@Override
	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询用户账户信息
	 * 2018年5月8日 
	 * 下午4:59:25
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	public PageDTO findBackUser(BackUserVO backUserVO) {
		backUserVO.setStartPage(backUserVO.getPage()*backUserVO.getRowNum()-backUserVO.getRowNum());
		List<BackUserPO> backUserPOs = wsjTestMapper.findBackUser(backUserVO);
		int count = wsjTestMapper.findBackUserNum(backUserVO);
		return PageDTO.getPagination(count, backUserPOs);
	}

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  分配角色
	 * 2018年5月8日 
	 * 下午5:39:59
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@Override
	@Transactional(readOnly=false)
	public void distributionRole(Integer userId, List<Integer> roleIds) {
		
		//查询是否存在该用户
		int count = wsjTestMapper.findBackBackUserExist(userId);
		if(count==0){
			throw new RuntimeException("该用户不存在");
		}
		//查询是否存在该角色
		for(int roleId : roleIds){
			int count1 = wsjTestMapper.findBackRoleExist(roleId);
			if(count1==0){
				throw new RuntimeException("该角色不存在");
			}
		}
		
		//先删除用户角色
		wsjTestMapper.deleteBackUserRole(userId);
		//在插入用户角色
		wsjTestMapper.addBackUserRole(userId,roleIds);
	}

	/**
	 * 创建人  ：  吴少杰
	 * 方法用途  ：  查询所有用户角色，并判断是否具有 该角色
	 * 2018年5月9日 
	 * 上午9:54:39
	 * 传入参数  ：  
	 * 返回结果  ：  
	 * 注意  ：  
	 **/
	@Override
	public List<BackRoleDTO> FindUserRole(Integer userId) {
		//判断该用户是否存在
		int count = wsjTestMapper.findBackBackUserExist(userId);
		if(count==0){
			throw new RuntimeException("该用户不存在");
		}
		//查询所有角色
		List<BackRoleDTO> backRoleDTOs = wsjTestMapper.findAllBackRole();
		//查询用户所具有的权限
		List<Integer> roleIds = wsjTestMapper.findBackUserRole(userId);
		for(BackRoleDTO backRoleDTO : backRoleDTOs){
			if(roleIds.contains(backRoleDTO.getRoleId())){
				backRoleDTO.setIsSelected(1);
			}
		}
		return backRoleDTOs;
	}

}
