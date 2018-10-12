/**  
 * @Title: JSONUtil.java
 * @Package: com.fuzamei.util
 * @Description: TODO 
 * @author: Ma Amin
 * @date: 2017-8-31 下午3:42:34
 */
package com.fuzamei.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;



/** 
 * @file_name: JSONUtil.java
 * @Description: TODO 对json操作的工具类
 * @author: Ma Amin
 * @date: 2017-8-31 下午3:42:34 
 * @version 1.0
 */
public class JSONUtil {
	/**
	 * 
	* @Title: jsonToMap
	* @Description: TODO 将json字符创转换成map
	* @param @param data
	* @param @return
	* @return Map<String,Object>
	* @author: Ma Amin
	* @date: 2017-8-31 下午3:43:08
	 */
	public static Map<String,Object> jsonToMap(String data){
		return (Map<String,Object>)JSON.parse(data);
	}
	/**
	 * 
	* @Title: getJsonMap
	* @Description: TODO 将查询出来的结果封装成json数据返回
	* @param @param code--状态码
	* @param @param success--true和false，成功或者失败
	* @param @param message--消息
	* @param @param data--返回的数据
	* @param @return
	* @return Map<String,Object>
	* @author: Ma Amin
	* @date: 2017-8-31 下午3:52:31
	 */
	public static Map<String,Object> getJsonMap(int code,boolean success,String message, Object data){
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		map.put("code", code);
		map.put("success", success);
		map.put("message", message);
		map.put("data", data);
		return map;
	}
	/**
	 * 
	* @Title: getJsonMapByForm
	* @Description: TODO 将表单传过来的json数据转换成Map对象
	* @param @param data
	* @param @return
	* @return Map<String,Object>
	* @author: Ma Amin
	* @date: 2017-8-31 下午3:53:28
	 */
	public static Map<String,Object> getJsonMapByForm(String data){
		//username=admin&password=admin
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		String [] arr = data.split("&");
		for(int i = 0; i < arr.length; i ++){
			map.put(arr[i].split("=")[0], arr[i].split("=")[1]);
		}
		return map;
	}
	/**
	 * 
	* @Title: JSONToList
	* @Description: TODO 将json数组装换换为list
	* @param @param data
	* @param @return
	* @return List<Map<String,Object>>
	* @author: Ma Amin
	* @date: 2017-11-14 上午11:14:36
	 */
	public static List<Map<String,Object>> JSONToList(String data){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray jsons = JSONArray.parseArray(data);
		for(int i = 0; i < jsons.size(); i ++){
			Map<String,Object> map = jsonToMap(jsons.get(i).toString());
			list.add(map);
		}
		return list;
	}
}
