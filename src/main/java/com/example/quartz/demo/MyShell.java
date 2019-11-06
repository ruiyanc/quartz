package com.example.quartz.demo;

import com.example.quartz.util.CopyFileUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyShell {
    public static void main(String[] args) {
        String command = "ChinaAQIData-master/data_from_cepm.sh";
        //需要传递的就是shell脚本的位置
        int retCode = JavaShellUtil.ExecCommand(command);
        System.out.println(retCode);
        if (retCode == 0) {
            System.out.println("success.....");
        } else {
            System.out.println("error.....");
        }
        String format = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());
        try {
            //要复制到的目标路径
            String tarPath = "/";
            CopyFileUtil.copyCheck("ChinaAQIData-master/archives/" + format + ".json", tarPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
