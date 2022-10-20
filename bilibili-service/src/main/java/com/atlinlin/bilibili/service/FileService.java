package com.atlinlin.bilibili.service;

import com.atlinlin.bilibili.dao.FileDao;
import com.atlinlin.bilibili.domain.File;
import com.atlinlin.bilibili.service.util.FastDFSUtil;
import com.atlinlin.bilibili.service.util.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @ author : LiLin
 * @ create : 2022-10-20 21:16
 */
@Service
public class FileService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    public  String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }

    //上传文件
    public String uploadFileBySlices(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        //定义方法获取MD5加密文件
      File dbFileMD5 = fileDao.getFileByMd5(fileMD5);
        //秒传功能
        if (dbFileMD5 != null) {
            String url = dbFileMD5.getUrl();
            return url;
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        if(!StringUtil.isNullOrEmpty(url)){
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
            fileDao.addFile(dbFileMD5);
        }
        //返回文件名
        return url;
    }
}
