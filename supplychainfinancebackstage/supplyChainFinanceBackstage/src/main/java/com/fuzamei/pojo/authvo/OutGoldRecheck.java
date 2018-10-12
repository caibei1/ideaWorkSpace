package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OutGoldRecheck {
	private String name = "outGoldRecheck";
	private boolean ifHave;
	private Set<String> authes;
}
