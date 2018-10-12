package com.fuzamei.service;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fuzamei.pojo.bo.BackAttachmentBO;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.bo.BillOrderBO;
import com.fuzamei.pojo.bo.CreditBO;
import com.fuzamei.pojo.bo.TongdunBO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.tongdun.TongdunResultTD;
import com.fuzamei.pojo.vo.TongdunVO;
import com.fuzamei.util.PageDTO;

public interface VentureManagementService {

	PageDTO queryToBeVerified(BillBO billBO);

	PageDTO queryVerified(BillBO billBO);

	PageDTO queryBillOrder(BillOrderBO billOrderBO);

	BillDTO queryBillById(BillBO billBO);

	void approveBill(BillBO billBO);

	void rejectBill(BillBO billBO);

	void insertResponsible2Attachment(List<BackAttachmentBO> attachmentList, List<File> attachmentFileList,
			MultipartFile[] files, Integer userId, Integer billId, String directoryPath);

	int checkIfHaveBill(Integer billId);

	void setEnterpriseCreditLine(CreditBO creditBO);

	TongdunResultTD queryTongdunResult(TongdunBO tongdunBO);

	PageDTO queryAllEnterpriseTongdunInfo(TongdunBO tongdunBO);

	Double getBillAvailableTotalAmountById(BillBO billBO);

	PageDTO queryEnterpriseCreditLine(CreditBO creditBO);

	Double getUsedMoneyByEnterpriseId(CreditBO creditBO);

	Double getTotalReturnedMoneyByEnterpriseId(CreditBO creditBO);

	EnterprisePO queryLeftoverLoan(BillBO billBO);

	Integer queryBillStatusById(Integer billId);

}
