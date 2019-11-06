package com.example.quartz.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class IO流 {
    public static void main(String[] args) {
        File file = new File("/home/yanrui/test.log");
//        File file = new File(".."+File.separatorChar+"untitled"+File.separatorChar+"test.txt");
        /*JDK7新特性可以在try后的括号中定义变量，并使用后系统自动关闭流对象*/
//        干嘛
//        BufferedWriter 字符缓冲输出流
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            bw.write("Hello World!!");
//            char[] chars = new char[1024];
//            int len;
//            while ((len = fr.read(chars)) != -1) {
//                System.out.println(new String(chars, 0 ,len));
//            }
            String line;
            //readLine()读取一行字符数据
            while ((line = br.readLine()) != null) {
//                String substring = line.substring(line.indexOf("-") + 1);
                Object[] split = line.split(",");
                System.out.println(Arrays.toString(split));
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
