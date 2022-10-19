package com.atlinlin.bilibili.service;

import ch.qos.logback.core.joran.conditional.ThenOrElseActionBase;
import com.alibaba.fastjson.JSONObject;
import com.atlinlin.bilibili.dao.UserDao;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.PageResult;
import com.atlinlin.bilibili.domain.User;
import com.atlinlin.bilibili.domain.UserInfo;
import com.atlinlin.bilibili.domain.constant.UserConstant;
import com.atlinlin.bilibili.domain.exception.ConditionException;
import com.atlinlin.bilibili.service.util.MD5Util;
import com.atlinlin.bilibili.service.util.RSAUtil;
import com.atlinlin.bilibili.service.util.TokenUtil;
import com.mysql.cj.Constants;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @ author : LiLin
 * @ create : 2022-10-15 12:33
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    //用户注册
    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已经注册!");
        }
        //注册逻辑
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword ;
        try {
             rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        //添加用户信息 在xml配置可以获取主键id
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK); //不要写死
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
        //进行用户等级权限的添加
        //提升点 这里引用userAuthService而不是userRoleService 或authRoleService
        //比如这里引用userRoleService userRoleService引用userService 会导致循环依赖问题
        userAuthService.addUserDefaultRole(user.getId());
    }

    //获取用户手机号
    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }


    //登录
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        //md5密码加密，用数据库里的salt
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误！");
        }
        //加一个工具类，生成token返回给前端
        return TokenUtil.generateToken(dbUser.getId());
    }


    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /**
     * 用户修改
     * @param user
     * @return
     */
    public void updateUsers(User user) throws Exception {
        //获取该用户
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if(dbUser==null){
            throw new ConditionException("用户不存在");
        }
        //修改密码
        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        //修改时间
        user.setUpdateTime(new Date());
        //操作dao修改用户
        userDao.updateUsers(user);
    }

    /**
     * 用户详细表修改
     * @param userInfo
     */
    public void updateUserInfos(UserInfo userInfo) {
        //这里不用像上面一样进行判断，这里是判断后的第二步
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start" ,(no -1) * size);
        params.put("limit",size);
        Integer total = userDao.pageCountUserInfos(params);
        //建一个空的list列表 这里是真正的进行分页列表
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(total,list);
    }
}
