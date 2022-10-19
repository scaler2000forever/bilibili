package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.UserMoment;
import com.atlinlin.bilibili.domain.annotation.ApiLimitedRole;
import com.atlinlin.bilibili.domain.annotation.DataLimited;
import com.atlinlin.bilibili.domain.constant.AuthRoleConstant;
import com.atlinlin.bilibili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 10:55
 */
@RestController
public class UserMomentsApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserMomentsService userMomentsService;

    /**
     * 用户发布动态 方法 ：新增
     * @param userMoment 封装好的用户动态
     * @return
     * @throws Exception
     */
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception{
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    /**
     * 获取该用户关注的动态
     * @return
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>>getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list =  userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }
}
