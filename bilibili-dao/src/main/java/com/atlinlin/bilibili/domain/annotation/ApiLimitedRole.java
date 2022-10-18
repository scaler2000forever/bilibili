package com.atlinlin.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 22:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimitedRole {

    /**
     * 限制的角色编码 升级方式可以使用权限组
     * @return
     */
    String[] limitedRoleCodeList() default {};
}
