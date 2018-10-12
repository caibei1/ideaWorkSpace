package com.fuzamei.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BackAttachmentDTO {
	private String attachmentId;//附件id
	private String attachmentName;//附件名
	private String attachmentType;//附件类型
	private String attachmentUrl;//附件url
	private String operatorId;//操作人id
	private Long createTime;//创建时间
	private Long updateTime;//更新时间
}
