package com.fuzamei.pojo.authvo;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecordRefundAudit {
	private String name = "recordRefundAudit";
	private boolean ifHave;
	private Set<String> authes;
}
