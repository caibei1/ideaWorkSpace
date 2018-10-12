package com.fuzamei.pojo.bo;

import java.util.List;

import com.fuzamei.pojo.dto.BackAttachmentDTO;
import com.fuzamei.pojo.dto.BillDTO;
import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author 查询常用的实体参数类
 *
 */
@Setter
@Getter
@ToString
public class BillBO {
	private Integer billId;//应收账款id号
	private Integer page;//初始查询页
	private Integer rowNum;//每页显示的行数
	private Integer startPage;//初始页
	private Long startTime;//起始时间
	private Long endTime;//结束时间
	private String enterpriseName;//企业名称
	private Integer enterpriseId;//企业id
	private Integer statusId;//状态id，0表示待审核，1表示已通过，2表示未通过
	private Integer version;//版本号
	
	private Double approveMoney;//审定额度(审核单据同意的时候从前端传过来的)
	
	private Long currentTime;//当前时间
	
	private BillDTO billDTO;//用于当bill审核通过时，将bill中的信息存在里面，并传到service层进行插入
	
	private Integer accountId;//账户id
	
	private UserDetailDTO userDetailDTO;
	private List<BackAttachmentDTO> attachmentList;
	
	private List<Integer> billIds;//分页查询的时候用的billIds
}
