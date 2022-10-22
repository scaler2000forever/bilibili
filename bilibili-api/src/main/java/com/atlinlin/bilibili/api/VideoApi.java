package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.*;
import com.atlinlin.bilibili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ author : LiLin
 * @ create : 2022-10-21 9:36
 */

@RestController
public class VideoApi {

    @Autowired
    private UserSupport userSupport;
    @Autowired
    private VideoService videoService;

    /**
     * 视频投稿
     * @param video
     * @return
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video){
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        return JsonResponse.success();
    }

    /**
     * 瀑布流加载页面分区
     * @param size
     * @param no
     * @param area
     * @return
     */
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size,Integer no ,String area){
        PageResult<Video> result = videoService.pageListVideos(size,no,area);
        return new JsonResponse<>(result);
    }

    //通过流的方式传输，响应写在Http流的输出里
    @GetMapping("/video-slices")
    public void viewVideoOnLineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        videoService.viewVideoOnLineBySlices(request,response,url);

    }


    /**
     * 点赞(简易)
     * @param videoId 视频id
     * @return
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(videoId,userId);
        return JsonResponse.success();
    }

    /**
     * 点赞删除
     * @param videoId 视频id
     * @return
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(videoId,userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频点赞数量
     * @return
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String,Object>> getVideoLikes(@RequestParam Long videoId){
        //区分游客模式和用户登录模式
        Long userId = null;
        try {
             userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
        }
        Map<String,Object> result = videoService.getVideoLikes(videoId,userId);
        return new JsonResponse<>(result);
    }

    /**
     * 添加视频收藏
     * @param videoCollection 请求的视频
     * @return
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection){
        Long userId = userSupport.getCurrentUserId();
         videoService.addVideoCollection(videoCollection,userId);
         return JsonResponse.success();
    }

    /**
     * 取消视频收藏
     * @param videoId
     * @return
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(videoId,userId);
        return JsonResponse.success();
    }

    /**
     * 查看视频收藏量
     * @param videoId
     * @return
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String,Object>> getVideoCollection(@RequestParam Long videoId){
        //这里也要区分游客模式和用户登录状态
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) { }
        //这里是表现层体现业务不用显示具体 故 map创建是由service层创建
      Map<String, Object> result = videoService.getVideoCollections(videoId,userId);
        return new JsonResponse<>(result);
    }

    /**
     * 视频投币
     * @param videoCoin 视频投币
     * @return
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin){
        //这里想一下投币这个功能游客是不可能的
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(videoCoin,userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频投币量
     * @param videoId
     * @return
     */
    @GetMapping("/video-coins")
    public JsonResponse<Map<String,Object>> getVideoCoins(@RequestParam Long videoId){
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) { }
        Map<String,Object> result = videoService.getVideoCoins(videoId,userId);
        return new JsonResponse<>(result);
    }

    /**
     * 添加视频评论
     * @param videoComment
     * @return
     */
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComment(@RequestBody VideoComment videoComment){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComment(videoComment,userId);
        return JsonResponse.success();
    }

    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComment(@RequestParam Integer size,
                                                                       @RequestParam Integer no,
                                                                       @RequestParam Long videoId){
        PageResult<VideoComment> result = videoService.pageListVideoComment(size,no,videoId);
        return new JsonResponse<>(result);
    }

}
