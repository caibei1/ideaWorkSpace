package com.fuzamei.mapper;

import com.fuzamei.pojo.bo.DownLoadBO;
import com.fuzamei.pojo.po.AttachmentPO;

public interface AttachmentMapper {

	AttachmentPO queryAttachmentInfomation(DownLoadBO downLoadBO);

}
