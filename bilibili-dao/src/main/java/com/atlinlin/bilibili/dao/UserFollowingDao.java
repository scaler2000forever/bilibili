package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-15 23:58
 */
@Mapper
public interface UserFollowingDao {
    //多个参数使用param指定键
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);
}
