package com.fuzamei.util.blockChain;


/*import com.fzm.eddsa.EdDSAPrivateKey;
import com.fzm.eddsa.spec.EdDSANamedCurveTable;
import com.fzm.eddsa.spec.EdDSAParameterSpec;
import com.fzm.eddsa.spec.EdDSAPrivateKeySpec;
import com.fzm.theromus.sha.Keccak;
import com.fzm.theromus.sha.Parameters;
import com.fzm.theromus.utils.HexUtils;*/

import java.util.UUID;

import com.fuzamei.theromus.sha.Keccak;
import com.fuzamei.theromus.sha.Parameters;
import com.fuzamei.theromus.utils.HexUtils;
import com.fuzamei.util.blockChain.eddsa.EdDSAPrivateKey;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSANamedCurveTable;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSAParameterSpec;
import com.fuzamei.util.blockChain.eddsa.spec.EdDSAPrivateKeySpec;

/**
 * Created by zhengfan on 2017/7/13.
 * Explain
 */
public class KeyUtils {

    /**
     * getprivatekey
     * @param password
     * @param random
     * @return
     */
    public static String getPrivateKey(String password, String random) {
        String tempString = "";
        try{
            String key = password + random;
            Keccak keccak = new Keccak();
            String s = HexUtils.getHex(key.getBytes());
            tempString = keccak.getHash(s, Parameters.KECCAK_256);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tempString;
    }
    public static String getPrivateKey1(String random) {
        String tempString = "";
        try{
            String key = random;
            Keccak keccak = new Keccak();
            String s = HexUtils.getHex(key.getBytes());
            tempString = keccak.getHash(s, Parameters.KECCAK_256);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tempString;
    }

    /**
     * getpublickey
     * @param privateKey
     * @return
     */
    public static String getPublicKey(String privateKey) {
        String publickey = "";
        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("Ed25519");
        try {
            EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(HexUtil.hexString2Bytes(privateKey), spec);
            EdDSAPrivateKey sKey = new EdDSAPrivateKey(privKey);
            publickey = HexUtil.bytes2HexString(sKey.getAbyte()).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publickey;
    }

    /**
     * random to getprivatekey
     * @return
     */
    public static String getRandom() {
        String s = "";
        try{
            String str = UUID.randomUUID().toString().replace("-", "");
            s = str.substring(str.length() - 16);
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }
    public static void main(String[] args) {
//		String key = "90b289fda1fb0439158f837bbe60cc1ec99616dd0bc6335d6fd0bf3d22888e20b15a4f6c5c1163b5f80715c9bd87d5118ec4b5668cb29f148eeceec61ddeadc2";
    	
   /* 	*//** 用户注册 **//*
    	String password = "123456";		//要注册用户的密码
		String random = "b3ec927eae2ee505";	//生成的随机数
		String privateKey = getPrivateKey(password, random);	//生成的用户私钥
		String userPublicKey = getPublicKey(privateKey);			//生成用户的公钥
		System.out.println("随机数："+random);
		System.out.println("私钥："+privateKey);
		System.out.println("公钥："+userPublicKey);
		String userUid = "20000";
		String userName = "杭州复杂美科技有限公司787898898981";
		ProtobufBean protobufBean = ProtobufUtils.requestUserCreate(userUid, userPublicKey, privateKey,userName);	//管理员的公钥
		String result = BlockChainUtil.sendPostParam(protobufBean);
		System.out.println(result);*/
		
		
		/*int x=(int)(Math.random()*100000000)+10000000;
		System.out.println(x);*/
		
		/** 先平台初始化  **/
		ProtobufBean protobufBean = ProtobufUtilsSM2.initPlatform();	
		String result = BlockChainUtil.sendPostParam(protobufBean);
		System.out.println("平台初始化区块链返回的结果："+result);
		
		
		/** setAdmin,将用户写入到区块链 **/
    	/*String random  = getRandom();
    	String password = "123456";
    	String privateKey = getPrivateKey(password, random);	//生成的私钥
		String userPublicKey = getPublicKey(privateKey);			//生成的公钥
		System.out.println("随机数："+random);
		System.out.println("私钥："+privateKey);
		System.out.println("公钥："+userPublicKey);
		ProtobufBean protobufBean = ProtobufUtils.requestSetAdmin(userPublicKey);	//管理员的公钥
		String result = BlockChainUtil.sendPostParam(protobufBean);
		System.out.println(result);*/
    	
    	
    	
		//平台的公钥和私钥 
		/*String key = "90b289fda1fb0439158f837bbe60cc1ec99616dd0bc6335d6fd0bf3d22888e20b15a4f6c5c1163b5f80715c9bd87d5118ec4b5668cb29f148eeceec61ddeadc2";
		String privateKeyP = key.substring(0,64);
		String pubkeyP = key.replace(privateKeyP, "");
		System.out.println("平台的私钥："+privateKeyP);
		System.out.println("平台的公钥："+pubkeyP);*/
//		ProtobufUtils.requestSetAdmin(pubkey, type, adminPubkey);
//		String userPublicKey = "f0060c4c286903f7905dcc6f549681ee6446c67f824da5b01e669efc6d383883";
		/*ProtobufBean protobufBean = ProtobufUtils.requestSetAdmin(pubkeyP, "0", userPublicKey,privateKeyP);
		String result1 = BlockChainUtil.sendPostParam(protobufBean);*/
//		System.out.println(result1);
	}


}
