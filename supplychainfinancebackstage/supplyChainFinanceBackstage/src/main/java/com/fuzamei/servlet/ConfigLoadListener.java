/**  
 * @Title: ConfigLoadListener.java
 * @Package: com.fuzamei.servlet
 * @Description: TODO 
 * @author: Ma Amin
 * @date: 2017-10-15 下午7:17:56
 */
package com.fuzamei.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fuzamei.mapper.BlockChainHistoryMapper;
import com.fuzamei.pojo.bo.BlockChainHistoryBO;
import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.util.BlockChainUtil;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.ProtoBuf4SM2Util;

/**
 * 
* @file_name: ConfigLoadListener.java
* @Description: TODO 平台初始化
* @author: Ma Amin
* @date: 2018-1-4 下午1:48:15 
* @version 1.0
 */
@Component
public class ConfigLoadListener implements ServletContextListener{
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ProtoBufBean protoBufBean = ProtoBuf4SM2Util.initPlatform(ConfReadUtil.getProperty("publicKeySM2"), ConfReadUtil.getProperty("privateKeySM2"), ConfReadUtil.getProperty("superadminPubKeySM2"));
		String result = BlockChainUtil.sendPost(protoBufBean);
		boolean checkResult = BlockChainUtil.checkResult(result);
		if(checkResult){
			System.out.println("++++++++++++++++++++++++++++++平台初始化成功+"+"++++++++++++++++++++++++++++++");
		}else{
			String errorMessage = BlockChainUtil.getErrorMessage(result);
			System.out.println("++++++++++++++++++++++++++++++平台初始化失败:"+errorMessage+"++++++++++++++++++++++++++++++");
		}
//		System.out.println("++++++++++++++++++++++++++++++平台初始化（测试版本）"+"++++++++++++++++++++++++++++++");
	}
}