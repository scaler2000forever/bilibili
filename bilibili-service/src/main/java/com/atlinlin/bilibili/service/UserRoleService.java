package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.UserRoleDao;
import com.atlinlin.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 16:50
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }
}
