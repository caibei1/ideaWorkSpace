package com.fuzamei.pojo.bean;

import java.util.List;

public class BankFlowResultBean {
	private String code;
	private String message;
	private BankPlatformBean platformToken;
	private List<BankFlowDataBean> data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public BankPlatformBean getPlatformToken() {
		return platformToken;
	}
	public void setPlatformToken(BankPlatformBean platformToken) {
		this.platformToken = platformToken;
	}
	public List<BankFlowDataBean> getData() {
		return data;
	}
	public void setData(List<BankFlowDataBean> data) {
		this.data = data;
	}
}