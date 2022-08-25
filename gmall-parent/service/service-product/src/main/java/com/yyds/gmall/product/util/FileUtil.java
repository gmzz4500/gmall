package com.yyds.gmall.product.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName: FileUtil
 * @Author: yyd
 * @Date: 2022/7/29/029
 * @Description:
 */

/***
 * 图片管理的工具包
 */
public class FileUtil {

    /**
     * 在静态模块中加载配置文件
     */
    static {
        try {
            //加载配置文件
            ClassPathResource resource = new ClassPathResource("fastdfs.conf");
            //初始化fastdfs
            ClientGlobal.init(resource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param file
     * @return
     * @throws Exception
     */
    public static String uploadFile(MultipartFile file) throws Exception{

        //初始化tracker
        TrackerClient trackerClient = new TrackerClient();
        //通过trackerClient获取连接
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过trackerServer获取storage的信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        //通过storageClient文件上传
        String originalFilename = file.getOriginalFilename();
        /**
         * 1.文件的字节码
         * 2.文件的拓展名
         * 3.附加参数:水印、拍摄时间地点等等
         */
        String[] strings = storageClient.upload_file(file.getBytes(),
                StringUtils.getFilenameExtension(originalFilename),
                null);
        return strings[0] + "/" + strings[1];
    }

    /**
     * 文件下载
     * @param groupName
     * @param path
     * @return
     * @throws Exception
     */
    public static byte[] downloadFile(String groupName, String path) throws Exception{
        //通过storageClient进行文件下载
        byte[] bytes = FileUtil.getStorageClient().download_file(groupName, path);
        //返回字节数组
        return bytes;
    }

    public static boolean deleteFile(String groupName, String path)throws Exception{
        //通过storageClient进行文件删除
        int i = FileUtil.getStorageClient().delete_file(groupName, path);
        //返回结果
        return i == 0;
    }

    /**
     * 把字节数组转换成本地文件的方法
     * @param bytes
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean saveFile(byte[] bytes,String filePath,String fileName){
        File file = null;

//        BufferedOutputStream bos = null;

        FileOutputStream fos = null;
        try {
            file = new File(filePath + "\\" + fileName);

//            bos = new BufferedOutputStream(new FileOutputStream(file));
//            bos.write(bytes);
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtil.downloadFile("group1", "M00/00/01/wKjIgGLkIGCAQy6oAAGEpu0WceU976.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String filenameExtension = StringUtils.getFilenameExtension("wKjIgGLkIGCAQy6oAAGEpu0WceU976.png");
//        String fileName = UUID.randomUUID().toString() + filenameExtension;
        String fileName = "1." + filenameExtension;
        boolean save = FileUtil.saveFile(bytes,"D:",fileName);
        System.out.println(save?"图片下载成功":"图片下载失败");
    }

    /**
     * 获取storageClient的私有方法
     * @return
     */
    private static StorageClient getStorageClient() throws Exception{
        //初始化tracker
        TrackerClient trackerClient = new TrackerClient();
        //通过tracker获取连接
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过trackerServer获取storage的信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }
}
