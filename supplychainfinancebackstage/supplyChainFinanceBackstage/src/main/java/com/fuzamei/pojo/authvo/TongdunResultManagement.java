package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class TongdunResultManagement {
	private String name = "tongdunResultManagement";
	private boolean ifHave;
	private Set<String> authes; 
}
