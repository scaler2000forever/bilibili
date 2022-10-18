package com.atlinlin.bilibili.domain.auth;

import lombok.Data;

import java.util.Date;

@Data
public class AuthRoleMenu {
    private Long id;

    private Long roleId;

    private Long menuId;

    private Date createTime;

    //关联的实体类里一样使用连表的方式
    private AuthMenu authMenu;
}
