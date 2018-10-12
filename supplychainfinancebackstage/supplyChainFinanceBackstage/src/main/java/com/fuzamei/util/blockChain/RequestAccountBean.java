package com.fuzamei.util.blockChain;

/**
 * Created by zhengfan on 2017/5/21.
 * Explain 合约请求体json格式
 */
public class RequestAccountBean {


    /**
     * jsonrpc : 2.0
     * method : tmsp_query
     * params : ["0a460a2094a6e9c5a75358d1662403ccbc6c2a480dbf75a84dc2ec43b69eb3ae3a13f5ca10081a2094a6e9c5a75358d1662403ccbc6c2a480dbf75a84dc2ec43b69eb3ae3a13f5ca1a401b4947339abacd2570a96bb842184ee1efca67051d6269e6fa6dbf211f2663c1c07974980c0235c2ee5a7595c1e27af51b74ca5d23c0e46061cec590c0290500"]
     * id : null
     */

    private String jsonrpc;
    private String method;
    private Object id;
    private String[] params;


    public RequestAccountBean(String jsonrpc, String method, Object id, String[] params) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = id;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
    /*
    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }*/
}
