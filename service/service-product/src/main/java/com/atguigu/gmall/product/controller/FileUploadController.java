package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.util.FileUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Date:2022/7/29
 * Author:cjc
 * Description:
 */
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {
    @Value("${fileServer.url}")
    private String fileUrl;

    /**
     * 文件上传
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam MultipartFile file) throws Exception {
        //返回文件路径
        return Result.ok(fileUrl+ FileUtil.upload(file));
    }
}
