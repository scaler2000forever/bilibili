package com.atlinlin.bilibili.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Tag {
    private Long id;
    private String name;
    private Date createTime;
}
