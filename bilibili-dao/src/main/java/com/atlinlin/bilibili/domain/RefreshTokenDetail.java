package com.atlinlin.bilibili.domain;

import lombok.Data;

import java.util.Date;

@Data
public class RefreshTokenDetail {
    private Long id;
    private Long userId;
    private String refreshToken;
    private Date createTime;
}
