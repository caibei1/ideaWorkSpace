package com.fuzamei.pojo.vo;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class IdCardVO {
	private String code;
	private String message;
	private Map<String, Object> platformtoke;
	private Map<String, Object> data;
}
