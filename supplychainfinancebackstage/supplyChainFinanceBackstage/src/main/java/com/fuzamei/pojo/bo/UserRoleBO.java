package com.fuzamei.pojo.bo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.fuzamei.pojo.BasePojo;
import com.fuzamei.pojo.dto.UserDetailDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author ylx
 * @descri 用户账号角色关联表对应的实体类
 */
@Setter
@Getter
@ToString
public class UserRoleBO extends BasePojo{
	private Integer userId;
	private List<Integer> roleIds;
	private UserDetailDTO userDetailDTO;
}
