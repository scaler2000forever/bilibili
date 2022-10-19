package com.atlinlin.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.atlinlin.bilibili.domain.RefreshTokenDetail;
import com.atlinlin.bilibili.domain.User;
import com.atlinlin.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;


/**
 * @ author : LiLin
 * @ create : 2022-10-15 12:35
 */
@Mapper
public interface UserDao {


    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long id);

    //数据库返回的是修改个数
    Integer updateUsers(User user);

    Integer updateUserInfos(UserInfo userInfo);

    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    Integer pageCountUserInfos(Map<String,Object> params);

    List<UserInfo> pageListUserInfos(Map<String, Object> params);

    Integer deleteRefreshToken(@Param("refreshToken") String refreshToken,@Param("userId") Long userId);
                                                                                                        //这里字段注意，前面是创建时间
    Integer addRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId, @Param("createTime") Date createTime);

    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);
}
