package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author ylx
 * @descri 附件表实体类
 */
@Setter
@Getter
@ToString
public class AttachmentPO {
	private Integer attachmentId;
	private String attachemntUrl;
	private String attachemntName;
}
