package com.fuzamei.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.fuzamei.pojo.bo.BackAttachmentBO;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.dto.BackAttachmentDTO;

public interface BackAttachmentMapper {

	@Select("select count(*) from back_attachment_bill where bill_id=#{billId}")
	int checkIfHaveAttachment(Integer billId);//查询该单据id下有多少个附件信息

	void insert2BackAttachment(@Param("attachmentList")List<BackAttachmentBO> attachmentList);

	void insert2BackAttachmentBill(@Param("billId")Integer billId, @Param("attachmentList")List<BackAttachmentBO> attachmentList);
	
	@Select("select * from back_attachment where attachment_id=#{attachmentId}")
	BackAttachmentDTO queryAttachment(String attachmentId);

	List<BackAttachmentDTO> queryDetailAttachments(BillBO billBO);

}
