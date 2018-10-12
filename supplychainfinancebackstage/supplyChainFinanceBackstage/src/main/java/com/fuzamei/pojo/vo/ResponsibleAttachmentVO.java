package com.fuzamei.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author ylx
 * @Descri 尽职调查附件的实体类
 */
@Setter
@Getter
@ToString
public class ResponsibleAttachmentVO {
	private String responsibleAttachmentId;//附件id
	private String responsibleAttachmentName;//附件名
	private String responsibleAttachmentUrl;//附件url地址
}
