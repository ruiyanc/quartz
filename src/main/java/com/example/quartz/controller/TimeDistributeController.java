package com.example.quartz.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.quartz.service.LogService;
import com.example.quartz.service.ShellService;
import com.example.quartz.service.TimeDistributeService;
import com.example.quartz.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class TimeDistributeController {

    @Autowired
    private TimeDistributeService timeDistributeService;
    @Autowired
    private ShellService shellService;
    @Autowired
    private LogService logService;

    public static final String path = "/home/yanrui/";

    //定时爬虫
    @GetMapping("shell")
    public void shell() {
//        复制到的目标路径
        shellService.shell();
    }

    //ftp推送给客户
    @GetMapping("ftpPush")
    public void ftpPush() {
        List<Map<String, Object>> mapList = timeDistributeService.orderTime("3");
        for (Map<String, Object> map : mapList) {
            timeDistributeService.ftpPush(map);
        }
    }

    @GetMapping("findLog")
    public void findLog() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        File file = new File(path + File.separatorChar + date + File.separatorChar + "warn.log");
//        File file = new File(".."+File.separatorChar+"untitled"+File.separatorChar+"test.txt");
        /*JDK7新特性可以在try后的括号中定义变量，并使用后系统自动关闭流对象*/
//        BufferedWriter 字符缓冲输出流
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            //readLine()读取一行字符数据
            while ((line = br.readLine()) != null) {
//                String substring = line.substring(line.indexOf("-") + 1);
                Object[] list = line.split(",");
                logService.insertLog(list);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //每小时扫描一次定时任务
    @GetMapping("crawling")
    public String crawling() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "crawling");
        jsonObject.put("jobGroup", "crawling");
        jsonObject.put("description", "每小时爬取一次数据");
        jsonObject.put("requestType", "GET");
        jsonObject.put("url", "http://localhost:8111/shell");
        jsonObject.put("params", "");
        jsonObject.put("cronExpression", "0 0 */1 * * ?");
        HttpClientUtil.postJson("http://localhost:8111/quartz/httpJob/add", jsonObject.toString());
        return "success";
    }

    //每5分钟扫描一次定时任务
    @GetMapping("timer")
    public String timer() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "timer");
        jsonObject.put("jobGroup", "timer");
        jsonObject.put("description", "每五分钟查询一次所有已审核订单");
        jsonObject.put("requestType", "GET");
        jsonObject.put("url", "http://localhost:8111/timeDistribute");
        jsonObject.put("params", "");
        jsonObject.put("cronExpression", "0 */5 * * * ?");
        HttpClientUtil.postJson("http://localhost:8111/quartz/httpJob/add", jsonObject.toString());
        return "success";
    }


    @GetMapping("timeFindLog")
    public String timeFindLog() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "logger");
        jsonObject.put("jobGroup", "logger");
        jsonObject.put("description", "每小时扫描日志到数据库");
        jsonObject.put("requestType", "GET");
        jsonObject.put("url", "http://localhost:8111/findLog");
        jsonObject.put("params", "");
        jsonObject.put("cronExpression", "0 40 */1 * * ?");
        HttpClientUtil.postJson("http://localhost:8111/quartz/httpJob/add", jsonObject.toString());
        return "success";
    }

    //定时推送
    @GetMapping("timeDistribute")
    public void timeDistribute() {
        List<Map<String, Object>> mapList = timeDistributeService.orderTime("1");
        List<Map<String, Object>> delList = timeDistributeService.orderCyclicity("3");
        for (Map<String, Object> map : mapList) {
            String duration = map.get("subscription_duration").toString();
            String subscriptionId = map.get("subscription_id").toString();
            if ("".equals(duration)) {
                //立刻ftp推送给客户并且订单状态改为已结束
                timeDistributeService.ftpPush(map);
                timeDistributeService.pushEnd(subscriptionId, "-1");
//                System.out.println("成功");
            } else {
                try {
                    timeDistributeService.timeDisposal(map);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map<String, Object> map : delList) {
            timeDistributeService.deleteTime(map);
        }
    }
}
