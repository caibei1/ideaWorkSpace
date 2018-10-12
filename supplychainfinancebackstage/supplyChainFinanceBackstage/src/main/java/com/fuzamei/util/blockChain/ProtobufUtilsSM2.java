package com.fuzamei.util.blockChain;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fuzamei.pojo.vo.BankMoneyFlowVO;
import com.fuzamei.pojo.vo.SignVerifyVO;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.HexStringUtil;
import com.fuzamei.util.HttpRequest;
import com.fuzamei.util.JSONUtil;
import com.fuzamei.util.blockChain.eddsa.EdDSAEngine;
import com.fuzamei.util.blockChain.eddsa.EdDSAPrivateKey;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSANamedCurveTable;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSAParameterSpec;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSAPrivateKeySpec;
import com.fuzamei.util.blockChain.sm2.SM2Utils;
import com.fuzamei.util.blockChain.sm2.Util;
import com.google.protobuf.ByteString;

import fzmsupply.Api;



/**
 * 
* @file_name: ProtobufUtilsSM2.java
* @Description: TODO SM2签名工具类
* @author: Ma Amin
* @date: 2018-1-4 下午1:29:11 
* @version 1.0
* 将eds算法改成SM2算法
 */
public class ProtobufUtilsSM2 {
	
	
	
	private static HttpRequest httpRequest = new HttpRequest();
	private static String systemPrefix = ConfReadUtil.getProperty("systemPrefix");
	private final static String p1SignMessageURL = ConfReadUtil.getProperty("p1SignMessageURL");
	
	/**
	 * 
	* @Title: initPlatform
	* @Description: TODO 平台初始化
	* @param @return
	* @return ProtobufBean
	* @author: Ma Amin
	* @date: 2017-10-14 下午11:33:26
	 */
	public static ProtobufBean initPlatform(){
		ProtobufBean protobufBean = new ProtobufBean();
		String publikey = ConfReadUtil.getProperty("publicKeySM2");//平台的公钥
		String platformKey = ConfReadUtil.getProperty("privateKeySM2");//平台私钥--最大的
		String superAdminPubKey = ConfReadUtil.getProperty("superadminPubKeySM2");//超级管理员的公钥
		Api.RequestInitPlatform.Builder builder = Api.RequestInitPlatform.newBuilder();
		try {
			builder.setPlatformKey(ByteString.copyFrom(HexUtil.hexString2Bytes(superAdminPubKey)));//平台初始化创建超级管理员(用超级管理员的公钥)
        	SecureRandom secureRandom = new SecureRandom();
            long instructionId = secureRandom.nextLong();	//随机数
            protobufBean.setInstructionId(instructionId);
            //将requestCreateAccount对象签名
            //request携带请求信息
            Api.Request.Builder request = Api.Request.newBuilder();
            request.setInitPlatform(builder);
            request.setInstructionId(instructionId);
            request.setActionId(Api.MessageType.MsgInitPlatform);
            request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(publikey)));
            Api.Request request1 = request.build();
            //用操作者的私钥签名
            byte[] bytePrivateKey = Util.hexToByte(platformKey);
	        String re = Util.encodeHexString(request1.toByteArray());
	        byte[] dataa = re.getBytes();	//将签名数据转换成二进制数组
	        byte []signByteString1 =SM2Utils.sign(bytePrivateKey, dataa);
            ByteString signByteString =  ByteString.copyFrom(signByteString1);		//签名后得到的byteString数据
            //签名后的结果也给request
            request.setSign(signByteString);
            //将携带着签名后的参数信息通过http给合约，在callback里解析
            Api.Request request2 = request.build();
            byte[] requestByte = request2.toByteArray();
            String signdateBase64 = Base64.getEncoder().encodeToString(requestByte);
            protobufBean.setSignature(signdateBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return protobufBean;
	}
	
	/*public static void main(String[] args) throws Exception {
		String key = "444E6EA3EE0C7E0AAA5EE5C6BBC7A2D8DE3FB3FA990AD470232D07FB445F92D7AEED";
		System.out.println(key.length());
		String prikS = Base64.getEncoder().encodeToString(Util.hexToByte(key));
		byte[] decode = Base64.getDecoder().decode(prikS.getBytes());
		System.out.println(Arrays.toString(decode));
		System.out.println(decode.length);
		System.out.println(Arrays.toString(Util.hexToByte(key)));
		System.out.println(Arrays.toString(Util.hexStringToBytes(key)));
		System.out.println(Arrays.toString(HexUtil.hexString2Bytes(key)));
		byte[] bs = new byte[]{68, 78, 110, -93, -18, 12, 126, 10, -86, 94, -27, -58, -69, -57, -94, -40, -34, 63, -77, -6, -103, 10, -44, 112, 35, 45, 7, -5, 68, 95, -110, -41, -82, -19};
		String str = Util.encodeHexString(bs);//将byte数组转化为16进制字符串
		System.out.println(Arrays.toString(str.getBytes()));
		
		System.out.println(str);
		System.out.println("444e6ea3ee0c7e0aaa5ee5c6bbc7a2d8de3fb3fa990ad470232d07fb445f92d7aeed");
		ProtobufBean protobufBean = initPlatform();	
		String result = BlockChainUtil.sendPostParam(protobufBean);
		System.out.println("平台初始化区块链返回的结果："+result);
	}*/
	
	/*public static final ProtobufBean createAdmin(String userId,String publicKey,String personName,String idCardNum){
		ProtobufBean protobufBean = new ProtobufBean();
		String superAdminPUBKey = ConfReadUtil.getProperty("superadminPubKeySM2");
		String superAdminPRIKey = ConfReadUtil.getProperty("superAdminPrivateKeySM2");
		
		//创建builder对象
		Api.RequestCreateAdmin.Builder builder = Api.RequestCreateAdmin.newBuilder();//builder中的参数按照要求设置好
		builder.setPubkey(ByteString.copyFrom(Util.hexToByte(publicKey)));//设置好用户的公钥
		builder.setCertificate(idCardNum);//设置好身份证参数
		builder.setUid(userId);//用户id参数
		builder.setName(personName);//用户的人名
		
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//随机数(这个就是区块链查询的hash值 )
        protobufBean.setInstructionId(instructionId);
        
        //将参数往reuqest里面丢
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setCreateAdmin(builder);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgCreateAdmin);
        request.setPubkey(ByteString.copyFrom(Util.hexToByte(superAdminPUBKey)));//超级管理员的公钥
        //在没有签名的之前，暂时将所有参数进行构建
        Api.Request requestBuilder = request.build();
        //用操作者的私钥签名（平台的私钥）
        byte[] bytePrivateKey = Util.hexToByte(superAdminPRIKey);//私钥的字节数组形式（超级管理员的私钥）
        String requestBuilderString = Util.encodeHexString(requestBuilder.toByteArray());//将参数先转化为二进制，然后转化为16进制字符串
        byte[] sourceData = requestBuilderString.getBytes();//将16进制的参数字符串转化为字节数组
        byte[] signBytes =SM2Utils.sign(bytePrivateKey, sourceData);//进行SM2签名，得到签名的字节数组
        ByteString signByteString =  ByteString.copyFrom(signBytes);//签名转化为ByteString类型
        request.setSign(signByteString);//将签名的ByteString参数设置到request中
        
        //将携带着签名后的参数信息通过http给合约，在callback里解析
        Api.Request finalRequest = request.build();
        
        byte[] requestByte = finalRequest.toByteArray();
        String signdateBase64 = Base64.getEncoder().encodeToString(requestByte);
        protobufBean.setSignature(signdateBase64);
		return protobufBean;
	}*/
	
	public static void main(String[] args) throws UnsupportedEncodingException, IllegalArgumentException {
//		ProtobufBean bean = initPlatform();
//		ProtobufBean bean = createAdmin("1", 
//				"0BC26E5B76C273AA059172D0E8D7DF255B40B29906F68F90B2A5B854A6FF5E395F88699792A3D3E2D9E921956BC34B30394EC65628F08CECBC01E9E4506E71D5", 
//				"小明", "330102198809172714");
//		System.out.println(bean.getSignature());
//		String sendPostParam = BlockChainUtil.sendPostParam(bean);
//		System.out.println(sendPostParam);
//		boolean vilaResult = BlockChainUtil.vilaResult(sendPostParam);
//		System.out.println(vilaResult);
//		System.out.println(new String(Util.hexToByte("706C6174666F726D206578697374")));
//		System.out.println(HexUtil.hexToString("6572727369676E"));
	}
	
//	/**
//	 * 
//	 * @Title: requestApplyWithdraw  
//	 * @Description: 入金申请 
//	 * @return
//	 */
//	public static ProtobufBean requestApplyDeposit(BankMoneyFlowVO bankMoneyFlowVO ,String publicKey) {
//		ProtobufBean protobufBean = new ProtobufBean();
//		Api.RequestApplyDeposit.Builder requestApplyDeposit = Api.RequestApplyDeposit.newBuilder();
//		try {
//			SecureRandom secureRandom = new SecureRandom();
//			long instructionId = secureRandom.nextLong();
//			requestApplyDeposit.setDepositId(bankMoneyFlowVO.getMoneyFlowNo());
//			requestApplyDeposit.setRmb(Long.parseLong(String.valueOf(bankMoneyFlowVO.getAmount()*100)));
//			//设置request信息
//			Api.Request.Builder request = Api.Request.newBuilder();
//			request.setApplyDeposit(requestApplyDeposit);
//			request.setInstructionId(instructionId);
//			request.setActionId(Api.MessageType.MsgApplyDeposit);
//			request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(publicKey)));
//			request.setUid(String.valueOf(bankMoneyFlowVO.getEnterpriseId()));
//			//生成cfca签名所需要的数据
//			Api.Request request1 = request.build();
//			String sourceData = Util.encodeHexString(request1.toByteArray());
//		    String uid = systemPrefix + String.valueOf(bankMoneyFlowVO.getEnterpriseId());
//		    SignVerifyVO signVerifyVO = new SignVerifyVO();
//		    signVerifyVO.setInstructionId(instructionId);
//		    signVerifyVO.setSourceData(sourceData);
//		    signVerifyVO.setUid(uid);
//		    String sign =  ProtobufUtilsSM2.getSignString(signVerifyVO);
//		    //签名处理
//		    ByteString byteSign = ByteString.copyFrom(Base64Utils.decode(sign));
//		    request.setSign(byteSign);
//		    Api.Request requestNew = request.build();
//		    byte[] signatureByte = requestNew.toByteArray();
//		    String signature  = encodeBase64(signatureByte);
//		    //封装protobufBean
//		    protobufBean.setInstructionId(instructionId);
//		    protobufBean.setSignature(signature);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return protobufBean;
//	}
	
	public static String encodeBase64(byte[] input) throws Exception {
		Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
		Method mainMethod = clazz.getMethod("encode", byte[].class);
		mainMethod.setAccessible(true);
		Object retObj = mainMethod.invoke(null, new Object[] { input });
		return (String) retObj;
	}
	
	public static String getSignString(SignVerifyVO signVerifyVO){
		String sourceData = signVerifyVO.getSourceData();
		String uid = signVerifyVO.getUid();
		// 参数封装
		String param = "{uId:\"" + uid + "\",sourceData:\""+sourceData+"\"}";
		String resultMsg = httpRequest.sendPost(p1SignMessageURL, param);
		Map<String,Object> resultMsgMap = JSONUtil.jsonToMap(resultMsg);
		String dataString = resultMsgMap.get("data").toString();
		Map<String,Object> dataStringMap = JSONUtil.jsonToMap(dataString);
		String signature = dataStringMap.get("result").toString();
		return signature;
	}
	public static boolean vilaResult(String jsonData){
		boolean flag = false;
		if(jsonData != null){
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
		}
		return flag;
	}
	
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
	
	public static String analysisBlockChainMsg(String result){
		String fruit = "";
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

}

