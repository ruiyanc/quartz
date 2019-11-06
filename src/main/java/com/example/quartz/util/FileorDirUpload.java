package com.example.quartz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileorDirUpload {

    public static void fileFtpUpload(String host, int port, String username, String password, String localPath, String filePath) {
        File file = new File(localPath);
        System.out.println("进入ftp上传模块");
        if (!file.exists()) {
            System.out.println("本地路径不存在");
        } else {
            //如果是文件夹
            if (file.isDirectory()) {
                FTPUpload ftp = new FTPUpload(host, port, username, password);
                ftp.ftpLogin();
                System.out.println("1");
                //上传文件夹
                boolean uploadFlag = ftp.uploadDirectory(localPath, filePath);
                System.out.println("uploadFlag : " + uploadFlag);
            } else if (file.isFile()) {
                //如果是文件
                try {
                    FileInputStream in = new FileInputStream(file);
                    boolean flag = FtpApche.uploadFile(host, port, username, password, filePath, file.getName(), in);
                    System.out.println(flag);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
