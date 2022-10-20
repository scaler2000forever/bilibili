package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;


/**
 * @ author : LiLin
 * @ create : 2022-10-20 21:16
 */
@Mapper
public interface FileDao {

    File getFileByMd5(String fileMD5);

    Integer addFile(File dbFileMD5);
}
