package com.fuzamei.util.blockChain;

import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.blockChain.eddsa.EdDSAEngine;
import com.fuzamei.util.blockChain.eddsa.EdDSAPrivateKey;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSANamedCurveTable;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSAParameterSpec;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSAPrivateKeySpec;
import com.google.protobuf.ByteString;

import fzmsupply.Api;



/**	
 * Created by zhengfan on 2017/7/17.
 * Explain
 */
public class ProtobufUtils {
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
		String publikey = ConfReadUtil.getProperty("publicKey"); 	//调用这公钥钥
		String platformPrivateKey = ConfReadUtil.getProperty("platformPrivateKey"); 				//平台公钥 --随便弄一个
		String platformKey = ConfReadUtil.getProperty("privateKey");			//平台私钥--最大的
		Api.RequestInitPlatform.Builder builder = Api.RequestInitPlatform.newBuilder();
		try {
        	builder.setPlatformKey(ByteString.copyFrom(HexUtil.hexString2Bytes(platformKey)));
        	SecureRandom secureRandom = new SecureRandom();
            long instructionId = secureRandom.nextLong();	//随机数
            protobufBean.setInstructionId(instructionId);
            //将requestCreateAccount对象签名
            Api.RequestInitPlatform requestBuyServe = builder.build();
            //request携带请求信息
            Api.Request.Builder request = Api.Request.newBuilder();
            request.setInitPlatform(builder);
            request.setInstructionId(instructionId);
            request.setActionId(Api.MessageType.MsgInitPlatform);
            request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(publikey)));
            Api.Request request1 = request.build();
            //用操作者的私钥签名
            byte[] bytePrivateKey = HexUtil.hexString2Bytes(platformPrivateKey);
            //签名啊
            ByteString signByteString = getSign(request1.toByteArray(), bytePrivateKey);
            //签名后的结果也给request
            request.setSign(signByteString);
            //将携带着签名后的参数信息通过http给合约，在callback里解析
            Api.Request request2 = request.build();
            byte[] requestByte = request2.toByteArray();
//            String signdata = HexUtil.bytes2HexString(requestByte);
            String signdateBase64 = encodeBase64(requestByte);
            protobufBean.setSignature(signdateBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return protobufBean;
	}
  
    private static ByteString getSign(byte[] requestByte, byte[] bytePrivateKey) {
        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("Ed25519");
        byte[] signbyte = new byte[0];
        try {
            Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
            EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(bytePrivateKey, spec);
            PrivateKey sKey = new EdDSAPrivateKey(privKey);
            sgr.initSign(sKey);
            sgr.update(requestByte);
            signbyte = sgr.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return ByteString.copyFrom(signbyte,0,64);
    }
    public static String encodeBase64(byte[]input) throws Exception{  
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");  
        Method mainMethod= clazz.getMethod("encode", byte[].class);  
        mainMethod.setAccessible(true);  
         Object retObj=mainMethod.invoke(null, new Object[]{input});  
         return (String)retObj;  
    }  

}

