package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.service.DemoService;
import com.atlinlin.bilibili.service.FileService;
import com.atlinlin.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @ author : LiLin
 * @ create : 2022-10-14 19:46
 */

@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @GetMapping("/query")
    public Long query(Long id){
        return demoService.query(id);
    }

    /**
     * 文件分片  这个操作生产中前端完成
     * @param file 传入的文件
     * @throws Exception
     */
    @GetMapping
    public void slices(MultipartFile file) throws Exception{
        fastDFSUtil.convertFileToSlice(file);
    }


}
