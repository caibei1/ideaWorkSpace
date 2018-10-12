package com.fuzamei.constant;

import com.fuzamei.util.ConfReadUtil;

/**
 * 
 * @author 路径常亮累(主要用于文件存放)
 *
 */
public class Path {
	private Path() {
		throw new AssertionError("can not be instaniated");
	}
	public static final String BASE_PATH = ConfReadUtil.getProperty("path.basePath", "conf.properties");//文件上传的根路径
	public static final String RESPONSEBLE_PATH = ConfReadUtil.getProperty("path.responseblePath", "conf.properties");//尽职调查报告上传的目录
	public static final String RESPONSEBLE_ZIP_PATH = ConfReadUtil.getProperty("path.responsebleZipPath", "conf.properties");//尽职调查报告压缩文件上传的目录
	
}
