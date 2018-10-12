package com.fuzamei.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Result<T> {
	public String code;
	public String  message;
	public PlatformToken platformToken;
	public Back  data;
	public boolean  udun;


	
	

}
