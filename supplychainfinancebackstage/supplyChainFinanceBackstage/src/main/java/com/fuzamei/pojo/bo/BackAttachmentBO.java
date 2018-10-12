package com.fuzamei.pojo.bo;
/**
 * 
 * @author ylx
 * @descri 后台附件表对应的实体类
 */

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BackAttachmentBO {
	private String attachmentId;//附件id
	private String attachmentName;//附件名
	private String attachmentType;//附件类型
	private String attachmentUrl;//附件url
	private Integer operatorId;//操作人id
	private Long createTime;//创建时间
	private Long updateTime;//更新时间
}
