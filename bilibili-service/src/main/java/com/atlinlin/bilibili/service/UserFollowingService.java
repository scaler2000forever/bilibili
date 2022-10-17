package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.UserFollowingDao;
import com.atlinlin.bilibili.domain.FollowingGroup;
import com.atlinlin.bilibili.domain.User;
import com.atlinlin.bilibili.domain.UserFollowing;
import com.atlinlin.bilibili.domain.UserInfo;
import com.atlinlin.bilibili.domain.constant.UserConstant;
import com.atlinlin.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ author : LiLin
 * @ create : 2022-10-15 23:59
 */
@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    /**
     * 新增用户关注
     *
     * @param userFollowing
     */
    @Transactional //对于数据库操作有两个，为了保证数据库一致性，添加事务
    public void addUserFollowings(UserFollowing userFollowing) {
        //分组操作
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在");
            }
        }

        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("关注的用户不存在！");
        }
        //开始新增操作 ,这里这种方法采取的是先删除后添加，省去了修改操作
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);//将分组与用户之间的关系先解绑
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }


    /**
     * 获取用户关注列表
     *
     * @param userId 登录用户Id
     * @return
     */
    public List<FollowingGroup> getUserFollowings(Long userId) {
        //第一步，获取关注的用户列表
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //第二步，根据关注用的id查询关注用户的基本信息
        if (followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        //匹配用户信息 ，两个list整合
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    //注入用户详细信息
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //第三步：将关注用户按关注分组进行分类
        //添加功能，通过用户id将关注用户的分组全部查出来
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        //生成一个全部分组展示，数据库不用存
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        //将groupList和userInfo整合
        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list) {
                if (group.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            //新创建的字段存放的是Userinfo详细信息
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }


    /**
     * 获取关注的粉丝
     * @param userId
     * @return
     */
    public List<UserFollowing> getUserFans(Long userId) {
        //将粉丝id都抽取出来 1.获取当前用户粉丝列表
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (fanIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        //是否被关注的初始化和粉丝的赋值  2.根据粉丝的用户id查询对应的粉丝基本信息
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        //统一定义不关注
        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            //关注的用户和他的粉丝是否匹配 3.查询当前用户是否已经关注该粉丝了
            for (UserFollowing following : followingList) {
                if (following.getFollowingId().equals(fan.getUserId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }

        }
        return fanList;
    }

    /**
     * 添加用户功能
     * @param followingGroup
     * @return
     */
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        //逻辑实现
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        //这里获取插入的主键需要在xml中配置可获取主键
        return followingGroup.getId();
    }

    /**
     * 获取用户关注分组
     * @param userId
     * @return
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);

    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList){
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList){
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
