package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.UserCoinDao;
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
import java.util.stream.Collectors;

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

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private UserService userService;

    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideos(video);
        //获取主键id xml中不要忘记添加获取
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        //lambda表达式给集合赋值
        tagList.forEach(item -> {
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
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        //返回的是List先查个数
        List<Video> list = new ArrayList<>();
        Integer total = videoDao.pageCountVideos(params);
        if (total > 0) {
            //个数满足就去查分页数据
            list = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total, list);
    }

    public void viewVideoOnLineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewVideoOnLineBySlices(request, response, url);
    }

    public void addVideoLike(Long videoId, Long userId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
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
        videoDao.deleteVideoLikeByVideoIdAndUserId(videoId, userId);
    }

    /**
     * 查看视频点赞数量
     *
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
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    @Transactional
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
        videoDao.deleteVideoCollection(videoId, userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Long count = videoDao.getVideoCollections(videoId);
        VideoLike videoLike = videoDao.getVideoCollectionsByVideoIdAndUserId(videoId, userId);
        boolean like = videoLike != null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("like", like);
        return map;
    }

    @Transactional //对多张表进行操作
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        //安全判断
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if (videoId == null) {
            throw new ConditionException("参数异常！~");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！~");
        }
        //查询当前登录用户是否拥有足够的硬币
        Integer userCoinAmount = userCoinService.getUserCoinAmount(userId);
        //游客投币
        userCoinAmount = userCoinAmount == null ? 0 : userCoinAmount;
        //余额不足
        if (amount > userCoinAmount) {
            throw new ConditionException("账户硬币余额不足");
        }
        //从这里开始投币
        //查看用户对当前视频的投币详情
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        //新增视频投币
        if (dbVideoCoin == null) {
            videoCoin.setUserId(userId);
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        } else {
            Integer dbAmount = dbVideoCoin.getAmount();
            dbAmount += amount;
            //更新视频投币
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        }
        //更新用户当前硬币数
        userCoinService.updateUserCoinsAmount(userId, (userCoinAmount - amount));
    }

    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        Long amount = videoDao.getVideoCoinsAmount(videoId);
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean like = videoCoin != null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("amount", amount);
        map.put("like", like);
        return map;
    }

    public void addVideoComment(VideoComment videoComment, Long userId) {
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("非法参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }


    /**
     * 评论分页
     *
     * @param size
     * @param no
     * @param videoId
     * @return
     */
    public PageResult<VideoComment> pageListVideoComment(Integer size, Integer no, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);
        //计算评论总数
        Integer total = videoDao.pageCountVideoComments(params);
        List<VideoComment> list = new ArrayList<>();
        if (total > 0) {
            //1.获得评论分页数据
            list = videoDao.pageListVideoComments(params);
            //2.批量查询二级评论
            List<Long> parentIdList = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootIds(parentIdList);//根据id->查询对应的rootIdList信息
            //批量查询用户信息
            Set<Long> userIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
//            Set<Long> replyUserId = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            userIdList.addAll(replyUserIdList);
//            userIdList.addAll(childUserIdList);
            //根据评论下的各个userId查到对应的详细信息
            List<UserInfo> userInfoList = userService.batchGetUserInfoByUserIds(userIdList);
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            //将一级评论下的二级评论也查出来
            list.forEach(comment -> {
                Long id = comment.getId();
                List<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child -> {
                    if (id.equals(child.getRootId())) {
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        childList.add(child);
                    }
                });
                comment.setChildList(childList);
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });
        }
        return new PageResult<>(total, list);
    }

    public Map<String, Object> getVideoDetails(Long videoId) {
        Video video = videoDao.getVideoDetails(videoId);
        Long userId = video.getUserId();
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        //查询对应视频用户信息
        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();
        Map<String, Object> params = new HashMap<>();
        params.put("video", video);
        params.put("userInfo", userInfo);
        return params;
    }
}
