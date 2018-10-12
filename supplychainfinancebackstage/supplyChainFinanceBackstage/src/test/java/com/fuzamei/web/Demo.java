package com.fuzamei.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Demo {
	@Test
	public void test02(){
		List<String> list = new ArrayList<>();
		for (String string : list) {
			System.out.println("进来了"+string);
		}
		System.out.println("out");
	}
}
