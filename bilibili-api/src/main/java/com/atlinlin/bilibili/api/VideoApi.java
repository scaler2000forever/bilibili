package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.api.support.UserSupport;
import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.domain.PageResult;
import com.atlinlin.bilibili.domain.Video;
import com.atlinlin.bilibili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
}
