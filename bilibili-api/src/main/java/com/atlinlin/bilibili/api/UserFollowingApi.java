package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.FollowingGroup;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.UserFollowing;
import com.atlinlin.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-17 16:07
 */
@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    //获取当前登录用户的信息
    @Autowired
    private UserSupport userSupport;

    /**
     * 添加关注的用户
     * @param userFollowing 用户
     * @return
     */
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFollowing){
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 获取关注列表
     * @return 参数从tokens中获取
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> results = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(results);
    }

    /**
     * 获取用户粉丝
     * @return
     */
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans(){
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> userFansLists = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(userFansLists);
    }

    /**
     * 添加用户关注分组 返回值类型用Long 新建完之后将分组id传回给前端
     * @param followingGroup 添加嘛 需要传数据给后台
     * @return
     */
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroups(@RequestBody FollowingGroup followingGroup){
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return new JsonResponse<>(groupId);
    }

    /**
     *获取用户关注分组
     * 这里不用参数的原因是userID在token中获取，不在路径中传递
     * @return
     */
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list = userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(list);
    }
    //分页查询用户列表
    



}
