package com.atlinlin.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

import javax.websocket.server.PathParam;
import java.util.Map;

/**
 * @ author : LiLin
 * @ create : 2022-10-14 19:32
 */
@Mapper
public interface DemoDao {

    Long query(Long id);
}
