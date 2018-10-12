package com.fuzamei.constant;

public class RegexConstant {
	private RegexConstant(){
		throw new AssertionError("can not be instaniated");
	}
	//车牌号正则表达式
	public static final String CAR_NO="^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
	//订单正则表达式
	public static final String ORDER_ID="^\\d{9}$";
	//箱号正则表达式
	public static final String BOX_NO="^[a-zA-Z0-9]{8}$";
	
	public static final String NUMBER_NO="\\d+(,\\d+)*";//校验传的id是数字，以逗号隔开
	
	public static final String RANDOM_REGEX="^[a-zA-Z0-9]{10}$";//校验传的随机数
	
	public static final String PWD_REGEX="^[a-zA-Z0-9]{5,20}$";//校验密码
	
	public static final String USERNAME_REGEX="^\\w{5,20}$";//校验用户名
	
	public static final String NAME_REGEX="^[\\u4e00-\\u9fa50-9A-Za-z]{1,20}$";//校验用户名
	
	public static final String NUM="^[3-4]$";
	
	public static final String MONEY = "^[\\d]+\\.[\\d]{0,5}|[\\d]+$";//钱的数据格式
	
	public static final String ID_CARD = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
	
	public static final String AUTH_NAME_REGEX="^(/\\w+)+$";//权限url名
	
}
