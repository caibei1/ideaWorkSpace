package com.fuzamei.util.blockChain;

/**
 * Created by zhengfan on 2017/6/20.
 * Explain  请求的操作回执和区块链签名
 */
public class ProtobufBean {

    private long instructionId;
    private String signature;

    public long getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(long instructionId) {
        this.instructionId = instructionId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
