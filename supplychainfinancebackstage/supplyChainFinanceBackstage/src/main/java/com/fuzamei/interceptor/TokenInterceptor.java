package com.fuzamei.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.fuzamei.constant.HintMSG;
import com.fuzamei.service.UserService;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

/**
 * @className:TokenInterceptor[每次访问需要在请求头上带上tokenId，key为Authorization，不然会被拦截器拦截]
 * @author:ylx--->[yanglingxiao2009@163.com]
 * @date:2018年3月2日21:11
 * @version v1.0
 */
public class TokenInterceptor extends HandlerInterceptorAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenInterceptor.class);
	
	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		try {
			LOGGER.info("对用户的token信息进行校验");
			String tokenAndUserId = request.getHeader("Authorization");
			if(tokenAndUserId==null||"".equals(tokenAndUserId.trim())){//请求头不能为null
				throw new RuntimeException(HintMSG.NULL_AUTH);
			}
			String token =ValidationUtil.checkBlankAndAssignString(tokenAndUserId.split("&")[0].replace("Bearer", ""));//token校验
			int userId = ValidationUtil.checkAndAssignInt(tokenAndUserId.split("&")[1]);//账户id校验
			boolean flag = userService.verificationToken(userId, token);//校验userId和token值是否符合
			if(!flag){//token校验未通过
				throw new RuntimeException(HintMSG.TOKEN_FAIL);
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("拦截token信息发生异常:{}",e.getMessage());
			PrintWriter writer = response.getWriter();
			ResultResp resultResp = ResultResp.getResult(300, false, e.getMessage(), null);
			response.setContentType("application/json");
			writer.append(JSON.toJSONString(resultResp));
			return false;
		}
		
	}

}
