package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.UserFollowingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ author : LiLin
 * @ create : 2022-10-15 23:59
 */
@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;
}
