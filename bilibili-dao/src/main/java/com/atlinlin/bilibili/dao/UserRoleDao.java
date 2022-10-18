package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 18:18
 */
@Mapper
public interface UserRoleDao {

    List<UserRole> getUserRoleByUserId(Long userId);
}
