package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.Video;
import com.atlinlin.bilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * @ author : LiLin
 * @ create : 2022-10-21 9:38
 */
@Mapper
public interface VideoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> tagList);

    Integer pageCountVideos(HashMap<String, Object> params);

    List<Video> pageListVideos(HashMap<String, Object> params);
}
