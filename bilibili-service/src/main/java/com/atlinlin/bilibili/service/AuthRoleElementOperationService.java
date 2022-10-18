package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.AuthRoleElementOperationDao;
import com.atlinlin.bilibili.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 18:38
 */
@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;



    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
       return authRoleElementOperationDao.getRoleElementOperationByRoleIds(roleIdSet);
    }
}
