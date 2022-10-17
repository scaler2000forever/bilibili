package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.FollowingGroupDao;
import com.atlinlin.bilibili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-16 0:00
 */

/**
 * 用户分组 其中功能是用户关注的前提条件
 */
@Service
public class FollowingGroupService {

    @Autowired
    private FollowingGroupDao followingGroupDao;

    /**
     * 获取用户关注类型
     * @param type
     * @return
     */
    public FollowingGroup getByType(String type){
        return followingGroupDao.getByType(type);
    }

    /**
     * 获取用户主键id
     * @param id
     * @return
     */
    public FollowingGroup getById(Long id){
        return followingGroupDao.getById(id);
    }


    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }


    public void addFollowingGroup(FollowingGroup followingGroup) {
        followingGroupDao.addFollowingGroup(followingGroup);
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupDao.getUserFollowingGroups(userId);
    }
}
