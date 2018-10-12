package com.fuzamei.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fuzamei.constant.Auth;
import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.pojo.bo.PwdBO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.service.UserService;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController
@RequestMapping("/userCenter")
public class UserCenterAction {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HttpServletRequest req;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCenterAction.class);
	
	/**
	 * @Title modifyPassword
	 * @Description: TODO(用户修改个人密码)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"originalPwd":"",
	 	"newPwd":"",
	 	"newConfirmedPwd"
	 }
	 */
	@RequestMapping(value="/modifyPassword",method = RequestMethod.POST)
	private ResultResp modifyPassword(@RequestBody PwdBO pwdBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		LOGGER.info("用户:{}--->用户修改自己的密码",userId);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.MODIFY_PWD.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			String realPwd = userDetailDTO.getPassword();
			String originalPwd =ValidationUtil.checkBlankAndAssignString(pwdBO.getOriginalPwd(), RegexConstant.PWD_REGEX);
			String newPwd =ValidationUtil.checkBlankAndAssignString(pwdBO.getNewPwd(), RegexConstant.PWD_REGEX);
			String newConfirmedPwd =ValidationUtil.checkBlankAndAssignString(pwdBO.getNewConfirmedPwd(), RegexConstant.PWD_REGEX);
			if(!realPwd.equals(originalPwd)){
				throw new RuntimeException("原始密码错误");
			}
			if(!newPwd.equals(newConfirmedPwd)){
				throw new RuntimeException("两次密码输入不一致");
			}
			if(originalPwd.equals(newPwd)){
				throw new RuntimeException("新密码不能和原密码相同");
			}
			pwdBO.setUserId(userId);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			userService.modifyPassword(pwdBO);//修改用户密码
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
}
