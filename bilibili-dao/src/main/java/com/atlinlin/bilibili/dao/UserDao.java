package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.User;
import com.atlinlin.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


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
}
