package com.atlinlin.bilibili.api;

import com.atlinlin.bilibili.domain.JsonResponse;
import com.atlinlin.bilibili.service.FileService;
import com.atlinlin.bilibili.service.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ author : LiLin
 * @ create : 2022-10-20 21:15
 */
@RestController
public class FileApi {

    @Autowired
    private FileService fileService;

    /**
     * 给文件进行MD5加密返回给前端，前端拿着这个加密文件进行断点续传 这个操作生产中前端完成
     * @param file 上传文件
     * @return
     */
    @PostMapping("/md5files")
    public JsonResponse<String> getFileMD5(MultipartFile file) throws Exception{
        String fileMD5 = fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
    }

    /**
     * 分片好的文件上传
     * @param slice 片
     * @param fileMD5 加密文件
     * @param sliceNo 分片序号
     * @param totalSliceNo 分片总数
     * @return 返回上传的路径地址
     * @throws Exception
     */
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice,
                                                   String fileMD5,
                                                   Integer sliceNo,
                                                   Integer totalSliceNo) throws Exception{
       return new JsonResponse<String>(fileService.uploadFileBySlices(slice,fileMD5,sliceNo,totalSliceNo));
    }

}
