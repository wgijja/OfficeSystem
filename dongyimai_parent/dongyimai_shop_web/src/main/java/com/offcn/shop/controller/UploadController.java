package com.offcn.shop.controller;

import com.offcn.entity.Result;
import com.offcn.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_PATH}")
    private String FILE_SERVER_PATH;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {
        //1、获取文件的全名称
        String fileName = file.getOriginalFilename();
        //2、截取文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            //3、实例化上传文件工具类
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            //4、使用工具类中的方法完成文件上传,此处获取到的文件路径是以组名开头的，需要加上服务器地址
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //5、将图片路径返回到前端
            String url = FILE_SERVER_PATH + path;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "网络繁忙，请稍候再试");
        }
    }
}
