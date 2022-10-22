package com.atlinlin.bilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * @ author : LiLin
 * @ create : 2022-10-22 1:01
 */
@Data
public class UserCoin {
    private Long id;
    private Long userId;
    private Integer amount;
    private Date createTime;
    private Date updateTime;
}
