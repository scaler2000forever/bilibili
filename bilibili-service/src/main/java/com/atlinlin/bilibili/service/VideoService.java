package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.VideoDao;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.PageResult;
import com.atlinlin.bilibili.domain.Video;
import com.atlinlin.bilibili.domain.VideoTag;
import com.atlinlin.bilibili.domain.exception.ConditionException;
import com.atlinlin.bilibili.service.util.FastDFSUtil;
import org.apache.commons.validator.routines.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
}
