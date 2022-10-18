package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.domain.auth.AuthRoleElementOperation;
import com.atlinlin.bilibili.domain.auth.AuthRoleMenu;
import com.atlinlin.bilibili.domain.auth.UserAuthorities;
import com.atlinlin.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 16:27
 */
@Service
public class UserAuthService {


    //第一步用户绑定哪些角色 之后分别创建相关service
    @Autowired
    private UserRoleService userRoleService;

    //第二步 角色绑定了哪些权限
    @Autowired
    private AuthRoleService authRoleService;

    public UserAuthorities getUserAuthorities(Long userId) {
        //一个用户对应多个关联的角色，所以返回值是一个列表
        //注意这里提升点:因为这里用的是连表所以不能直接查询用户的权限即code,所以需要三张表两次操作
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        //操作权限
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationByRoleIds(roleIdSet);
        //页面菜单权限 方法同上类似，关联查询
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByRoleIds(roleIdSet);
        //返回类型是UserAuthorities 构造一个返回对象
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        userAuthorities.setRoleMenuList(authRoleMenuList);
        return userAuthorities;
    }
}
