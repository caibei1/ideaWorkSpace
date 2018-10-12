package com.fuzamei.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.Path;
import com.fuzamei.constant.RegexConstant;
import com.fuzamei.constant.Statuses;
import com.fuzamei.pojo.bo.BackAttachmentBO;
import com.fuzamei.pojo.bo.BillBO;
import com.fuzamei.pojo.bo.BillOrderBO;
import com.fuzamei.pojo.bo.CreditBO;
import com.fuzamei.pojo.bo.ResponsibleAttachmentBO;
import com.fuzamei.pojo.bo.TongdunBO;
import com.fuzamei.pojo.dto.BackAttachmentDTO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.tongdun.TongdunResultTD;
import com.fuzamei.service.BackAttachmentService;
import com.fuzamei.service.UserService;
import com.fuzamei.service.VentureManagementService;
import com.fuzamei.util.FileTransferUtil;
import com.fuzamei.util.PageDTO;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;
/**
 * 
 * @author ylx
 * @describe 风险控制模块
 */
@RestController
@RequestMapping(value="/ventureManagement")
public class VentureManagementAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VentureManagementAction.class);

	private static final String BASE_PATH = Path.BASE_PATH;//文件根路径
	
	private static final String RESPONSEBLE_PATH = Path.RESPONSEBLE_PATH;//尽职调查报告的文件夹路径
	
	@Autowired
	private BackAttachmentService backAttachmentService;
	
	@Autowired
	private VentureManagementService ventureManagementService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HttpServletRequest req;
	
	/**
	 * @Title queryToBeVerified
	 * @Description: TODO(查询待审核信息)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 * 时间查询条件以登记时间为准
	 {
	 	"startTime":"",
	 	"endTime":"",
	 	"enterpriseName":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/firstTrial/queryToBeVerified",method=RequestMethod.POST)
	private ResultResp queryToBeVerified(@RequestBody BillBO billBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_UNVERIFIED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId, e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(billBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(billBO.getRowNum(), 1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(billBO.getStartTime(), 0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(billBO.getEndTime(), Long.MAX_VALUE);
			billBO.setStartTime(startTime);
			if(startTime>endTime) billBO.setEndTime(Long.MAX_VALUE);
			else billBO.setEndTime(endTime);
			billBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = ventureManagementService.queryToBeVerified(billBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId, e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title queryLeftoverLoan
	 * @Description: TODO(查询企业剩余额度)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"enterpriseId":""
	 }
	 */
	@RequestMapping(value="/firstTrial/queryLeftoverLoan",method=RequestMethod.POST)
	private ResultResp queryLeftoverLoan(@RequestBody BillBO billBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_LEFTOVERLOAN.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId, e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkAndAssignInt(billBO.getEnterpriseId());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			EnterprisePO enterprisePO = ventureManagementService.queryLeftoverLoan(billBO);
			if(enterprisePO==null){
				throw new RuntimeException("该企业信息不存在");
			}
			Double creditLine = enterprisePO.getCreditLine()==null ? 0d : enterprisePO.getCreditLine();
			Double consumedLoan =  enterprisePO.getConsumedLoan()==null ? 0d :enterprisePO.getConsumedLoan();
			Double leftover = creditLine - consumedLoan;
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), leftover);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId, e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title queryVerified
	 * @Description: TODO(查询已审核信息)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 * 时间查询条件以登记时间为准
	 {
	 	"startTime":"",
	 	"endTime":"",
	 	"enterpriseName":"",
	 	"rowNum":10,
	 	"page":1
	 }
	 */
	@RequestMapping(value="/firstTrial/queryVerified",method=RequestMethod.POST)
	private ResultResp queryVerified(@RequestBody BillBO billBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_UNVERIFIED.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(billBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(billBO.getRowNum(), 1);
			Long startTime = ValidationUtil.checkAndAssignDefaultLong(billBO.getStartTime(), 0L);
			Long endTime = ValidationUtil.checkAndAssignDefaultLong(billBO.getEndTime(), Long.MAX_VALUE);
			billBO.setStartTime(startTime);
			if(startTime>endTime) billBO.setEndTime(Long.MAX_VALUE);
			else billBO.setEndTime(endTime);
			billBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = ventureManagementService.queryVerified(billBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title approveBill
	 * @Description: TODO(同意待审核的应收账)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"billId":"",
	 	"approveMoney":""
	 }
	 */
	@RequestMapping(value="/firstTrial/approveBill",method=RequestMethod.POST)
	private ResultResp approveBill(@RequestBody final BillBO billBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.APPROVE_BILL.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			Integer billId = billBO.getBillId();//单据id号
			if(billId==null){
				throw new RuntimeException("应收账id号不能为空");
			}
			List<BackAttachmentDTO> attachmentList = backAttachmentService.queryDetailAttachments(billBO);//查询billId下详细的附件信息
			billBO.setAttachmentList(attachmentList);//放到service层中处理
			if(attachmentList.size()==0){
				throw new RuntimeException("该单据还未未上传过附件");
			}
			BillDTO billDTO = ventureManagementService.queryBillById(billBO);//查询单据状态信息和单据上关联的企业授信额度信息
			if(billDTO==null){
				throw new RuntimeException("该单据信息不存在");
			}
			if(billDTO.getStatus()!=Statuses.UNVERIFIED){
				throw new RuntimeException("该应收账已经审核过");
			}
			if(billDTO.getApproveMoney()!=null){
				throw new RuntimeException("该应收账已经有额度了");
			}
			Double creditLine = billDTO.getEnterprisePO().getCreditLine();//企业的授信额
			Double approveMoney = billBO.getApproveMoney();//单据的审定额度
			//校验审定额度这个参数
			ValidationUtil.checkRangeAndAssignDouble(approveMoney, 0.00000d, 1000000.00000d,RegexConstant.MONEY);
			if(creditLine == null || creditLine == 0){
				throw new RuntimeException("您还未设置过企业的授信额度");
			}
			if(approveMoney > creditLine){
				throw new RuntimeException("单据的审定额度不能大于企业的授信额度");
			}
			billBO.setEnterpriseId(billDTO.getReceivedEnterpriseId());//将企业id带入BO，方便后面查询所有有效订单总额度信息
			//通过企业id查询有效单据的面值总额(不用排除当前单据的bill_id，因为怕万一单据回购后要重新审核的问题)
			Double totalLoan = ventureManagementService.getBillAvailableTotalAmountById(billBO);
			totalLoan = (totalLoan==null ? 0d : totalLoan);//如果是null,设置为0
			if(totalLoan + approveMoney > creditLine){//已有票据和当前要给的额度的总额不能大于授信额度
				throw new RuntimeException("该企业有效单据总额不能大于企业的授信额度");
			}
			billBO.setBillDTO(billDTO);//将bill中的详细信息带入service层插入到bill_order表中
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			billBO.setUserDetailDTO(userDetailDTO);//获取操作人详细信息，去service层处理，用于上区块链用
			ventureManagementService.approveBill(billBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title rejectBill
	 * @Description: TODO(拒绝待审核的应收账)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"billId":""
	 }
	 */
	@RequestMapping(value="/firstTrial/rejectBill",method=RequestMethod.POST)
	private ResultResp rejectBill(@RequestBody BillBO billBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.REJECT_BILL.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			if(billBO.getBillId()==null){
				throw new RuntimeException("应收账id号不能为空");
			}
			BillDTO billDTO= ventureManagementService.queryBillById(billBO);
			if(billDTO.getStatus()!=Statuses.UNVERIFIED){
				throw new RuntimeException("该应收账已经审核过");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			billBO.setUserDetailDTO(userDetailDTO);//获取操作人详细信息，去service层处理，用于上区块链用
			ventureManagementService.rejectBill(billBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title downloadAttachment
	 * @Description: TODO(下载附件)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"attachmentId":"",
	 	"attachmentUrl":""
	 }
	 */
	/*@RequestMapping(value="/firstTrial/queryVerified")
	private ResultResp downloadAttachment(@RequestBody DownLoadBO downLoadBO, HttpServletResponse response){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Authes.DOWNLOAD_ATTACH);
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
		String filePath = downLoadBO.getAttachmentUrl();//================>>这个路径要和他们确认TODO
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
	
	
	/**
	 * @Title uploadResponsibleReports
	 * @Description: TODO(上传尽调报告)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 * 表单形式上传
	 */
	@RequestMapping(value="/firstTrial/uploadResponsibleReports",method=RequestMethod.POST)
	private ResultResp uploadResponsibleReports(@RequestParam("file") MultipartFile[] files,
												@RequestParam("billId") Integer billId){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.UPLOAD_RESPOSIBLE_REPORTS.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
//			if(files.length>1){
//				throw new RuntimeException("只支持单文件上传");
//			}
			ValidationUtil.checkNullInteger(billId);
			int billCount = ventureManagementService.checkIfHaveBill(billId);//是否存在这个单据
			if(billCount==0){
				throw new RuntimeException("该单据不存在");
			}
			Integer status= ventureManagementService.queryBillStatusById(billId);
			if(status!=Statuses.UNVERIFIED){
				throw new RuntimeException("该应收账已经审核过");
			}
//			int attachmentCount = backAttachmentService.checkIfHaveAttachment(billId);//查询该订单下是否已经存在附件
//			if(attachmentCount>0){
//				throw new RuntimeException("该订单无法再上传附件");
//			}
			files = FileTransferUtil.checkMultiUploadFilesAndSuffixes(files,false,"png","pdf","doc","docx","rar","zip");//校验上传文件格式和文件是否为空
			boolean sameName = FileTransferUtil.checkIfHasSameFileName(files);//检查文件重名
			if(sameName){
				throw new RuntimeException("上传文件名不能重复");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			long currentTime = System.currentTimeMillis();//当前时间
			List<BackAttachmentBO> attachmentList = new ArrayList<BackAttachmentBO>();//放附件的集合信息
			List<File> attachmentFileList = new ArrayList<File>();//放附件的File对象信息(方便后面的全部删除使用)
			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getOriginalFilename();
				String identicalMark = currentTime+String.valueOf(userId)+i;//唯一的标识符：时间+uid+index
				String type = filename.substring(filename.lastIndexOf("."));//文件后缀名
				String url = RESPONSEBLE_PATH+"/"+identicalMark+type;//除去根路径BASE_PATH剩下的部分
				BackAttachmentBO attachmentBO = new BackAttachmentBO();
				attachmentBO.setAttachmentId(identicalMark);//保证id的唯一性
				attachmentBO.setAttachmentName(filename);
				attachmentBO.setAttachmentType(type);//类似".pdf"的样式
				attachmentBO.setAttachmentUrl(url);
				attachmentBO.setCreateTime(currentTime);
				attachmentBO.setUpdateTime(currentTime);
				attachmentBO.setOperatorId(userId);
				attachmentList.add(attachmentBO);
				File destinationFile = new File(BASE_PATH+url);//指定绝对路径
				attachmentFileList.add(destinationFile);
			}
			String directoryPath = BASE_PATH + RESPONSEBLE_PATH;
			ventureManagementService.insertResponsible2Attachment(attachmentList,attachmentFileList,files,userId,billId,directoryPath);//将文件上传，同时将附件信息插入数据库
			return ResultResp.getResult(HintEnum.UPLOAD_SUCCESS.getCode(), true, HintEnum.UPLOAD_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->上传失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.UPLOAD_FAIL.getCode(), false, HintEnum.UPLOAD_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title downloadResponsibleReports
	 * @Description: TODO(下载尽调报告)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 * get post请求都可以
	 {
	 	"responsibleAttachmentId":"",
	 	"responsibleAttachmentUrl":""
	 }
	 */
	@RequestMapping(value="/firstTrial/downloadResponsibleReports",method=RequestMethod.POST)
	private ResultResp downloadResponsibleReports(@RequestBody ResponsibleAttachmentBO responsibleAttachmentBO, HttpServletResponse response){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.DOWNLOAD_RESPOSIBLE_REPORTS.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		BackAttachmentDTO backAttachmentDTO;
		String filePath;
		try {
			String attachmentId = responsibleAttachmentBO.getResponsibleAttachmentId();
			String attachmentUrl = responsibleAttachmentBO.getResponsibleAttachmentUrl();
			ValidationUtil.checkBlankString(attachmentId);//附件id校验
			ValidationUtil.checkBlankString(attachmentUrl);//附件url校验
			filePath = BASE_PATH + attachmentUrl;//要下载附件的绝对路径
			if(!new File(filePath).exists()){//查看文件是否在服务器上
				throw new RuntimeException("文件不存在");
			}
			backAttachmentDTO = backAttachmentService.queryAttachment(attachmentId);
			if(backAttachmentDTO==null){//附件信息是否存在
				throw new RuntimeException("该附件不存在");
			}
			if(!attachmentUrl.equals(backAttachmentDTO.getAttachmentUrl())){//是否请求参数被修改
				throw new RuntimeException("附件信息不一致");
			}
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			String filename = backAttachmentDTO.getAttachmentName();
			String contentType;//根据文件名的后缀名判定给浏览器什么content-type
			if(filename.endsWith("pdf")){
				contentType="application/pdf";
			}else if(filename.endsWith("doc")){
				contentType="application/msword";
			}else if(filename.endsWith("docx")){
				contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			}else{
				contentType="multipart/form-data";
			}
			InputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
			filename = URLEncoder.encode(filename, "UTF-8");
//			response.addHeader("Content-Disposition", "attachment;filename=" + filename);
			response.addHeader("Content-Disposition", filename);
			response.setContentType(contentType);//设置contentType
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			for (int len = 0; (len = bis.read()) != -1;) {
				out.write(len);
			}
			out.close();
			bis.close();
			return ResultResp.getResult(HintEnum.DOWNLOAD_SUCCESS.getCode(), true, HintEnum.DOWNLOAD_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->下载失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.DOWNLOAD_FAIL.getCode(), false, HintEnum.DOWNLOAD_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title queryTongdunResult
	 * @Description: TODO(根据企业id查询同盾结果)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 * get post请求都可以
	 {
	 	"receivedEnterpriseId":""
	 }
	 */
	@RequestMapping(value="/firstTrial/queryTongdunResult",method=RequestMethod.POST)
	private ResultResp queryTongdunResult(@RequestBody TongdunBO tongdunBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_TONGDUN.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkMinAndAssignInt(tongdunBO.getReceivedEnterpriseId(),0);//id号不能小于0
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			TongdunResultTD tongdunResultTD = ventureManagementService.queryTongdunResult(tongdunBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), tongdunResultTD);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	//tongdunResultManagement/同盾结果管理
	/**
	 * @Title queryAllEnterpriseTongdunInfo
	 * @Description: TODO(查询所有企业的同盾管理页面)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"page":"",
	 	"rowNum":"",
	 	"receivedEnterpriseName":""
	 }
	 */
	@RequestMapping(value="/tongdunResultManagement/queryAllEnterpriseTongdunInfo",method=RequestMethod.POST)
	private ResultResp queryAllEnterpriseTongdunInfo(@RequestBody TongdunBO tongdunBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.TONGDUN_MANAGEMENT.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(tongdunBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(tongdunBO.getRowNum(), 1);
			tongdunBO.setStartPage((page-1)*rowNum);
			//如果空直接以null返回
			tongdunBO.setReceivedEnterpriseName(ValidationUtil.checkBlankStringAndAssignNullIfIsBlank(tongdunBO.getReceivedEnterpriseName()));
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = ventureManagementService.queryAllEnterpriseTongdunInfo(tongdunBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	//creditLineManagement/额度管理模块
	/**
	 * @Title setEnterpriseCreditLine
	 * @Description: TODO(设置企业的授信额度)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"receivedEnterpriseId":"",
	 	"creditLine":""
	 }
	 */
	@RequestMapping(value="/creditLineManagement/setEnterpriseCreditLine")
	protected ResultResp setEnterpriseCreditLine(@RequestBody final CreditBO creditBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		UserDetailDTO userDetailDTO;
		try {
			userDetailDTO = userService.checkUserAuthority(userId, true, AuthEnum.SET_CREDITLINE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败", userId, e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			Integer enterpriseId = creditBO.getReceivedEnterpriseId();
			Double creditLine = creditBO.getCreditLine();
			ValidationUtil.checkAndAssignInt(enterpriseId);
			ValidationUtil.checkRangeAndAssignDouble(creditLine, 0.00000d, 1000000.00000d,RegexConstant.MONEY);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			creditBO.setUserDetailDTO(userDetailDTO);
			ventureManagementService.setEnterpriseCreditLine(creditBO);
			return ResultResp.getResult(HintEnum.OPERATION_SUCCESS.getCode(), true, HintEnum.OPERATION_SUCCESS.getHintMsg(), null);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.OPERATION_FAIL.getCode(), false, HintEnum.OPERATION_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	/**
	 * @Title queryEnterpriseCreditLine
	 * @Description: TODO(查询企业的额度信息)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"page":"",
	 	"rowNum":"",
	 	"enterpriseName":""
	 }
	 */
	@RequestMapping(value="/creditLineManagement/queryEnterpriseCreditLine")
	protected ResultResp queryEnterpriseCreditLine(@RequestBody CreditBO creditBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_CREDITLINE.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败", userId, e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(creditBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(creditBO.getRowNum(), 1);
			creditBO.setStartPage((page-1)*rowNum);
			//如果空直接以null返回
			creditBO.setEnterpriseName(ValidationUtil.checkBlankStringAndAssignNullIfIsBlank(creditBO.getEnterpriseName()));
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = ventureManagementService.queryEnterpriseCreditLine(creditBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->操作失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
	
	/**
	 * @Title getUsedMoneyByEnterpriseId
	 * @Description: TODO(查询企业已使用的额度值)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"receivedEnterpriseId":""
	 }
	 */
	/*@RequestMapping(value="/creditLineManagement/getUsedMoneyByEnterpriseId")
	protected ResultResp getUsedMoneyByEnterpriseId(@RequestBody CreditBO creditBO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Auth.GET_USED_MONEY);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败", userId, e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkAndAssignInt(creditBO.getReceivedEnterpriseId());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			Double money = ventureManagementService.getUsedMoneyByEnterpriseId(creditBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), money);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}*/
	
	/**
	 * @Title getTotalReturnedMoneyByEnterpriseId
	 * @Description: TODO(查询企业总还款额度)
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {
	 	"receivedEnterpriseId":""
	 }
	 */
	/*@RequestMapping(value="/creditLineManagement/getTotalReturnedMoneyByEnterpriseId")
	protected ResultResp getTotalReturnedMoneyByEnterpriseId(@RequestBody CreditBO creditBO){
		String userId = req.getHeader("Authorization").split("&")[1];
		try {
			userService.checkUserAuthority(ValidationUtil.checkAndAssignInt(userId), true, Auth.GET_TOTAL_RETURNED_MONEY);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->权限验证失败", userId, e.getMessage());
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			ValidationUtil.checkAndAssignInt(creditBO.getReceivedEnterpriseId());
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			Double money = ventureManagementService.getTotalReturnedMoneyByEnterpriseId(creditBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), money);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->查询失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}*/
	
	
	
	
	//lockupAndFinancingManagement/后台质押融资管理模块
	/**
	 * @Title queryBillOrder
	 * @Description: TODO(查询审核通过的质押单据(状态有：申请质押中55，质押中56，质押到期74，质押逾期75，已回购60))
	 * @author ylx
	 * @date 2018年3月6日 15:48
	 * @return ResultResp返回类型
	 {	
	 	"page":"",
	 	"rowNum":"",
	 	"status":"",
	 	"payedEnterprise":""
	 }
	 */
	@RequestMapping(value="/lockupAndFinancingManagement/queryBillOrder",method=RequestMethod.POST)
	public ResultResp queryBillOrder(@RequestBody BillOrderBO billOrderBO){
		Integer userId = ValidationUtil.checkAndAssignInt(req.getHeader("Authorization").split("&")[1]);
		try {
			userService.checkUserAuthority(userId, true, AuthEnum.QUERY_BILL_ORDER.getAuthUrl());
		} catch (Exception e) {
			LOGGER.error("用户:{}-->权限验证失败",userId);
			return ResultResp.getResult(HintEnum.NO_AUTH.getCode(), false, HintEnum.NO_AUTH.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			int page = ValidationUtil.checkMinAndAssignInt(billOrderBO.getPage(), 1);
			int rowNum=ValidationUtil.checkMinAndAssignInt(billOrderBO.getRowNum(), 1);
			billOrderBO.setStartPage((page-1)*rowNum);
		} catch (Exception e) {
			LOGGER.error("用户:{},详情:{}-->参数校验失败",userId,e.getMessage());
			return ResultResp.getResult(HintEnum.VALI_FAIL.getCode(), false, HintEnum.VALI_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
		try {
			PageDTO pageDTO = ventureManagementService.queryBillOrder(billOrderBO);
			return ResultResp.getResult(HintEnum.QUERY_SUCCESS.getCode(), true, HintEnum.QUERY_SUCCESS.getHintMsg(), pageDTO);
		} catch (Exception e) {
			LOGGER.error("用户:{}-->查询失败",userId);
			return ResultResp.getResult(HintEnum.QUERY_FAIL.getCode(), false, HintEnum.QUERY_FAIL.getHintMsg()+":"+e.getMessage(), null);
		}
	}
	
}
