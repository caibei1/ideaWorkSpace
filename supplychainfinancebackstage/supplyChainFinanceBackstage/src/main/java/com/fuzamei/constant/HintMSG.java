package com.fuzamei.constant;

/**
 * 
 * @author ylx
 * @descibe: 提示信息的类
 */
public class HintMSG {
	private HintMSG() {
		throw new AssertionError("禁止实例化");
	}

	public static final String DOWNLOAD_SUCCESS = "文件下载成功";
	public static final String DOWNLOAD_FAIL = "文件下载失败";
	public static final String UPLOAD_SUCCESS = "文件上传成功";
	public static final String UPLOAD_FAIL = "文件上传失败";
	public static final String QUERY_SUCCESS = "查询成功";
	public static final String QUERY_FAIL = "查询失败";
	public static final String CHECK_SUCCESS = "查看成功";
	public static final String CHECK_FAIL = "查看失败";
	public static final String FILE_NOT_FOUND = "文件不存在";
	public static final String FILE_CANT_BE_NULL = "文件不能为空";
	public static final String OPERATION_SUCCESS = "操作成功";
	public static final String OPERATION_FAIL = "操作失败";
	public static final String DELETE_SUCCESS = "删除成功";
	public static final String DELETE_FAIL = "删除失败";
	public static final String NO_AUTH = "无权操作";
	public static final String ILLEGAL = "非法操作";
	public static final String ACCOUNT_EXP = "账号异常";
	public static final String LOGIN_SUCCESS = "登陆成功";
	public static final String LOGIN_FAIL = "登陆失败";
	public static final String TOKEN_FAIL = "TOKEN验证失败,请重新登录";
	public static final String NULL_AUTH = "Authorization为空";
	
}
