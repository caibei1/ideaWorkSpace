package com.fuzamei.web;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.fuzamei.baseTest.BaseJunit4Test;
import com.fuzamei.mapper.UserMapper;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.service.UserService;

public class GeneralTest extends BaseJunit4Test{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserMapper userMapper;
	
	@Test
	public void test01(){
		UserDetailDTO user = userService.queryUserByNameAndPwd("ylx978", "123456");
		System.out.println(JSON.toJSONString(user, true));
	}
	
	@Test
	public void test02(){
		System.out.println(userMapper.verificationToken(1, "abcdefg"));
	}
	
	
}
