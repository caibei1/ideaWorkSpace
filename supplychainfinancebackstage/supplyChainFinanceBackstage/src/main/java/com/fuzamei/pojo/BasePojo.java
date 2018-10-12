package com.fuzamei.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BasePojo {
	private Long createTime;//创建时间
	private Long updateTime;//更新时间
	private Integer page;//页数
	private Integer startPage;//起始页
	private Integer rowNum;//每页显示条数
}
