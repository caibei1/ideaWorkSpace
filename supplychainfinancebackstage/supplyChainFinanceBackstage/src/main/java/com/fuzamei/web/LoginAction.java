package com.fuzamei.web;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.HintMSG;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.pojo.bo.UserBO;
import com.fuzamei.pojo.dto.AdminDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.vo.AdminVO;
import com.fuzamei.pojo.vo.BackUserVO;
import com.fuzamei.service.UserService;
import com.fuzamei.util.AuthUrlToJsonUtil;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController
public class LoginAction {
	
	@Autowired
	private UserService userService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAction.class);
	
	/**
	* @Title: login
	* @Description: TODO(用户登陆操作)
	{
		"username":"",
		"password":""
	}
	* @return ResultResp  返回类型
	* @author ylx
	* @date 2018年3月2日 23:47PM
	* @throws
	 */
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ResultResp login(@RequestBody UserBO userBO){
		String username = userBO.getUsername();
		String password = userBO.getPassword();
		LOGGER.info("用户名:{} 进行了登录",username);
		try {//先进行数据校验
			ValidationUtil.checkBlankAndAssignString(username,RegexConstant.USERNAME_REGEX);
			ValidationUtil.checkBlankAndAssignString(password,RegexConstant.PWD_REGEX);
		} catch (Exception e) {
			LOGGER.error("用户名:{},登录校验异常:{}",username,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		String token = UUID.randomUUID().toString().replaceAll("-", "");//生成新token
		try {
			AdminDTO adminDTO = userService.queryAdmin();//看有没有管理员
			if(adminDTO==null){
				LOGGER.warn("平台初始化，创建超级管理员:{}",username);
				AdminVO adminVO = userService.createAdmin(username,password,token);//创建超级管理员,和管理员,插入token,上区块链
				return ResultResp.getResult(HintEnum.LOGIN_SUCCESS.getCode(), true, HintEnum.LOGIN_SUCCESS.getHintMsg(), adminVO);
			}
			UserDetailDTO userDTO = userService.queryUserByNameAndPwd(username,password);
			if(userDTO==null){//用户名密码错误
				LOGGER.error("用户名:{} 输入错误的账户名或密码",username);
				return ResultResp.getResult(HintEnum.WRONG_USER_PWD.getCode(), false, HintEnum.WRONG_USER_PWD.getHintMsg(), null);
			}
			boolean ifFirstLogin = false;//用来判定是否是初次登录的标记
			if(userDTO.getToken()==null||"".equals(userDTO.getToken().trim())){//初次登录
				LOGGER.warn("用户名:{} 初次登录，进行token信息的插入",username);
				userService.insertToken(userDTO.getUserId(), token);
				ifFirstLogin = true;
			} else {//再次登录
				LOGGER.info("用户名:{} 登录，对token信息进行更新",username);
				userService.updateToken(userDTO.getUserId(),token);
			}
			BackUserVO userVO = new BackUserVO();//整合用户信息给前端
			userVO.setUsername(username);
			userVO.setUserId(userDTO.getUserId());
			userVO.setRoles(userDTO.getRoleName());
			userVO.setAuthUrl(userDTO.getAuthorityUrl());
			userVO.setAuthUrlJSON(AuthUrlToJsonUtil.authUrlToJson(userVO.getAuthUrl()));//给前端整合好的authUrl的json格式
			userVO.setIfFirstLogin(ifFirstLogin);//让前端判断用户是否是初次登录(方便进行密码的重新修改页面的跳转)
			userVO.setTokenId("Bearer"+token+"&"+userDTO.getUserId());
			return ResultResp.getResult(HintEnum.LOGIN_SUCCESS.getCode(), true, HintEnum.LOGIN_SUCCESS.getHintMsg(), userVO);
		} catch (Exception e) {
			LOGGER.error("用户名:{},登录异常详情:{}",username,e.getMessage());
			return ResultResp.getResult(500, false, HintMSG.LOGIN_FAIL+":"+e.getMessage(), null);
		}
	}
	
}
