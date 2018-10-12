package com.fuzamei.pojo.bo;

import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ylx
 * @descri 给企业设定授信额度的实体类
 */
@Setter
@Getter
@ToString
public class CreditBO {
	private Integer receivedEnterpriseId;//收账企业id号
	private Double creditLine;//授信额度
	private Long authorizeTime;//授信时间
	private Integer status;//状态id
	
	private Integer page;//页数
	private Integer rowNum;//每页显示条数
	private Integer startPage;//初始页
	private String enterpriseName;//企业名称(查询条件之一)
	
	private UserDetailDTO userDetailDTO;
}
