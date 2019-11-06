package com.example.quartz.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.quartz.util.HttpClientUtil;
import com.example.quartz.util.JobUtil;
import org.quartz.CronExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@RequestMapping("job")
public class DemoController {

    @RequestMapping("/hello")
    public String hello() {
        JSONObject json = new JSONObject();
        json.put("ret", "hello");
        return json.toString();
    }

    @GetMapping("add")
    public String add() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", "t8");
        jsonObject.put("jobGroup", "t8");
        jsonObject.put("description", "测试");
        jsonObject.put("requestType", "GET");
        jsonObject.put("url", "http://localhost:8080/job/hello");
        jsonObject.put("params", "");
        jsonObject.put("cronExpression", "0 0/10 * * * ?");
        return HttpClientUtil.postJson("http://localhost:8080/quartz/httpJob/add", jsonObject.toString());
    }

    @PostMapping("delete")
    public String delete(HttpServletRequest request) {
        String jobName = request.getParameter("jobName");
        String jobGroup = request.getParameter("jobGroup");
        JSONObject jsonObject = new JSONObject();
        System.out.println(jobName);
        System.out.println(jobGroup);
        jsonObject.put("jobName", jobName);
        jsonObject.put("jobGroup", jobGroup);
        return HttpClientUtil.postJson("http://localhost:8080/quartz/job/delete", jsonObject.toString());
    }

    @PostMapping("update")
    public String update(HttpServletRequest request) {
//        CronExpression cronExpression = new CronExpression("0 0 10 * * ?");
        String jobName = request.getParameter("jobName");
        String jobGroup = request.getParameter("jobGroup");
        String cronExpression = request.getParameter("cronExpression");
        JSONObject jsonObject = new JSONObject();
        System.out.println(jobName);
        System.out.println(jobGroup);
        jsonObject.put("jobName", jobName);
        jsonObject.put("jobGroup", jobGroup);
        jsonObject.put("cronExpression", cronExpression);
        return HttpClientUtil.postJson("http://localhost:8080/quartz/job/update", jsonObject.toString());
    }

    @PostMapping("pause")
    public String pause(HttpServletRequest request) {
        String jobName = request.getParameter("jobName");
        String jobGroup = request.getParameter("jobGroup");
        JSONObject jsonObject = new JSONObject();
        System.out.println(jobName);
        System.out.println(jobGroup);
        jsonObject.put("jobName", jobName);
        jsonObject.put("jobGroup", jobGroup);
        return HttpClientUtil.postJson("http://localhost:8080/quartz/job/resume", jsonObject.toString());
    }


}
