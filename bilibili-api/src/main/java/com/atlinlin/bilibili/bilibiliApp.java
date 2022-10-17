package com.atlinlin.bilibili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ author : LiLin
 * @ create : 2022-10-14 19:06
 */
@EnableTransactionManagement
@SpringBootApplication
public class bilibiliApp {
    public static void main(String[] args) {
        SpringApplication.run(bilibiliApp.class,args);
    }
}
