package com.fuzamei.constant;

import lombok.Getter;

@Getter
public enum StateEnum {
	BUY_BACK(60,"已回购"),
	APPLY_IMPAWN(55,"申请质押中"),
	IMPAWNING(56,"质押中"),
	IMPAWN_EXPIRE(74,"质押到期"),
	IMPAWN_OVERDUE(75,"质押逾期");
	
	private Integer status;
	private String statusName;
	private StateEnum(Integer status,String statusName){
		this.status=status;
		this.statusName=statusName;
	}
}
