package com.fuzamei.constant;

/**
 * 
 * @author ylx[yanglingxiao2009@163.com]
 * @date 2018-3-1 23:48
 * @describe 用于返回消息统一code和message
 *
 */
public enum HintEnum {
	
	DOWNLOAD_SUCCESS(200,"文件下载成功"),
	DOWNLOAD_FAIL(500,"文件下载失败"),
	UPLOAD_SUCCESS(200,"文件上传成功"),
	UPLOAD_FAIL(500,"文件上传失败"),
	QUERY_SUCCESS(200,"查询成功"),
	QUERY_FAIL(500,"查询失败"),
	CHECK_SUCCESS(200,"查看成功"),
	CHECK_FAIL(500,"查看失败"),
	FILE_NOT_FOUND(500,"文件不存在"),
	FILE_CANT_BE_NULL(500,"文件不能为空"),
	OPERATION_SUCCESS(200,"操作成功"),
	OPERATION_FAIL(500,"操作失败"),
	DELETE_SUCCESS(200,"删除成功"),
	DELETE_FAIL(500,"删除失败"),
	NO_AUTH(300,"无权操作"),
	ILLEGAL(300,"非法操作"),
	ACCOUNT_EXP(300,"账号异常"),
	LOGIN_SUCCESS(200,"登陆成功"),
	LOGIN_FAIL(200,"登陆失败"),
	WRONG_USER_PWD(300,"用户名或密码错误"),
	BLOCKCHAIN_ERROR(300,"区块链上链错误"),
	VALI_FAIL(400,"数据校验失败");
	
	
	private int code;//编号
	private String hintMsg;//提示信息
	
	private HintEnum(int code,String hintMsg){
		this.code=code;
		this.hintMsg=hintMsg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getHintMsg() {
		return hintMsg;
	}

	public void setHintMsg(String hintMsg) {
		this.hintMsg = hintMsg;
	}
	
	
}
