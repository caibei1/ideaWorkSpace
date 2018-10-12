package com.fuzamei.pojo.po;

import java.util.List;

import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class FinanceBankPO extends AccountPO{//出入金 审核交易流水记录表
	private Integer  id;                       //主键id
	private String   moneyFlowNo;              //交易编号
	private Long     moneyFlowNos;              //交易编号   只给sdy插入用的字段
	private String   bankTransactionNo;        //银行交易流水编号
	private Double   amount;				   //出金，入金的金额
	private String   bankCard;                 //银行卡号
	private Long     createTime;               //创建时间
	private String   createBy;                 //创建人（存法人名称）
	private Long     firstTime;                //初审完成时间
	private String   bankName;                 //银行名称                 
	private Integer  transferType;             //转账类型？？？？？
	private Long     secondTime;               //复审完成时间
	private Integer  enterpriseId;             //企业id
	private String   enterpriseName;           //企业名称
	private Integer  operationType;            //操作类型    1代表入金 2代表出金
	private Integer  manuslAutomatic;          //1代表人工 or 2代表自动
	private Integer  firstCheckBy;             //初审的人
	private Integer  secondCheckBy;            //复审的人
	private String   cashFlowHash;             //交易流水hash
	private String   memorandum;               //备注信息(新加的)
	private String   transferEnterprise;       //划拨企业(新加的)
	private Long     endTime;                  //完成时间
	private Integer  uid;                      //复审人得操作uid
	private Integer  state;                    //状态  1.待审核 2.入金通过 3.入金拒绝 4.黑账
                                               //  5.初审通过 6.初审拒绝 7.复审通过 8.复审拒绝 9.出账成功 10.出账失败11.待划拨 12.已退款 13.已划拨
	private Integer  type;
	private Integer  status;                                              
	private  Integer userId;//
	private  String legalPersonName;           //法人姓名
	private  String stateName;                 //状态名称
	private  String chuShenName;               //用户名字(初审的人)
	private  String fuShenName;                //用户名字(复审的人)
	private  String bankCardId;
	
	private UserDetailDTO userDetailDTO;       //用户详细信息
	private Integer  flag;                     //标识性 0代表上链成功 ，1是上链失败
	private String   sign;                     //签名
	//////
	//以下lys
	private String  tranflow;    //流水编号
	private String  accno;       //银行卡号
	private String  accnoname ;  //大账户银行名称
    private String 	serialnumber;//请求id
    private List<String> serialNumbers;
    private List<String> serialNumbers2;
    private String  requestsn ;  //银行返回过来的状态码2.3.4或9处理中
    private String  useof;      //备注
    private String  money;      //金额
    private String  pevvaccname;//账号名称
    private String pecvopenaccdept;//开户机构
	
}
