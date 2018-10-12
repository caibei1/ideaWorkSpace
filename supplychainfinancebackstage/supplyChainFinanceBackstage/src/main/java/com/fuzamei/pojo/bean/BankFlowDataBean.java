package com.fuzamei.pojo.bean;


public class BankFlowDataBean implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String platformtoken;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlatformtoken() {
		return platformtoken;
	}

	public void setPlatformtoken(String platformtoken) {
		this.platformtoken = platformtoken;
	}

	public String getAccountid() {
		return accountid;
	}

	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}

	public String getTran_FLOW() {
		return tran_FLOW;
	}

	public void setTran_FLOW(String tran_FLOW) {
		this.tran_FLOW = tran_FLOW;
	}

	public String getBflow() {
		return bflow;
	}

	public void setBflow(String bflow) {
		this.bflow = bflow;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getAmt1() {
		return amt1;
	}

	public void setAmt1(String amt1) {
		this.amt1 = amt1;
	}

	public String getAccno2() {
		return accno2;
	}

	public void setAccno2(String accno2) {
		this.accno2 = accno2;
	}

	public String getAcc_NAME1() {
		return acc_NAME1;
	}

	public void setAcc_NAME1(String acc_NAME1) {
		this.acc_NAME1 = acc_NAME1;
	}

	public String getCadbank_Nm() {
		return cadbank_Nm;
	}

	public void setCadbank_Nm(String cadbank_Nm) {
		this.cadbank_Nm = cadbank_Nm;
	}

	public String getCreattime() {
		return creattime;
	}

	public void setCreattime(String creattime) {
		this.creattime = creattime;
	}

	public String getFlag1() {
		return flag1;
	}

	public void setFlag1(String flag1) {
		this.flag1 = flag1;
	}

	public String getRltv_ACCNO() {
		return rltv_ACCNO;
	}

	public void setRltv_ACCNO(String rltv_ACCNO) {
		this.rltv_ACCNO = rltv_ACCNO;
	}

	public int getStata() {
		return stata;
	}

	public void setStata(int stata) {
		this.stata = stata;
	}

	public String getDet() {
		return det;
	}

	public void setDet(String det) {
		this.det = det;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String accountid;
	private String tran_FLOW;//交易流水号
	private String  bflow;	//企业支付流水号
	private String   amt;	//发生额
	private String   amt1;	//余额
	private String  accno2;	//对方账号
	private String acc_NAME1;//对方户名
	
	private String cadbank_Nm;//对方账户开户行名称
	
	private String creattime;//交易日期
	
	private String flag1;//借贷标志  0, 借  1，贷
	
	private String rltv_ACCNO;//关联账号
	
	private int stata;
	
	private String det;//备注

	

}
