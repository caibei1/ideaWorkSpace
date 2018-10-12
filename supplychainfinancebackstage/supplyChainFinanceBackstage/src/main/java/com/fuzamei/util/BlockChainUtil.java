package com.fuzamei.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.pojo.protobuf.RequestBean;
import com.fuzamei.util.blockChain.sm2.Util;
import com.mchange.util.AssertException;

public class BlockChainUtil {
	private BlockChainUtil(){
		throw new AssertException("instaniation is not allowed");
	}
	
	/**
	 * 向区块链丢签名，并返回结果
	 * @param protobufBean
	 * @return
	 */
	public static String sendPost(ProtoBufBean protobufBean){
		HttpRequest httpRequest = new HttpRequest();
		String blockUrl = ConfReadUtil.getProperty("blockChain_url");
		String jsonstr = JSON.toJSONString(RequestBean.getInstance(protobufBean.getSignature()));
		return httpRequest.sendPost(blockUrl, jsonstr);
	}
	
	/**
	 * 
	 * @param jsonData
	 * @return
	 */
	public static boolean checkResult(String jsonData){
		Map<String,Object> map = JSONUtil.jsonToMap(jsonData);	//总的map
		//得到第一个code的值
		String result = map.get("result").toString();	//得到result节点
		Map<String,Object> resultMap = JSONUtil.jsonToMap(result);
		String check_tx = resultMap.get("check_tx").toString();
		Map<String,Object> check_txMap = JSONUtil.jsonToMap(check_tx);
		String code1 = check_txMap.get("code").toString();
		//得到第二个code的值
		String deliver_tx = resultMap.get("deliver_tx").toString();
		Map<String,Object> deliver_txMap = JSONUtil.jsonToMap(deliver_tx);
		String code2 = deliver_txMap.get("code").toString();
		if(("0".equals(code1))&&("0".equals(code2))){	//两个都等于0才等于成功
			return true;
		}
		return false;
	}
	
	/**
	 * 如果验证错误，返回区块链错误的信息
	 * @param jsonData
	 * @return
	 */
	public static String getErrorMessage(String jsonData){
		Map<String,Object> map = JSONUtil.jsonToMap(jsonData);	//总的map
		//得到第一个code的值(如果第一个有值就取第一个，如果第一个没有值取第二个)
		String result = map.get("result").toString();	//得到result节点
		Map<String,Object> resultMap = JSONUtil.jsonToMap(result);
		String check_tx = resultMap.get("check_tx").toString();
		Map<String,Object> check_txMap = JSONUtil.jsonToMap(check_tx);
		String data = check_txMap.get("data").toString();
		if("".equals(data.trim())){
			String deliver_tx = resultMap.get("deliver_tx").toString();
			Map<String,Object> deliver_txMap = JSONUtil.jsonToMap(deliver_tx);
			String data2 = deliver_txMap.get("data").toString();
			return Util.hexStringToString(data2,2);
		}
		return Util.hexStringToString(data,2);
	}
	
	
	
	public static void main(String[] args) {
		ProtoBufBean protobufBean = ProtoBufBean.getInstance(100000000000000L, "GhjKBDEyMzQSBmhlaGVoZRoCAQSiAUAS/KBSFy2p8AYC4/jnv2YJmM3bGBRg1lr4r9j5t/Nyfyr6SOG0ueNX2ynqIxF9wDK9wjf8J2JpBjcuBXk5NaBhqAGN9Pj/5rfJ8DWyAUcwRQIhAJMi7kqcUXv6Lu8JyKJU+sSRkLo/AVKdyEVOQdZmqxYpAiBb3BgEAMS9Cre3d3HEvpSeuZkgVjoivZky1EWu5kuf7bgBBg==");
		System.out.println(sendPost(protobufBean));
		System.out.println(Util.hexStringToString("756E657870656374656420454F46",2));
	}
}
