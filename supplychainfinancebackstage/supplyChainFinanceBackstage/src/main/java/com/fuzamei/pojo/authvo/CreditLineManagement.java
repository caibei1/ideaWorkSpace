package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreditLineManagement {
	private String name = "creditLineManagement";
	private boolean ifHave;
	private Set<String> authes;
}
