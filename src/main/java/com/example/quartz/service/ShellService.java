package com.example.quartz.service;

import com.example.quartz.util.JavaShellUtil;
import org.springframework.stereotype.Service;

@Service
public class ShellService {


    public void shell() {
        String command = "./ChinaAQIData-master/data_from_cepm.sh";
        //需要传递的就是shell脚本的位置
        int retCode = JavaShellUtil.ExecCommand(command);
        System.out.println(retCode);
        if (retCode == 0) {
            System.out.println("success.....");
        } else {
            System.out.println("error.....");
        }
       /* String format = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());
        try {
            //要复制到的目标路径
            CopyFileUtil.copyCheck("ChinaAQIData-master/archives/" + format + ".json", tarPath);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
