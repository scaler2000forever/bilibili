package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.auth.UserAuthorities;
import com.atlinlin.bilibili.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 16:27
 */
@RestController
public class UserAuthController {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;


    /**
     *用户权限查询 通过用户id查询 用户角色权限 传回给前端
     * @return
     */
    //两个权限 页面权限和操作权限 前端和后台联动的权限控制 主要在于前端
    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities(){
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }



}
