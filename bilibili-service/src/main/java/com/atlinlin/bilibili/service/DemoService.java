package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @ author : LiLin
 * @ create : 2022-10-14 19:41
 */

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public Long query(Long id){
        return demoDao.query(id);
    }



}