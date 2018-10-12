package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class Role {
	private String name = "role";
	private boolean ifHave;
	private Set<String> authes; 
}
