package com.fuzamei.service.impl;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fuzamei.mapper.EnterpriseMapper;
import com.fuzamei.pojo.po.EnterprisePO;
import com.fuzamei.pojo.vo.EnterpriseVO;
import com.fuzamei.service.EnterpriseService;

@Service
public class EnterpriseServiceImpl implements EnterpriseService{
	@Autowired
	EnterpriseMapper enterpriseMapper;
	/**
	 * -----------------------通过企业名称查企业编号
	 * 
	 * @author fzm_sdy
	 * @param data
	 * @return
	 */
	public EnterpriseVO selectEnterpriseByName(EnterpriseVO enterpriseVO) {
		EnterprisePO PO = new EnterprisePO();
		try {
			PO= enterpriseMapper.selectOneEnterpriseByName(enterpriseVO.getEnterpriseName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		if(PO!=null){
			BeanUtils.copyProperties(PO, enterpriseVO);
		}else{
			return null;
		}
		return enterpriseVO;
	}
}
