package com.atlinlin.bilibili.dao;

import com.atlinlin.bilibili.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    Video getVideoById(Long id);

    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoLike(VideoLike videoLike);

    Integer deleteVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Long getVideoLikes(Long videoId);

    Integer deleteVideoCollection(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Long getVideoCollections(Long videoId);

    VideoLike getVideoCollectionsByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer addVideoCoin(VideoCoin videoCoin);

    Integer updateVideoCoin(VideoCoin videoCoin);

    Long getVideoCoinsAmount(Long videoId);

    Integer addVideoComment(VideoComment videoComment);

    Integer pageCountVideoComments(HashMap<String, Object> params);

    List<VideoComment> pageListVideoComments(HashMap<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(List<Long> parentIdList);

    Video getVideoDetails(Long id);
}
