package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.VideoDao;
import com.atlinlin.bilibili.domain.*;
import com.atlinlin.bilibili.domain.exception.ConditionException;
import com.atlinlin.bilibili.service.util.FastDFSUtil;
import org.apache.commons.validator.routines.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @ author : LiLin
 * @ create : 2022-10-21 9:37
 */
@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideos(video);
        //获取主键id xml中不要忘记添加获取
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        //lambda表达式给集合赋值
        tagList.forEach(item ->{
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        videoDao.batchAddVideoTags(tagList);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        //第一步先判断合不合法
        if (size == null || no == null) {
            throw new ConditionException("参数异常！");
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("start",(no -1)*size);
        params.put("limit",size);
        params.put("area",area);
        //返回的是List先查个数
        List<Video> list = new ArrayList<>();
        Integer total = videoDao.pageCountVideos(params);
        if (total > 0) {
            //个数满足就去查分页数据
            list = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total,list);
        }

    public void viewVideoOnLineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewVideoOnLineBySlices(request,response,url);
    }

    public void addVideoLike(Long videoId, Long userId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId,userId);
        if (videoLike != null) {
            throw new ConditionException("已点赞");
        }
        //创建videoLike类创建进入表
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLikeByVideoIdAndUserId(videoId,userId);
    }

    /**
     * 查看视频点赞数量
     * @param videoId
     * @param userId
     * @return
     */
    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        Long count = videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        //游客模式和用户登录模式区分
        //判断如果用户点赞不是Null就是true值反之like为false
        //这里主要是前端页面展示是否点过赞功能体现
        boolean like = videoLike != null;
        HashMap<String, Object> result = new HashMap<>();
        result.put("count" ,count);
        result.put("like" ,like);
        return result;
    }

    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        //因为使用requestBody接收，所以我们需要对传递参数的判断
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        //删除原有视频收藏
        videoDao.deleteVideoCollection(videoId, userId);
        videoCollection.setVideoId(videoId);
        videoCollection.setUserId(userId);
        videoCollection.setGroupId(groupId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long videoId, Long userId) {
        //这里删除方法代码复用了
        videoDao.deleteVideoCollection(videoId,userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Long count = videoDao.getVideoCollections(videoId);
        VideoLike videoLike = videoDao.getVideoCollectionsByVideoIdAndUserId(videoId, userId);
        boolean like = videoLike != null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("count",count);
        map.put("like",like);
        return map;
    }
}
