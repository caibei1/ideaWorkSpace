package com.fuzamei.pojo.protobuf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RequestBean {
	/**
     * jsonrpc : "2.0"
     * method : "broadcast_tx_commit"
     * params : ["0a460a2094a6e9c5a75358d1662403ccbc6c2a480dbf75a84dc2ec43b69eb3ae3a13f5ca10081a2094a6e9c5a75358d1662403ccbc6c2a480dbf75a84dc2ec43b69eb3ae3a13f5ca1a401b4947339abacd2570a96bb842184ee1efca67051d6269e6fa6dbf211f2663c1c07974980c0235c2ee5a7595c1e27af51b74ca5d23c0e46061cec590c0290500"]
     * id : null
     */
	private RequestBean(){}
	private String jsonrpc;
    private String method;
    private Object id;
    private String[] params;
    public static final RequestBean getInstance(String signature){
    	RequestBean requestBean = new RequestBean();
    	requestBean.setJsonrpc("2.0");
    	requestBean.setMethod("broadcast_tx_commit");
    	requestBean.setId(null);
    	requestBean.setParams(new String[]{signature});
    	return requestBean;
    }
}
