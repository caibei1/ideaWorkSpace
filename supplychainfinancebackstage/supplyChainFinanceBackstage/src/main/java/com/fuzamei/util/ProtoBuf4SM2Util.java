package com.fuzamei.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

import com.fuzamei.pojo.protobuf.ProtoBufBean;
import com.fuzamei.util.blockChain.HexUtil;
import com.fuzamei.util.blockChain.ProtobufBean;
import com.fuzamei.util.blockChain.sm2.SM2Utils;
import com.fuzamei.util.blockChain.sm2.Util;
import com.google.protobuf.ByteString;

import fzmsupply.Api;
import fzmsupply.Api.CashRecord;
/**
 * 
 * @author ylx
 * @descri 针对这个项目的合约创建的签名用的工具类
 */
public class ProtoBuf4SM2Util {
	private ProtoBuf4SM2Util(){
		throw new AssertionError("instantiation is not permitted");
	}
	private static final String VERSION = "v1";
	// TODO 超级管理员私钥
	private final static String sysAdminPrivateKey = ConfReadUtil.getProperty("superAdminPrivateKeySM2");
	// TODO 超级管理员公钥
	private final static String sysAdminPublicKey = ConfReadUtil.getProperty("superadminPubKeySM2");

	/**
	 * @author ylx
	 * 整合参数，将签名后的ProtoBufBean整合好并返回
	 * @param operatorPriKey
	 * @param request
	 * @param requestBuilder
	 * @param instructionId
	 * @return
	 */
	private static final ProtoBufBean getProtoBufBean(String operatorPriKey,Api.Request.Builder request,Api.Request requestBuilder,long instructionId){
		//用操作者的私钥签名
        byte[] bytePrivateKey = Util.hexToByte(operatorPriKey);//操作者的私钥
        String requestBuilderString = Util.encodeHexString(requestBuilder.toByteArray());
        byte[] sourceData = requestBuilderString.getBytes();//将签名数据转换成二进制数组
        byte[] signBytes =SM2Utils.sign(bytePrivateKey, sourceData);
        ByteString signByteString =  ByteString.copyFrom(signBytes);//签名后得到的byteString数据
        //签名后的结果也给request
        request.setSign(signByteString);
        //将携带着签名后的参数信息通过http给合约，在callback里解析
        Api.Request finalRequest = request.build();
        byte[] requestByte = finalRequest.toByteArray();
        String signdateBase64 = Base64.getEncoder().encodeToString(requestByte);//最终的签名
		return ProtoBufBean.getInstance(instructionId, signdateBase64);
	}
	
	/**
	 * @author ylx
	 * 初始化平台
	 * @param operatorPubKey	平台最大的公钥
	 * @param operatorPriKey	平台最大的私钥
	 * @param platformKey	超级管理员公钥
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean initPlatform(String operatorPubKey,String operatorPriKey,String platformKey){
//		String publikey = ConfReadUtil.getProperty("publicKeySM2");//平台的公钥
//		String platformKey = ConfReadUtil.getProperty("privateKeySM2");//平台私钥--最大的
//		String superAdminPubKey = ConfReadUtil.getProperty("superadminPubKeySM2");//超级管理员的公钥
		Api.RequestInitPlatform.Builder builder = Api.RequestInitPlatform.newBuilder();
		builder.setPlatformKey(ByteString.copyFrom(HexUtil.hexString2Bytes(platformKey)));//平台初始化创建超级管理员(用超级管理员的公钥)
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
		long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setInitPlatform(builder);
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgInitPlatform);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));//操作人的公钥(这里是平台的)
        Api.Request requestBuilder = request.build();
		return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 创建后台角色
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param roleId	角色id
	 * @param roleName	 角色名称
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean createBackRole(String operatorId,String operatorPubKey,String operatorPriKey,String roleId,String roleName){
		Api.RequestCreateRole.Builder builder =  Api.RequestCreateRole.newBuilder();
		builder.setRoleid(roleId);
		builder.setRoleName(roleName);
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setCreateRole(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgCreateRole);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 修改后台角色
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param roleId	角色id
	 * @param roleName	角色名称
	 * @param rightType[]	权限类型数组
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean updateBackRole(String operatorId,String operatorPubKey,String operatorPriKey,String roleId,String roleName,Api.RightType... rightType){
		Api.RequestUpdateRole.Builder builder = Api.RequestUpdateRole.newBuilder();
		builder.setRoleid(roleId);
		if(rightType!=null && rightType.length!=0){//如果没有权限信息，区块链直接不传这个参数
			for (Api.RightType right : rightType) {//数组的插入形式
				builder.addRights(right);
			}
		}
		builder.setRoleName(roleName);
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setUpdateRole(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgUpdateRole);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 删除后台角色
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param roleIds	角色id(支持批量删除)
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean deleteBackRole(String operatorId,String operatorPubKey,String operatorPriKey,String... roleIds){
		//设置参数
		Api.RequestDeleteRole.Builder builder = Api.RequestDeleteRole.newBuilder();
		if(roleIds!=null && roleIds.length!=0){//如果传空，区块链那边不用传任何参数
			for (String roleId : roleIds) {
				builder.addRoleids(roleId);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setDeleteRole(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgDeleteRole);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 创建后台人员
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param uid	后台人员id
	 * @param officialPubKey	后台人员的公钥
	 * @param name	后台人员的人名
	 * @param certificate 后台人员的身份证号
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean createOfficial(String operatorId,String operatorPubKey,String operatorPriKey,String uid,String officialPubKey,String name,String certificate){
		//设置参数
		Api.RequestCreateOfficial.Builder builder = Api.RequestCreateOfficial.newBuilder();
		builder.setUid(uid);
		builder.setPubkey(ByteString.copyFrom(Util.hexStringToBytes(officialPubKey)));
		builder.setName(name);
		builder.setCertificate(certificate);
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
		//request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setCreateOfficial(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgCreateOfficial);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}

	/**
	 * @author ylx
	 * 修改后台人员
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param uid	用户id
	 * @param roleIds[]	角色id号
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean updateOfficial(String operatorId,String operatorPubKey,String operatorPriKey,String uid,String...roleIds){
		//设置参数
		Api.RequestUpdateOfficial.Builder builder = Api.RequestUpdateOfficial.newBuilder();
		builder.setUid(uid);
		if(roleIds!=null && roleIds.length!=0){//如果没有任何信息，则不给区块链传这个参数
			for (String roleId : roleIds) {
				builder.addRoleids(roleId);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setUpdateOfficial(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgUpdateOfficial);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 删除后台人员
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param uids	后台人员id号（支持批量删除账号）
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean deleteOfficial(String operatorId, String operatorPubKey,String operatorPriKey,String... uids){
		Api.RequestDeleteOfficial.Builder builder = Api.RequestDeleteOfficial.newBuilder();
		if(uids!=null && uids.length!=0){//如果没有选任何信息，不给区块链传任何参数
			for (String uid : uids) {
				builder.addUids(uid);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setDeleteOfficial(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgDeleteOfficial);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 资产审核录入(收款企业录入):给单据同意的时候开审定额度
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param assetId 	单据id---bill_id
	 * @param agree 是否同意 true表示同意，false表示拒绝
	 * @param validCredit	审定额度*100,转成long
	 * @param docHash	文件hash
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean examineAsset(String operatorId,String operatorPubKey,String operatorPriKey,String assetId,boolean agree,Long validCredit,String docHash){
		//设置参数
		Api.RequestExamineAsset.Builder builder = Api.RequestExamineAsset.newBuilder();
		builder.setAgree(agree);
		builder.setAssetId(assetId);
		if(agree) builder.setValidCredit(validCredit);//只有同意才会设置这些参数
		if(agree) builder.setDocHash(docHash);//只有同意才会设置这些参数
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setExamineAsset(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgExamineAsset);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 设置企业信用总额度
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param userUid	企业id
	 * @param totalAmount	设定的额度，要乘以100变成long形
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean SetCredit(String operatorId, String operatorPubKey,String operatorPriKey,String userUid,long totalAmount){
		Api.RequestSetCredit.Builder builder = Api.RequestSetCredit.newBuilder();
		builder.setUserUid(userUid);
		builder.setTotalAmount(totalAmount);
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setSetCredit(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgSetCredit);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 审核企业用户入金
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param userUid	企业id号
	 * @param rmb	审核入金的钱，double类型先乘以100再转换成long形
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean examineDeposit(String operatorId, String operatorPubKey,String operatorPriKey,Api.CashDeposit...cashDeposits){
		Api.RequestExamineDeposit.Builder builder = Api.RequestExamineDeposit.newBuilder();
		if(cashDeposits!=null && cashDeposits.length!=0){
			for (Api.CashDeposit cashDeposit : cashDeposits) {
				builder.addDeposits(cashDeposit);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setExamineDeposit(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgExamineDeposit);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 出纳审核企业用户出金(初审)
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param userUid	企业id
	 * @param rmb	出账金额，double类型先乘以100再转换成long形
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean ExamineWithdraw(String operatorId, String operatorPubKey,String operatorPriKey,Api.CashRecord...cashRecords){
		Api.RequestExamineWithdraw.Builder builder = Api.RequestExamineWithdraw.newBuilder();
		if(cashRecords!=null && cashRecords.length!=0){
			for (Api.CashRecord cashRecord : cashRecords) {
				builder.addWithdraws(cashRecord);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setExamineWithdraw(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgExamineWithdraw);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 财务主管审核或复核企业用户出金(复审)
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param userUid	企业id
	 * @param rmb	出账金额，double类型先乘以100再转换成long形
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean reviewWithdraw(String operatorId, String operatorPubKey,String operatorPriKey,Api.CashRecord...cashRecords){
		Api.RequestReviewWithdraw.Builder builder = Api.RequestReviewWithdraw.newBuilder();
		if(cashRecords!=null && cashRecords.length!=0){
			for (Api.CashRecord cashRecord : cashRecords) {
				builder.addWithdraws(cashRecord);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setReviewWithdraw(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgReviewWithdraw);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	/**
	 * @author ylx
	 * 确认出金结果(超级管理员签名)
	 * @param operatorPubKey	操作人的公钥
	 * @param operatorPriKey	操作人的私钥
	 * @param //确认出金记录数组(成功传true,失败传false)
	 * @return ProtoBufBean
	 */
	public static final ProtoBufBean confirmWithdraw(String operatorId, String operatorPubKey,String operatorPriKey,Api.CashRecord...cashRecords){
		Api.RequestConfirmWithdraw.Builder builder = Api.RequestConfirmWithdraw.newBuilder();
		if(cashRecords!=null && cashRecords.length!=0){
			for (Api.CashRecord cashRecord : cashRecords) {
				builder.addWithdraws(cashRecord);
			}
		}
		//随机数，生成一个操作hash
		SecureRandom secureRandom = new SecureRandom();
        long instructionId = secureRandom.nextLong();//传说中的操作hash值
        //request携带请求信息
        Api.Request.Builder request = Api.Request.newBuilder();
        request.setConfirmWithdraw(builder);
        if(!"1".equals(operatorId)){//超级管理员不用传递id
        	request.setUid(operatorId);//操作者的userId
        }
        request.setVersion(VERSION);
        request.setInstructionId(instructionId);
        request.setActionId(Api.MessageType.MsgConfirmWithdraw);
        request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(operatorPubKey)));
        Api.Request requestBuilder = request.build();
        return getProtoBufBean(operatorPriKey,request,requestBuilder,instructionId);
	}
	
	
	public static void main(String[] args) {
//		Api.CashRecord.Builder builder = Api.CashRecord.newBuilder();
//		builder.setRmb(100000000L);
//		builder.setUid("25458554");
//		CashRecord cashRecord = builder.build();
//		ProtoBufBean protoBufBean = examineDeposit(ConfReadUtil.getProperty("superadminPubKeySM2"), ConfReadUtil.getProperty("superAdminPrivateKeySM2"), cashRecord);
//		ProtoBufBean protoBufBean = createBackRole(ConfReadUtil.getProperty("superadminPubKeySM2"), ConfReadUtil.getProperty("superAdminPrivateKeySM2"), "admin","管理员");
//		ProtoBufBean protoBufBean = SetCredit(ConfReadUtil.getProperty("superadminPubKeySM2"), ConfReadUtil.getProperty("superAdminPrivateKeySM2"), "25458554",100000000L);
//		String sendPost = BlockChainUtil.sendPost(protoBufBean);
//		System.out.println(sendPost);
//		boolean checkResult = BlockChainUtil.checkResult(sendPost);
//		System.out.println(protoBufBean.getSignature());
//		if(!checkResult){
//			String errorMessage = BlockChainUtil.getErrorMessage(sendPost);
//			System.out.println(errorMessage);
//		}else{
//			System.out.println("chenggong");
//		}
		
	}
	/**
	 * @author WangBin
	 * 初始化平台
	 * @param overDue 要签名的信息
	 * @param instructionId hash
	 * @return ProtoBufBean
	 * @throws Exception
	 */
	public static final ProtoBufBean overdue(Set<Integer> id, long instructionId){
		Api.RequestOverdue.Builder builder = Api.RequestOverdue.newBuilder();
		for(int value : id){
			builder.addMorgageIds(String.valueOf(value));
		}
		Api.Request.Builder request = Api.Request.newBuilder();
		request.setOverdue(builder);
		request.setVersion(VERSION);
		request.setInstructionId(instructionId);
		request.setActionId(Api.MessageType.MsgOverdue);
		request.setPubkey(ByteString.copyFrom(HexUtil.hexString2Bytes(sysAdminPublicKey)));
		Api.Request requestBuilder = request.build();
		return getProtoBufBean(sysAdminPrivateKey, request, requestBuilder, instructionId);
	}
}
