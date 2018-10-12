package com.fuzamei.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GetJsonData {

	   public static String getJsonData(JSONObject jsonParam,String urls) {  
	        StringBuffer sb=new StringBuffer();  
	        try {  
	            // 创建url资源  
	            URL url = new URL(urls);  
	            // 建立http连接  
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	            // 设置允许输出  
	            conn.setDoOutput(true);  
                // 设置允许输入  
                conn.setDoInput(true);  
               // 设置不用缓存  
               conn.setUseCaches(false);  
           	   // 设置传递方式  
               conn.setRequestMethod("POST");  
               // 设置维持长连接  
               conn.setRequestProperty("Connection", "Keep-Alive");  
               // 设置文件字符集:  
               conn.setRequestProperty("Charset", "UTF-8");  
               // 转换为字节数组  
               byte[] data = (jsonParam.toString()).getBytes();  
              // 设置文件长度  
               conn.setRequestProperty("Content-Length", String.valueOf(data.length));  
              // 设置文件类型:  
              conn.setRequestProperty("Content-Type", "application/json");  
              // 开始连接请求  
              conn.connect();        
              OutputStream out = new DataOutputStream(conn.getOutputStream()) ;  
	            // 写入请求的字符串  
	            out.write((jsonParam.toString()).getBytes());  
	            out.flush();  
	            out.close();  
	            //System.out.println(conn.getResponseCode());  
	            // 请求返回的状态  
	            if (HttpURLConnection.HTTP_OK==conn.getResponseCode()){  
	                 //System.out.println("连接成功");  
	                 // 请求返回的数据  
	                InputStream in1 = conn.getInputStream();  
	                try {  
	                      String readLine=new String();  
	                      BufferedReader responseReader=new BufferedReader(new InputStreamReader(in1,"UTF-8"));  
	                      while((readLine=responseReader.readLine())!=null){  
	                        sb.append(readLine).append("\n");  
	                      }  
	                      responseReader.close();  
	                      //System.out.println(sb.toString());  
	                      
	                } catch (Exception e1) {  
	                    e1.printStackTrace();  
	                }  
	            } 
	           
	        } catch (Exception e) {  
	        }  
	        return sb.toString();  
	    }  
	    public static void main(String[] args) {  
	        JSONObject jsonParam = new JSONObject();  
	       /* jsonParam.put("accountid","33050110451700000060");  
	        jsonParam.put("flag", "0"); 
	       String url="http://192.168.33.47:8080/stockmgt_maven_project/blankinfo/WithdrawalsTransferAccounts";//出金做同意接口 */
	       jsonParam.put("accno", "6227001541270375905");
	        jsonParam.put("accnoname", "中国建设银行");
	        jsonParam.put("amount", 0.01);
	        jsonParam.put("pecvopenaccdept", "中国建设银行");
	        jsonParam.put("pevvaccname", "中国建设银行");
	        jsonParam.put("useof", "提现");
	        
	       /* JSONObject subJSON = new JSONObject();
	        subJSON.put("createTime", "1523278946435");
	        jsonParam.put("userDetailDTO", subJSON);*/
	        
	        String url="http://192.168.33.66:5123/stockmgt_maven_project/blankinfo/withTransferAccounts";//入金退款和审核拒绝的接口 
	        //String url="http://192.168.33.47:8080/stockmgt_maven_project/blankinfo/resultAccounts";
	        String data=GetJsonData.getJsonData(jsonParam,url);  
	        System.out.println(data);
	                //返回的是一个[{}]格式的字符串时:                                 
	                //JSONArray jsonArray = new JSONArray(data);                         
	               //返回的是一个{}格式的字符串时:                         
	               //JSONObject obj= new JSONObject(data);        
	    }  
	
	
	

	

	
	
	
	
}
