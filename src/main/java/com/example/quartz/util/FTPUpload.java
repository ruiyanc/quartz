package com.example.quartz.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.TimeZone;

public class FTPUpload {
    private FTPClient ftpClient;
    private String strIp;
    private int intPort;
    private String user;
    private String password;

     /**
      * Ftp构造函数
      * */

     FTPUpload(String strIp, int intPort, String user, String password) {
        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = password;
        this.ftpClient = new FTPClient();
    }

    /**
     *  判断是否登入成功
     */
    void ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());

        this.ftpClient.setControlEncoding("UTF-8");
        this.ftpClient.configure(ftpClientConfig);
        try {
            if (this.intPort > 0) {
                this.ftpClient.connect(this.strIp, this.intPort);
            } else {
                this.ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                System.out.println("登录FTP服务失败！");
                return;
            }
            this.ftpClient.login(this.user, this.password);
            // 设置传输协议
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            System.out.println("恭喜" + this.user + "成功登陆FTP服务器");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println((this.user + "登录FTP服务失败！" + e.getMessage()));
        }
        this.ftpClient.setBufferSize(1024 * 2);
        this.ftpClient.setDataTimeout(30 * 1000);
    }

    /**
     * 退出关闭服务器链接
     */
    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                // 退出FTP服务器
                boolean reuslt = this.ftpClient.logout();
                if (reuslt) {
                    System.out.println("成功退出服务器");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("退出FTP服务器异常！" + e.getMessage());
            } finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("关闭FTP服务器的连接异常！");
                }
            }
        }
    }

    /***
     * 上传Ftp文件
     * @param localFile 当地文件
     * @param romotUpLoadePath  上传服务器路径 - 应该以/结束 */
    private void uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success;
        try {
            // 改变工作路径
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            System.out.println(localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (!success) {
                System.out.println(localFile.getName() + "上传成功");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(localFile + "未找到");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * 下载文件
     * @param remoteFileName   待下载文件名称
     * @param localDires 下载到当地那个路径下
     * @param remoteDownLoadPath remoteFileName所在的路径  */

    private void downloadFile(String remoteFileName, String localDires,
                              String remoteDownLoadPath) {
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(
                    strFilePath));
            System.out.println(remoteFileName + "开始下载....");
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            if (!success ) {
                System.out.println(remoteFileName + "成功下载到" + strFilePath);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(remoteFileName + "下载失败");
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (success ) {
            System.out.println(remoteFileName + "下载失败!!!");
        }
    }

    /***
     * 上传文件夹
     * @param localDirectory   当地文件夹
     * @param remoteDirectoryPath   Ftp 服务器路径 以目录"/"结束
     * */
    boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
        File src = new File(localDirectory);
        try {
            remoteDirectoryPath = remoteDirectoryPath + "/"+ src.getName() ;
            boolean makeDirFlag = false;
            if (!this.ftpClient.changeWorkingDirectory(remoteDirectoryPath)) {
                //如果目录不存在创建目录
                String[] dirs = remoteDirectoryPath.split("/");
                String tempPath ="/";
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {continue;}
                    tempPath += "/" + dir;
                    if (!this.ftpClient.changeWorkingDirectory(tempPath)) {
                        if (this.ftpClient.makeDirectory(tempPath)) {
                            makeDirFlag=true;
                            this.ftpClient.changeWorkingDirectory(tempPath);
                        }
                    }
                }

            }
            System.out.println("localDirectory : " + localDirectory);
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("src.getName() : " + src.getName());
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("makeDirFlag : " + makeDirFlag);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(remoteDirectoryPath + "目录创建失败");
        }
        File[] allFile = src.listFiles();
        assert allFile != null;
        for (File anAllFile1 : allFile) {
            if (!anAllFile1.isDirectory()) {
                String srcName = anAllFile1.getPath();
                uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }
        for (File anAllFile : allFile) {
            if (anAllFile.isDirectory()) {
                // 递归
                uploadDirectory(anAllFile.getPath(), remoteDirectoryPath);
            }
        }
        return true;
    }

    /***
     * @下载文件夹
     * @param localDirectoryPath  本地地址
     * @param remoteDirectory 远程文件夹   */
    private void downLoadDirectory(String localDirectoryPath, String remoteDirectory) {
        try {
            String fileName = new File(remoteDirectory).getName();
            localDirectoryPath = localDirectoryPath + fileName + "//";
            new File(localDirectoryPath).mkdirs();
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
            for (FTPFile anAllFile : allFile) {
                if (!anAllFile.isDirectory()) {
                    downloadFile(anAllFile.getName(), localDirectoryPath, remoteDirectory);
                }
            }
            for (FTPFile anAllFile : allFile) {
                if (anAllFile.isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/" + anAllFile.getName();
                    downLoadDirectory(localDirectoryPath, strremoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("下载文件夹失败");
        }
    }

     /**FtpClient的Set 和 Get 函数*/
    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}