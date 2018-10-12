package com.fuzamei.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fuzamei.mapper.BackAttachmentMapper;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.dto.BackAttachmentDTO;
import com.fuzamei.service.BackAttachmentService;
/**
 * 
 * @author ylx
 * @descri 专门针对后台附件上传或查询使用的service层类
 */
@Service
public class BackAttachmentServiceImpl implements BackAttachmentService {

	@Autowired
	private BackAttachmentMapper backAttachmentMapper;
	
	@Override
	public int checkIfHaveAttachment(Integer billId) {
		return backAttachmentMapper.checkIfHaveAttachment(billId);
	}

	@Override
	public BackAttachmentDTO queryAttachment(String attachmentId) {
		return backAttachmentMapper.queryAttachment(attachmentId);
	}

	@Override
	public List<BackAttachmentDTO> queryDetailAttachments(BillBO billBO) {
		return backAttachmentMapper.queryDetailAttachments(billBO);
	}
	
}
