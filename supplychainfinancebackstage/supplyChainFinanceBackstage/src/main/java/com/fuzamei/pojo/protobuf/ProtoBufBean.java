package com.fuzamei.pojo.protobuf;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProtoBufBean {
	private ProtoBufBean(){}
	private Long instructionId;
    private String signature;
    
    public static final ProtoBufBean getInstance(Long instructionId,String signature){
    	ProtoBufBean protoBufBean = new ProtoBufBean();
    	protoBufBean.setInstructionId(instructionId);
    	protoBufBean.setSignature(signature);
    	return protoBufBean;
    };
}
