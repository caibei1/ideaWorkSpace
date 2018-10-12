package com.fuzamei.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fuzamei.constant.Auth;
import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.Authes;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.HintMSG;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.constant.Roles;
import com.fuzamei.pojo.bo.DownLoadBO;
import com.fuzamei.pojo.bo.IdCardBO;
import com.fuzamei.pojo.dto.EnterpriseDTO;
import com.fuzamei.pojo.po.AttachmentPO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.vo.IdCardVO;
import com.fuzamei.service.AttachmentService;
import com.fuzamei.service.UserManagementService;
import com.fuzamei.service.UserService;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.GetJsonData;
import com.fuzamei.util.HttpRequest;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;

@RestController
@RequestMapping(value="/userManagement")
public class UserManagementAction {
	@Autowired
	private HttpServletRequest req;
	@Autowired
	private UserService userService;
	@Autowired
	private UserManagementService userManagementService;
	@Autowired
	private AttachmentService attachmentService;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementAction.class);
	//public static String URL = "http://192.168.33.140:8080/FaceRecognition/rawveify/getIdentityjsonImg";
	/**
	 * (市场人员查看用户管理列表)
	 * @param enterpriseDTO
	 * @return
	  {
	 	"startTime":"",
	 	"endTime":"",
	 	"enterpriseName":"",
	 	"isVerification":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/queryUserManagement",method=RequestMethod.POST)
	public ResultResp queryUserManagement(@RequestBody EnterpriseDTO enterpriseDTO) {
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_USERMANAGE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkNullAndAssignString(enterpriseDTO.getEnterpriseName());
			int page = ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getPage(),1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(enterpriseDTO.getRowNum(),1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(enterpriseDTO.getStartTime(),0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(enterpriseDTO.getEndTime(),Long.MAX_VALUE);
			enterpriseDTO.setStartTime(startTime);
			if(startTime > endTime) {
				enterpriseDTO.setEndTime(Long.MAX_VALUE);
			} 
			enterpriseDTO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO=userManagementService.queryUserManagement(enterpriseDTO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	}
	
	/**
	 * 查询所有状态（1通过，2未通过）给前端展示
	 * @return
	 */
	@RequestMapping(value="/queryAllStatus",method=RequestMethod.POST)
	public ResultResp queryAllStatus() {
		try {
			Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_STATE.getAuthUrl());
			List<EnterprisePO> status = userManagementService.queryAllStatus();
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), status);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}

	}

	/**
	 * @Title queryIdentityCard
	 * @Description: TODO(查询账户信息)
	 * @author ylx
	 * @date 2018年3月7日 15:48
	 * @return ResultResp返回类型
	 {
	 	"type":1,
	 	"platformtoken":""
	 }
	 */
	@RequestMapping(value="/queryIdentityCard",method=RequestMethod.POST)
	public ResultResp queryIdentityCard(@RequestBody IdCardBO idCardBO) {
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_IDCARD.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkRangeAndAssignInt(idCardBO.getType(), 1, 2);//type只能是1：正面和2：反面
			ValidationUtil.checkBlankAndAssignString(idCardBO.getPlatformtoken());//token值
			idCardBO.setTag("1");//1表示图片
		} catch (Exception e) {
			LOGGER.error("用户:{},详情{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("face_interface"), JSON.toJSONString(idCardBO));
			String convertedResult = result.replaceAll("\n", "");
			IdCardVO idCardVO = JSON.parseObject(convertedResult, IdCardVO.class);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), idCardVO);
		} catch (Exception e) {
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
/*	public static void main(String[] args) throws Exception {
		IdCardBO idCardBO = new IdCardBO();
		idCardBO.setTag("1");
		idCardBO.setType("2");
		idCardBO.setPlatformtoken("000aa5e3-014c-42f3-9f96-06611911ebf5");
		String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("face_interface"), JSON.toJSONString(idCardBO));
//		IdCardVO idCardVO = JSON.parseObject(result, IdCardVO.class);
		Map<String,Object> parseObject = JSON.parseObject(result,Map.class);
		Map<String,Object> map = (Map<String,Object>)parseObject.get("data");
		String str = ((String) map.get("Base64"));
//		System.out.println(str.replaceAll("\r\n", ""));
		byte[] decode = Base64.getDecoder().decode(str.getBytes());
		FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\fuzamei\\Desktop\\xxx.jpg"));
		fos.write(decode);
		fos.close();
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 {
	 	"attachmentId":"",
	 	"attachmentUrl":""
	 }
	 * 
	 * 《（下载）审核资料(身份证照片) 》
	 * @return
	 */
	/*@RequestMapping(value="/downloadCheckData",method=RequestMethod.POST)
	private ResultResp downloadCheckData(@RequestBody DownLoadBO downLoadBO, HttpServletResponse response) {
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Auth.DOWNLOAD_CARD_PICTURE);
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkBlankString(downLoadBO.getAttachmentUrl());//url校验
			ValidationUtil.checkMinOfInt(downLoadBO.getAttachmentId(), 0);//附件id校验
		} catch (Exception e) {
			LOGGER.error("用户:{}-->参数校验失败",userId);
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		String filePath = downLoadBO.getAttachmentUrl();
		//如果文件不存在直接抛出异常
		if(!new File(filePath).exists()){
			LOGGER.error("用户:{}-->文件未找到",userId);
			return ResultResp.getResult(HintEnum.FILE_NOT_FOUND.getCode(), false, HintEnum.FILE_NOT_FOUND.getHintMsg(), null);
		}
		try {
			AttachmentPO attachmentPO = attachmentService.queryAttachmentInfomation(downLoadBO);//根据url和附件id号去核实是否存在该附件
			if(attachmentPO==null){//信息查询不到报异常
				throw new NullPointerException("该文件不存在");
			}
			String filename = attachmentPO.getAttachemntName();//获取附件名称
			InputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
			filename = URLEncoder.encode(filename, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + filename);
			response.setContentType("multipart/form-data");
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			for (int len = 0; (len = bis.read()) != -1;) {
				out.write(len);
			}
			out.close();
			bis.close();
			return ResultResp.getResult(HintEnum.DOWNLOAD_SUCCESS.getCode(), true, HintEnum.DOWNLOAD_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{}-->附件下载失败",userId);
			return ResultResp.getResult(HintEnum.DOWNLOAD_FAIL.getCode(), false, HintEnum.DOWNLOAD_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		
	
	}*/
	
	
	
	
	
	
	
	
	
	
}
