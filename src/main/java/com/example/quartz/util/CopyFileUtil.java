package com.example.quartz.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 逸之君
 */
public class CopyFileUtil {
    private static void copy(String OrlPath,String TarPath) throws IOException {
        System.out.println("开始复制...");
        File file=new File(OrlPath);
        if(file.isDirectory()){//是文件夹,获取该文件夹下所有
            System.out.println("文件夹:");
            File[] files = file.listFiles();//如果为空  不会执行for循环
            if(files.length==0) {
                System.out.println("给定目录是空文件夹");
            }else {//存在文件
                for(File listfile:files) {//取出下级目录存到file
                    System.out.println("目录路径:"+file.getAbsolutePath());//返回文件绝对路径  名
                    if(listfile.isDirectory()) {//是文件夹，创建
                        System.out.println("文件夹 :"+TarPath+File.separator+listfile.getName());
                        CopyDir(TarPath+File.separator+listfile.getName());
                        copy(listfile.getAbsolutePath(),TarPath+File.separator+listfile.getName());

                    }else {//是文件，复制
                        System.out.println("文件:"+" "+listfile.getAbsolutePath()+" "+TarPath+File.separator+listfile.getName());
                        CopyFile(listfile.getAbsolutePath(),TarPath+File.separator+listfile.getName());// 如果没有递归取到最下级目录
                        PrintDetail(listfile);
                    }
                }
            }//不为空，执行拷贝
        }else {//给定路径不是文件夹
            CopyFile(OrlPath,TarPath+File.separator+file.getName());
            PrintDetail(file);
        }
    }
    private static void CopyFile(String OrlPath,String TarPath) throws IOException {
        /*
         * 文件复制
         */
        System.out.println("正在复制...");
        RandomAccessFile Red_file=new RandomAccessFile(OrlPath, "r");
        RandomAccessFile Write_file=new RandomAccessFile(TarPath, "rw");

        byte[] File_BYTE=new byte[1024*1024];
        int len;
        long startTime=System.currentTimeMillis();
        while((len=Red_file.read(File_BYTE))!=-1) {
            Write_file.write(File_BYTE, 0, len);
            //System.out.println("读取长度："+len);
            //System.out.println("文件写入"+Write_file.length()+"byte");
        }
        long endTime=System.currentTimeMillis();
        long userTime=endTime-startTime;
        System.out.println("复制成功，耗时："+userTime+" ms");
        Red_file.close();
        Write_file.close();
    }

    private static void PrintDetail(File file) {
        System.out.println("文件名称:"+file.getName());//返回文件夹名称或者文件名称
        System.out.println("文件路径:"+file.getAbsolutePath());//返回文件绝对路径  名
        System.out.println("文件大小:"+file.length());

        Long lastModiTime=file.lastModified();//返回的是一个long   毫秒数
        Date date=new Date(lastModiTime);//毫秒数转标准时间格式，返回类型是Date
        //date转string 规定格式
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年mm月dd日 hh:mm:ss");
        String thisTime=simpleDateFormat.format(new Date(System.currentTimeMillis()));
        System.out.println("当前系统时间:"+thisTime);//java中是最常用的获取系统时间的方法，它返回的是1970年1月1日0点到现在经过的毫秒数
        String lastTime=simpleDateFormat.format(date);
        System.out.println("文件最后修改时间:"+lastTime);
    }

    private static void CopyDir(String dsrc) {//复制文件夹
        File dSrcFile= new File(dsrc);
        if(dSrcFile.mkdir()) {
            System.out.println("文件夹创建成功");
        }
    }

    public static void copyCheck(String OrlPath,String TarPath) throws IOException {


        File Orlfile=new File(OrlPath);
        File Tarfile=new File(TarPath);
        if(Orlfile.exists()){//给定路径是否存在
            System.out.println("给定路径存在");
            if(Tarfile.exists()) {
                System.out.println("目标路径存在");
                copy(OrlPath,TarPath);
            }else {
                System.out.println("目标路径不存在，创建目标路径 ");
                if(Tarfile.mkdirs()) {
                    System.out.println("目标路径创建成功");
                    copy(OrlPath,TarPath);
                }else {
                    System.out.println("目标路径创建失败");
                }
            }
        }else {//给定路径不存在
            System.out.println("给定路径不存在 ");
        }

    }


}
