package com.fuzamei.util;

import java.util.UUID;

public final class RandomUtil {
	private RandomUtil(){
		throw new AssertionError("instantiation is not permitted");
	}
	
	private static final String STRS="aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
	private static final int LEN4STRS = 62;
	
	private static final String STR="abcdefghijklmnopqrstuvwxyz0123456789";
	private static final int LEN4STR = 36;
	
	private static final String DIG="0123456789";
	private static final int LEN4DIG = 10;
	/**
	 * @describe 获取随机的uuid,除去"-"
	 * @return
	 */
	public static final String getUUidRandom(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * @describe 根据传入的参数生成特定长度的随机字符串(数字和小写字符)
	 * @param length 产生随机字符串的长度
	 * @param withUpperCase 如果是true说明要带入大写字母，false不带入大写字母
	 * @return
	 */
	public static final String getRandomString(int length,boolean withUpperCase){
		StringBuilder sb=new StringBuilder();
		String temp = STR;
		int len = LEN4STR;
		if(withUpperCase){
			temp = STRS;
			len = LEN4STRS; 
		}
		for (int i = 0; i < length; i++) {
			char c =temp.charAt((int)(Math.random()*len));
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * @describe 根据传入的参数生成特定长度的随机字符串(数字)
	 * @param length
	 * @return
	 */
	public static final String getRandomDigits(int length){
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c =DIG.charAt((int)(Math.random()*LEN4DIG));
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param digits int类型数字的位数，不能超过10位，也不能小于1，否则会报异常
	 * @return
	 */
	public static final int getRandomInteger(int digits){
		if(digits<1 || digits>10){
			throw new RuntimeException("out of integer's range");
		}
		int base = (int)Math.pow(10, digits-1);
		int randomSufix = (int) (Math.random()*base*9);
		switch (digits) {
		case 1:
			base=0;
			randomSufix = (int) (Math.random()*10);
			break;
		case 10:
			randomSufix = (int) (Math.random()*(Integer.MAX_VALUE-base+1));
			break;
		default:
			break;
		}
		return base+randomSufix;
	}
	
	/**
	 * 
	 * @param digits long类型数字的位数，不能超过19位，也不能小于1，否则会报异常
	 * @return
	 */
	public static final long getRandomLong(int digits){
		if(digits<1 || digits>19){
			throw new RuntimeException("out of long's range");
		}
		long base = (long)Math.pow(10, digits-1);
		long randomSufix = (long) (Math.random()*base*9);
		switch (digits) {
		case 1:
			base=0;
			randomSufix = (int) (Math.random()*10);
			break;
		case 19:
			randomSufix = (long) (Math.random()*(Long.MAX_VALUE-base+1));
			break;
		default:
			break;
		}
		return base+randomSufix;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			
			System.out.println(getRandomInteger(9));
		}
	}
	
}
