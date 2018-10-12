package com.fuzamei.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AdminDTO {
	private Integer userId;//用户id
	private String username;//用户名
	private String password;//密码
	private Integer parentId;//上级用户id
	private Long createTime;//创建时间
	private Long updateTime;//更新时间
}
