package com.atlinlin.bilibili.service.util;

import com.atlinlin.bilibili.domain.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.util.*;

/**
 * @ author : LiLin
 * @ create : 2022-10-20 13:00
 */
@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static final String PATH_KEY = "path-key:";
    public static final String UPLOAD_SIZE_KEY = "uploaded-size-key:";
    public static final String UPLOAD_NO_KEY = "uploaded-no-key:";
    public static final String DEFAULT_GROUP = "group1";
    public static final Integer SLICE_SIZE = 1024 * 1024 * 2;

    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        //注意subString范围包含左括号
        return fileName.substring(index + 1);
    }


    //上传
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        //当前类对象的方法
        String fileType = this.getFileType(file);
        fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    /**
     * 上传可以断点续传的文件
     *
     * @param file 传入文件
     * @return
     */
    public String uploadAppendFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        //当前类对象的方法
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
     * 添加断点续传文件 不会导致重复文件添加
     *
     * @param file
     * @param filePath 文件路径
     * @param offset   偏移量
     * @throws IOException
     */
    public void modifyAppendFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }


    //删除
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }


    /**
     * 分片断点续传
     * @param file       文件
     * @param fileMD5    md5加密文件
     * @param slice      分片
     * @param totalSlice 总片数
     * @return
     * @throws Exception
     */
    public String uploadFileBySlices(MultipartFile file, String fileMD5, Integer slice, Integer totalSlice) throws Exception {
        if (file == null || slice == null || totalSlice == null) {
            throw new ConditionException("参数异常");
        }
        String pathKey = PATH_KEY + fileMD5;
        String uploadNoKey = UPLOAD_NO_KEY + fileMD5;
        String uploadSizeKey = UPLOAD_SIZE_KEY + fileMD5;
        //获取上传大小 是否是第一个还是之后续传
        String uploadSizeStr = redisTemplate.opsForValue().get(uploadSizeKey);
        Long uploadedSize = 0L;
        if (!StringUtils.isNullOrEmpty(uploadSizeStr)) {
            uploadedSize = Long.valueOf(uploadSizeStr);
        }
        String fileType = this.getFileType(file);
        //判断上传的是第一个分片还是其他分片
        if (slice == 1) {
            String path = this.uploadAppendFile(file);
            //第一次上传判断是否成功
            if (StringUtils.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败! ");
            }
            //上传的保存在redis中
            redisTemplate.opsForValue().set(pathKey, path);

            redisTemplate.opsForValue().set(uploadNoKey,"1");
        } else {
            //不是第一次上传
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtils.isNullOrEmpty(filePath)) {
                throw new ConnectException("上传失败!");
            }
            //使用modify方法不是append方法
            this.modifyAppendFile(file, filePath, uploadedSize);
            //更新分片
            redisTemplate.opsForValue().increment(uploadNoKey);
        }
        //修改历史上传上传分片文件大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadSizeKey, String.valueOf(uploadedSize));
        //判断是否已经上传完,上传完后清除redis里面的key值和value值
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        if (uploadedNo.equals(totalSlice)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            //redisTemplate中有删除方法，需要集合类型
            List<String> list = Arrays.asList(uploadNoKey, pathKey, uploadSizeKey);
            redisTemplate.delete(list);
        }
        return resultPath;
    }

    /**
     * 切片处理
     * 指定文件切成多少片，那么需要用到IO流的关系，我们先读取文件的大小
     * 确定好每次读取多少，比如SLICE_SIZE=1024*2，在指定我们写入的路径path
     * @param multipartFile
     * @throws IOException
     */
    public void convertFileToSlice(MultipartFile multipartFile) throws IOException {
        String fileType = this.getFileType(multipartFile);
        File file =this.multipartFileToFile(multipartFile);
        long fileLength = file.length();
        int count = 1;
        for(int i = 0;i<fileLength;i+=SLICE_SIZE){
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
            randomAccessFile.seek(i);
            //长度是实际读取的，注意最后一次读取
            byte[]bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read();
            String path = "E:\\tempFile"+count+"."+fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes,0,len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        file.delete();//删除临时文件
    }



    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        File file = File.createTempFile(fileName[0],"."+fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    /**
     * fastDfs获取文件信息
     * @param request
     * @param response
     * @param path
     */
    public void viewVideoOnLineBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        //总大小
        long totalFileSize = fileInfo.getFileSize();
        //路径
        String url = httpFdfsStorageAddr + path;
        //获取请求头中的信息
        Enumeration<String> headerNames = request.getHeaderNames();//枚举类
        Map<String,Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header,request.getHeader(header));
        }

        String rangeStr = request.getHeader("range");
        String[]range;
        if(StringUtils.isNullOrEmpty(rangeStr)){
            rangeStr = "bytes=0-"+(totalFileSize-1);
        }
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        if(range.length >=2){
            begin = Long.parseLong(range[1]);
        }
        long end = totalFileSize -1;
        if(range.length >= 3){
            end = Long.parseLong(range[2]);
        }
        long len = (end-begin)+1;
        String contentRange = "bytes"+begin+"-"+end+"/"+totalFileSize;
        response.setHeader("Content-Range",contentRange);
        response.setHeader("Accept-Ranges","bytes");
        response.setHeader("Content-Type","video/mp4");
        response.setContentLength((int)len);
        HttpUtil.get(url,headers,response);
    }
}
