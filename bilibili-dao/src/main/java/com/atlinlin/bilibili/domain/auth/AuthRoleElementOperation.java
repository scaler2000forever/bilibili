package com.atlinlin.bilibili.domain.auth;

import lombok.Data;

import java.util.Date;

@Data
public class AuthRoleElementOperation {

    private Long id;

    private Long roleId;

    private Long elementOperationId;

    private Date createTime;

    //保存的是Id关联，使用连表方式关联查询 一次数据库请求把需要的数据都查出 还可少些业务逻辑代码
    // 最多使用两张表 否则数据库压力大
    private AuthElementOperation authElementOperation;
}
