package com.fuzamei.pojo.po;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BackRoleAuthPO {
	private Integer roleId;
	private Integer authorityId;
	private List<Integer> authorityIds;
}
