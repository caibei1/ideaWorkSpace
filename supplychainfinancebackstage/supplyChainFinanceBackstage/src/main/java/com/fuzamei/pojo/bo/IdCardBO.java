package com.fuzamei.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdCardBO {
	private String platformtoken;//查询身份证图片token
	private String tag;//1表示图片，2表示视频
	private String type;//1是正面，2是反面
}
