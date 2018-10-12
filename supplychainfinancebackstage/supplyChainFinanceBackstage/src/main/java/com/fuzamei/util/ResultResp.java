package com.fuzamei.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResultResp {
	
	private ResultResp(){}
	private ResultResp(int code,boolean success,String message,Object data){
		this.code=code;
		this.success=success;
		this.message=message;
		this.data=data;
	}
	private int code;//返回码
	private boolean success;//返回成功与否
	private String message;//返回信息
	private Object data;//返回的数据
	
	public static final ResultResp getResult(int code,boolean success,String message,Object data){
		return new ResultResp(code,success,message,data);
	};
}
