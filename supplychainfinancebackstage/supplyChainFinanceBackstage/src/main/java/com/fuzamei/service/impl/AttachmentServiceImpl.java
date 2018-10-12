package com.fuzamei.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fuzamei.mapper.AttachmentMapper;
import com.fuzamei.pojo.bo.DownLoadBO;
import com.fuzamei.pojo.po.AttachmentPO;
import com.fuzamei.service.AttachmentService;
@Service
public class AttachmentServiceImpl implements AttachmentService {
	
	@Autowired
	private AttachmentMapper attachmentMapper;
	
	@Override
	public AttachmentPO queryAttachmentInfomation(DownLoadBO downLoadBO) {
		return attachmentMapper.queryAttachmentInfomation(downLoadBO);
	}
	
}
