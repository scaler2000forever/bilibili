package com.atlinlin.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @ author : LiLin
 * @ create : 2022-10-18 22:30
 */

/**
 * 接口切点对应一个切面实现
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface DataLimited {


}
