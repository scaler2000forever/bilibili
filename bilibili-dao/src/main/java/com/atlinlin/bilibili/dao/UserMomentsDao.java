package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 10:58
 */
@Mapper
public interface UserMomentsDao {


    Integer addUserMoments(UserMoment userMoment);
}
