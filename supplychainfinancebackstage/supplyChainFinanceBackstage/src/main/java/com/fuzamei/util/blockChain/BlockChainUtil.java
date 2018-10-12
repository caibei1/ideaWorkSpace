/**
 * 
 */
package com.fuzamei.util.blockChain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.HexStringUtil;
import com.fuzamei.util.HttpRequest;
import com.fuzamei.util.JSONUtil;
import com.google.protobuf.ByteString;

/** 

 * @ClassName: BlockChainUtil 

 * @Description: TODO(这里用一句话描述这个类的作用) 

 * @author maamin 

 * @date 2017-8-21 下午1:35:52 

 * 
 

 */
@Component
public class BlockChainUtil {
/**
	
	* @Title: sendPost 
	
	* @Description: TODO(像区块链发送数据) 
	
	* @param @return    signdata：注册的签名
	
	* @return String    返回类型 
	
	* @throws
	 */
	/*public static boolean sendPostParam(ProtobufBean protobufBean){
		HttpRequest httpRequest = new HttpRequest();
		boolean flag = false;
		String signdata = protobufBean.getSignature();
		 String[] params = new String[1];
         params[0] = signdata;
         String jsonstr = JSON.toJSONString(new RequestAccountBean("2.0", "broadcast_tx_commit", null, params));
         try {
			String result =  httpRequest.sendPost("http://114.55.0.107:46657", jsonstr);
			System.out.println("区块链反馈的结果："+result);
			if(result!=null){	//成功
				flag =  true;
			}
			else{
				flag = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		}
         return flag;
	}*/
	/**
	 * 
	
	* @Title: sendPostParam 
	
	* @Description: TODO(放回区块链的结果) 
	
	* @param @param protobufBean
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @throws
	 */
	public static String  sendPostParam(ProtobufBean protobufBean){
		HttpRequest httpRequest = new HttpRequest();
		String blockUrl = ConfReadUtil.getProperty("url_prefix")+"://"+ConfReadUtil.getProperty("block_ip")+":"+ConfReadUtil.getProperty("block_port");
		String result = "";	//存放最终区块链放回的结果
		String signdata = protobufBean.getSignature();
		 String[] params = new String[1];
         params[0] = signdata;
         String jsonstr = JSON.toJSONString(new RequestAccountBean("2.0", "broadcast_tx_commit", null, params));
         try {
			result =  httpRequest.sendPost(blockUrl, jsonstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return result;
	}
	/**
	 * 
	
	* @Title: getHash 
	
	* @Description: TODO(从区块链返回的结果中得到hash值，错误，返回error的信息)----使用与最早的区块链解析的结果,ip为107 
	* 判断逻辑：
	* 先判断error，存在值，不用管，表示请求就错误了
	* error为空，在判断code是否为0，为0表示成功，否则表示失败
	
	* @param @param jsonData
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @throws
	 */
	/*public static boolean vilaResult(String jsonData){
		boolean flag = false;
		//json字符串转换成Map
		Map<String,Object> map = (Map<String,Object>)JSON.parse(jsonData);
		//先拿到error的值
		String errorValue = map.get("error").toString();
		if(!"".equals(errorValue)){	//失败
			flag = false;
		}
		else{	//
			//取出code,判断是否为0
			String result = map.get("result").toString();
			if(result.indexOf("\"code\":0")>-1){	//成功
				flag = true;
			}
			else{
				flag = false;
			}
		}
		return flag;
	}*/
	/**
	 * 
	* @Title: vilaResult
	* @Description: TODO 判断区块链返回的是否成功---该方法适用于新合约
	* @param @param jsonData
	* @param @return
	* @return boolean
	* @author: Ma Amin
	* @date: 2017-9-11 下午3:07:15
	 */
	public static boolean vilaResult(String jsonData){
		boolean flag = false;
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
			flag = true;
		}
		return flag;
	}
	/**
	 * 
	* @Title: sendBlockChain
	* @Description: TODO 发送签名到区块链
	* @param @param qianming
	* @param @return
	* @return boolean
	* @author: Ma Amin
	* @date: 2017-10-15 下午1:44:43
	 */
	public Map<String, Object> sendBlockChain(String qianming){
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;	//区块链成功返回true，失败返回false，默认返回false
		String fruit = "";
		ProtobufBean protobufBean = new ProtobufBean();		//签名对象
		Map<String, Object> mma = JSONUtil.jsonToMap(qianming);
		long instructionId = Long
				.parseLong(mma.get("sid").toString());
		protobufBean.setInstructionId(instructionId);
		protobufBean.setSignature(mma.get("signdata").toString());
		String result = sendPostParam(protobufBean);
		System.out.println(result);
		flag = vilaResult(result);	//区块链返回过来的结果
		Map<String, Object> signMap = JSONUtil.jsonToMap(result);
		Map<String, Object> resultMap = JSONUtil.jsonToMap(signMap.get("result").toString());
		Map<String, Object> check_tx = JSONUtil.jsonToMap(resultMap.get("check_tx").toString());
		Map<String, Object> deliver_tx = JSONUtil.jsonToMap(resultMap.get("deliver_tx").toString());
		if(check_tx.get("data") != null){
			if(!"".equals(check_tx.get("data").toString())){
				String hexStr = check_tx.get("data").toString();
				fruit = HexStringUtil.hexStringToString(hexStr);
			}
		}
		if(deliver_tx.get("data") != null){
			if(!"".equals(deliver_tx.get("data").toString())){
				String hexStr = deliver_tx.get("data").toString();
				fruit = HexStringUtil.hexStringToString(hexStr);
			}
		}
		map.put("flag", flag);
		map.put("data", fruit);
		return map;
		
	}
	
	public static String sendBlockChainMsg(String qianming){
		String fruit = "";
		ProtobufBean protobufBean = new ProtobufBean();		//签名对象
		Map<String, Object> mma = JSONUtil.jsonToMap(qianming);
		long instructionId = Long
				.parseLong(mma.get("sid").toString());
		protobufBean.setInstructionId(instructionId);
		protobufBean.setSignature(mma.get("signdata").toString());
		String result = sendPostParam(protobufBean);
		Map<String, Object> signMap = JSONUtil.jsonToMap(result);
		Map<String, Object> resultMap = JSONUtil.jsonToMap(signMap.get("result").toString());
		Map<String, Object> check_tx = JSONUtil.jsonToMap(resultMap.get("check_tx").toString());
		Map<String, Object> deliver_tx = JSONUtil.jsonToMap(resultMap.get("deliver_tx").toString());
		if(check_tx.get("data") != null){
			if(!"".equals(check_tx.get("data").toString())){
				String hexStr = check_tx.get("data").toString();
				fruit = HexStringUtil.hexStringToString(hexStr);
			}
		}
		if(deliver_tx.get("data") != null){
			String hexStr = check_tx.get("data").toString();
			fruit = HexStringUtil.hexStringToString(hexStr);
		}
		return fruit;
	}
	
	public static void main(String[] args) {
		String sign = "{\"signdata\":\"qgFJCg4KCDM1NzU1MDY3EMjvBQoOCggxMTk4MjQ0MhCgjQYQgIifu4IsGIDQnKWHLCDo/AsqBjU1NzA2MzABOKAGQgg0MDIwNTQzNeIBBjY5NTIzOOoBIEg9GeqoKyMT1aLugAkijYEriKYKmQxedqH5J/7dVJ8T8AGGy/uC7L24LPoBQBCOvyjk9tKTFVlhMK05oScWT3Zow7SSh55ADsaDdZzfd5gKwETDwfQ3rzdzzsIzIDupwXP8Ufp8Uo9w2E/Sig+AAhg=\",\"sid\":\"25018213496448390\"}";
		String s = sendBlockChainMsg(sign);
		System.out.println(s);
	}
}
