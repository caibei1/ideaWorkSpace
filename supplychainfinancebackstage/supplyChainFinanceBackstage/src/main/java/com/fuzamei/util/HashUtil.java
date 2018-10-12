package com.fuzamei.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

import com.fuzamei.util.blockChain.sm2.SM2Utils;
import com.fuzamei.util.blockChain.sm2.Sm2KeyPair;
import com.fuzamei.util.blockChain.sm2.Util;
/**
 * 
 * @author ylx
 * @Descri 文件hash计算工具类
 */
public class HashUtil {
	private HashUtil(){
		throw new AssertionError("instantiation is not permitted");
	}
	/** 
     * 根据文件计算出文件的MD5 
     * @param file 
     * @return 
     */  
    public static final String getFileMD5(File file) {  
    	if(!file.exists()){
    		throw new RuntimeException("该文件不存在");
    	}
        if (!file.isFile()) {  
        	throw new RuntimeException("无法对文件夹进行hash计算");
        }  
        MessageDigest digest = null;  
        FileInputStream in = null;  
        byte[] buffer = new byte[1024];  
        int len;  
        try {  
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);  
            while ((len = in.read(buffer)) != -1) {  
                digest.update(buffer, 0, len);  
            }  
        } catch (Exception e) {  
        	throw new RuntimeException("计算文件hash出错");
        }finally {
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					in = null;
				}
			}
		}
        BigInteger bigInt = new BigInteger(1, digest.digest());  
        return bigInt.toString(16);//转换成16进制
    }
    
    public static final String getFileMD5(String filePath){
    	return getFileMD5(new File(filePath));
    }
	
    /** 
     * 计算字符串的hash值
     * @param file 
     * @return 
     */
    public static final String getStringMD5(String str) {
    	MessageDigest digest = null;  
        try {  
            digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
        } catch (Exception e) {  
        	throw new RuntimeException("计算文件hash出错");
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());  
        return bigInt.toString(16);//转换成16进制
    }
//    public static final String getStringMD52(String str) {
//    	MessageDigest digest = null;  
//    	try {  
//    		digest = MessageDigest.getInstance("MD5");
//    	} catch (Exception e) {  
//    		throw new RuntimeException("计算文件hash出错");
//    	}
//    	BigInteger bigInt = new BigInteger(1, digest.digest(str.getBytes()));//这样也能加密(digest这个对象就digest一次，多次digest后的结果是一样的)
//    	return bigInt.toString(16);//转换成16进制
//    }
	
	public static void main(String[] args) throws Exception {
//		MessageDigest digest = null;  
//        try {  
//            digest = MessageDigest.getInstance("MD5");
//            digest.update("123456789".getBytes());
//        } catch (Exception e) {  
//        	throw new RuntimeException("计算文件hash出错");
//        }
//        byte[] bs = digest.digest();
//        BigInteger bigInt = new BigInteger(1, bs);  
//        System.out.println(bigInt);
//        System.out.println(bigInt.toString(36));
//        System.out.println(bs.length);
//        
//        System.out.println(Arrays.toString(bs));
//        
//        System.out.println(Util.byteToHex(bs));
        
//        BigInteger big = new BigInteger("2");
//        for (int i = 0; i < 127; i++) {
//        	big = big.multiply(new BigInteger("2"));
//		}
//        System.out.println(big);
		
//		byte[] bs = new byte[]{22,33,44,55,6,7,22,66,126,2};
//		System.out.println(new BigInteger(1,bs));
//		System.out.println(new BigInteger(1,bs).toString(16));
//		
//		System.out.println(Util.byteToHex(bs));
//		System.out.println(Util.hexToByte("16212C37060716427E02"));
//		System.out.println(new BigInteger("35").toString(36));
//		BigInteger pri = new BigInteger("36D0C88DD79917B40229469453884E58ACC10699F91E4805314F07C2C2301680", 16);
//		BigInteger pub = new BigInteger("DB34AB2A1D64C46560D15D3D63F406553FCFB72021E469A08AFC0848FADD06D5ED412322017D699CE170AF3E9C023316E102096BD9CA6C0059AA14C5F26863F5", 16);
//		System.out.println(pri);
//		System.out.println(pub);
//		BigInteger divide = pub.divide(pri);
//		System.out.println(divide);
//		System.out.println(divide.multiply(pri));
		
		
//		long t1=System.currentTimeMillis();
//		Sm2KeyPair sm2KeyPair;
//		String privateKey;//该账户的SM2私钥
//		String publicKey;//该账户的SM2公钥
//		byte[] sign;
//		String sourceText="1";
//		do {//确保验签能通过
//			System.out.println("test");
//			sm2KeyPair=SM2Utils.generateKeyPair();
//			privateKey = Util.getHexString(sm2KeyPair.getPriKey());
//			publicKey = Util.getHexString(sm2KeyPair.getPubKey());
//			sign = SM2Utils.sign(sm2KeyPair.getPriKey(), sourceText.getBytes());
//		} while (!SM2Utils.verifySign(sm2KeyPair.getPubKey(), sourceText.getBytes(), sign));
//		
//		byte[] signt = SM2Utils.sign(Util.hexToByte(privateKey), "456".getBytes());
//    	
//    	boolean verifySign = SM2Utils.verifySign(Util.hexToByte(publicKey), "456".getBytes(), signt);
//    	
//    	System.out.println(verifySign);
//    	long t2=System.currentTimeMillis();
//    	System.out.println(t2-t1);
		
		ValidationUtil.checkBlankString("000000");
	}

}
