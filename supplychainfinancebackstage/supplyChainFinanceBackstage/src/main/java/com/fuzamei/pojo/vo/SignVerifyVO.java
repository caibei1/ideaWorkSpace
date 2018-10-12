package com.fuzamei.pojo.vo;

public class SignVerifyVO {
	/**
	 * 证书下载用户的uid，必须是服务器端证书
	 */
	private String uid;
	/**
	 * 要签名的原文
	 */
	private String sourceData;
	/**
	 * instructionId
	 */
	private long instructionId;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}

	public long getInstructionId() {
		return instructionId;
	}

	public void setInstructionId(long instructionId) {
		this.instructionId = instructionId;
	}

	@Override
	public String toString() {
		return "SignVerifyVO [uid=" + uid + ", sourceData=" + sourceData + ", instructionId=" + instructionId + "]";
	}

	
	
}
