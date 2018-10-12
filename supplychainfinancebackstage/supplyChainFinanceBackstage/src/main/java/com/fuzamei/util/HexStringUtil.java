/**
 * 
 */
package com.fuzamei.util;

/**
 * FileName: HexStringUtil.java
 * @Description :TODO
 * @author 创建人 王斌
 * @date 创建时间 2017-12-6上午11:41:37
 * @version v1.0
 *
 * Modification  History:
 * Date              Author           Version        
 * -------------------------------------------------
 * 2017-12-6           王斌                                          @version          
 *
 * Why & What is modified:
 */
public class HexStringUtil {
	
	/**
	 * 字符串转换成为16进制(无需Unicode编码)
	 * @param str
	 * @return
	 */
	public static String stringToHexString(String str) {
	    char[] chars = "0123456789ABCDEF".toCharArray();
	    StringBuilder sb = new StringBuilder();
	    byte[] bs = str.getBytes();
	    int bit;
	    for (int i = 0; i < bs.length; i++) {
	        bit = (bs[i] & 0x0f0) >> 4;
	        sb.append(chars[bit]);
	        bit = bs[i] & 0x0f;
	        sb.append(chars[bit]);
	        // sb.append(' ');
	    }
	    return sb.toString().trim();
	}
  
    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * @param hexStr
     * @return
     */
    public static String hexStringToString(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
    public static void main(String[] args) {
//    	
//    	String str = "MEUCIGNHfjvtdKkAPUobRI4HN7nt9iANJDUyDFk5opz4Mx/2AiEA+fwaR6Ied417qqDMyYchNPPfV7B7SW6j3EjpH+9fRGw=";
//    	System.out.println(str.length());
    	String result = hexStringToString("6572726F72207369676E");
    	System.out.println(result);
	}
}
