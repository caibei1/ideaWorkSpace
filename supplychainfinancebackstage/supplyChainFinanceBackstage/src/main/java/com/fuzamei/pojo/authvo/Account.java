package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class Account {
	private String name = "account";
	private boolean ifHave;
	private Set<String> authes;
}
