package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.FollowingGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ author : LiLin
 * @ create : 2022-10-16 0:00
 */
@Service
public class FollowingGroupService {

    @Autowired
    private FollowingGroupDao followingGroupDao;


}
