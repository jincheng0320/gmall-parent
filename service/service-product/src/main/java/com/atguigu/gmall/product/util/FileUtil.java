package com.atguigu.gmall.product.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Date:2022/7/29
 * Author:cjc
 * Description:文件管理工具
 */
public class FileUtil {
    static {
        try {
            //加载配置文件
            ClassPathResource classPathResource = new ClassPathResource("track.conf");
            //初始化fastDfs对象
            ClientGlobal.init(classPathResource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     * @param file
     * @return
     * @throws Exception
     */
    public static String upload(MultipartFile file)throws Exception{
        //初始化tracker对象
        TrackerClient trackerClient = new TrackerClient();
        //获取服务链接
        TrackerServer connection = trackerClient.getConnection();
        //初始化storageClient
        StorageClient storageClient = new StorageClient(connection,null);
        //获取文件扩展名
        String fileNme = StringUtils.getFilenameExtension(file.getOriginalFilename());
        //文件上传
        String[] strings = storageClient.upload_file(file.getBytes(), fileNme, null);
        return strings[0]+"/"+strings[1];
    }

    /**
     * 文件下载
     * @param groupNme
     * @param path
     * @return
     * @throws Exception
     */
    public static byte[] download(String groupNme, String path)throws Exception{
        //初始化tracker对象
        TrackerClient trackerClient = new TrackerClient();
        //获取服务链接
        TrackerServer connection = trackerClient.getConnection();
        //初始化storageClient
        StorageClient storageClient = new StorageClient(connection,null);
        //文件下载
        byte[] bytes = storageClient.download_file(groupNme, path);
        return bytes;
    }

    /**
     * 文件删除
     * @param groupNme
     * @param path
     * @return
     * @throws Exception
     */
    public static boolean delete(String groupNme, String path) throws Exception{
        //初始化tracker对象
        TrackerClient trackerClient = new TrackerClient();
        //获取服务链接
        TrackerServer connection = trackerClient.getConnection();
        //初始化storageClient
        StorageClient storageClient = new StorageClient(connection,null);
        //文件删除
        int i = storageClient.delete_file(groupNme, path);
        return i == 0;
    }


    //将文件下载到D盘 并以1.jpg/1.png
//    public static void main(String[] args) throws Exception{
//        String groupName = "group1";
//        String path = "M00/00/01/wKjIgGLkCliAS85PAALgVKLUz70820.jpg";
//        //获取文件后缀名
//        String suffix = path.split("\\.")[1];
//        //System.out.println("suffix = " + suffix);
//        //指定存的路劲
//        File file = new File("D:\\1."+suffix);
//       //2 System.out.println("file = " + file.getPath());
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        fileOutputStream.write(download(groupName,path));
//        fileOutputStream.close();
//
//    }
}
