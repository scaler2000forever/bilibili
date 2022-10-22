package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ author : LiLin
 * @ create : 2022-10-22 0:47
 */
@Service
public class UserCoinService {

    @Autowired
    private UserCoinDao userCoinDao;


    public Integer getUserCoinAmount(Long userId) {
        return userCoinDao.getUserCoinAmount(userId);
    }

    public void updateUserCoinsAmount(Long userId, Integer amount) {
        Date updateTime = new Date();
        userCoinDao.updateUserCoinsAmount(userId,amount,updateTime);
    }
}
