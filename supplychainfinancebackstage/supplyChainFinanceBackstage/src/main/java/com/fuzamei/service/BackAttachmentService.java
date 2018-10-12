package com.fuzamei.service;

import java.util.List;

import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.dto.BackAttachmentDTO;

public interface BackAttachmentService {

	int checkIfHaveAttachment(Integer billId);

	BackAttachmentDTO queryAttachment(String attachmentId);

	List<BackAttachmentDTO> queryDetailAttachments(BillBO billBO);
	
}
