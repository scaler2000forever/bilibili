package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ author : LiLin
 * @ create : 2022-10-19 12:28
 */
@Mapper
public interface AuthRoleDao {

    AuthRole getRoleByCode(String code);
}
