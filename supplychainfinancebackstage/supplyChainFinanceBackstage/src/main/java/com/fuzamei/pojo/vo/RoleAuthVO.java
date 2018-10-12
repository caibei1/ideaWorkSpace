package com.fuzamei.pojo.vo;

import java.util.List;

import com.fuzamei.pojo.dto.BackAuthDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RoleAuthVO {
	private Integer roleId;//角色id
	private List<BackAuthDTO> authList;
}
