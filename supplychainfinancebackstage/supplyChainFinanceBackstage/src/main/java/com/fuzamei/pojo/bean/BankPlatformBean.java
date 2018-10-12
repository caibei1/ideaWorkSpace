package com.fuzamei.pojo.bean;

public class BankPlatformBean {
		public String getPlatformtoken() {
		return platformtoken;
	}
	public void setPlatformtoken(String platformtoken) {
		this.platformtoken = platformtoken;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getAccountid() {
		return accountid;
	}
	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}
		private String platformtoken;
		private boolean state;
		private String accountid;
		private String facedesc;
		public String getFacedesc() {
			return facedesc;
		}
		public void setFacedesc(String facedesc) {
			this.facedesc = facedesc;
		}
		
}
