package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OutGold {
	private String name = "outGold";
	private boolean ifHave;
	private Set<String> authes;
}
