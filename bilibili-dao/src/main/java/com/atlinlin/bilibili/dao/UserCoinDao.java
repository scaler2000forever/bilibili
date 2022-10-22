package com.atlinlin.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @ author : LiLin
 * @ create : 2022-10-22 10:11
 */
@Mapper
public interface UserCoinDao {

    Integer getUserCoinAmount(Long userId);

    Integer updateUserCoinsAmount(@Param("userId") Long userId, @Param("amount")Integer amount, @Param("updateTime")Date updateTime);
}
