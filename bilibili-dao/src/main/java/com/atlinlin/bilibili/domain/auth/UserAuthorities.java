package com.atlinlin.bilibili.domain.auth;

import lombok.Data;

import java.util.List;

@Data
public class UserAuthorities {
    //操作权限的相关列表
    List<com.atlinlin.bilibili.domain.auth.AuthRoleElementOperation> roleElementOperationList;

    //页面菜单操作权限列表
    List<com.atlinlin.bilibili.domain.auth.AuthRoleMenu> roleMenuList;

}
