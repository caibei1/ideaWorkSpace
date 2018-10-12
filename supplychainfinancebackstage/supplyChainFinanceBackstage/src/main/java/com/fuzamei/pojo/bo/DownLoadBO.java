package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @descri 下载文件的实体类
 */
@Setter
@Getter
@ToString
public class DownLoadBO {
	private String attachmentUrl;//附件下载地址
	private Integer attachmentId;//附件id
}
