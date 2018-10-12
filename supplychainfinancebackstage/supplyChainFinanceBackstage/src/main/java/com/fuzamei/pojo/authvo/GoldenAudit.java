package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoldenAudit {
	private String name = "goldenAudit";
	private boolean ifHave;
	private Set<String> authes;
}
