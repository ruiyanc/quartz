package com.example.quartz.service;

import com.alibaba.fastjson.JSONObject;
import com.example.quartz.util.Date2CronUtil;
import com.example.quartz.util.FileorDirUpload;
import com.example.quartz.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TimeDistributeService {

    public static final String PATH = "/root/testFileinDir/";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //推送完成时状态改为已结束
    public void pushEnd(String subscriptionId, String state) {
        String sql;
        if ("1".equals(subscriptionId.substring(0, 1))) {
            sql = "update data_history_subscription " +
                    " set state =" + state +
                    " where history_subscription_id=" + subscriptionId;
        } else {
            sql = "update data_cyclicity_subscription " +
                    " set state =" + state +
                    " where cyclicity_subscription_id=" + subscriptionId;
        }
        jdbcTemplate.update(sql);
    }

    public void pushEnd(String subscriptionId, String state, Timestamp first, Timestamp last) {
        String sql = null;
        if ("0".equals(subscriptionId.substring(0, 1))) {
            sql = "update data_cyclicity_subscription " +
                    " set state =" + state + ",first_distribute_date='" + first + "',last_distribute_date='" + last + "'" +
                    " where cyclicity_subscription_id=" + subscriptionId;
        }
        jdbcTemplate.update(sql);
    }

    //定时查询已审核订单
    public List<Map<String, Object>> orderTime(String state) {
        String sql = "SELECT cyclicity_subscription_info.cyclicity_subscription_id as subscription_id,subscription_name,subscription_type,ftp_adress,ftp_username,ftp_password,ftp_port,remote_path, subscription_duration, application_date,state,checked_date,checked_feedback,user_id,cyclicity_subscription_info.encode as encode,data_type,data_name,last_distribute_date,first_distribute_date"
                + " FROM data_cyclicity_subscription,cyclicity_subscription_info,data_info "
                + " where data_info.encode=cyclicity_subscription_info.encode and cyclicity_subscription_info.cyclicity_subscription_id= data_cyclicity_subscription.cyclicity_subscription_id and state=" + state
                + " UNION"
                + " SELECT history_subscription_info.history_subscription_id as subscription_id,subscription_name,subscription_type,ftp_adress,ftp_username,ftp_password,ftp_port,remote_path, '' as subscription_duration , application_date,state,checked_date,checked_feedback,user_id ,history_subscription_info.encode as encode,data_type,data_name,'' as last_distribute_date, '' as first_distribute_date"
                + " FROM data_history_subscription ,history_subscription_info,data_info"
                + " where  data_info.encode=history_subscription_info.encode and history_subscription_info.history_subscription_id= data_history_subscription.history_subscription_id and state=" + state;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> orderCyclicity(String state) {
        String sql = "SELECT cyclicity_subscription_info.cyclicity_subscription_id as subscription_id,subscription_name,subscription_type,ftp_adress,ftp_username,ftp_password,ftp_port,remote_path, subscription_duration, application_date,state,checked_date,checked_feedback,user_id,cyclicity_subscription_info.encode as encode,data_type,data_name,last_distribute_date,first_distribute_date"
                + " FROM data_cyclicity_subscription,cyclicity_subscription_info,data_info "
                + " where data_info.encode=cyclicity_subscription_info.encode and cyclicity_subscription_info.cyclicity_subscription_id= data_cyclicity_subscription.cyclicity_subscription_id and state=" + state;
        return jdbcTemplate.queryForList(sql);
    }

    //根据dataType选取相应的文件夹ftp推送
    public void ftpPush(Map map) {
        String type;
        String encode = map.get("encode").toString();
        String ftpAddress = map.get("ftp_adress").toString();
        String ftpUsername = map.get("ftp_username").toString();
        String ftpPassword = map.get("ftp_password").toString();
        int ftpPort = Integer.parseInt(map.get("ftp_port").toString());
        String remotePath = map.get("remote_path").toString();
        String subscriptionName = map.get("subscription_name").toString();
        String format = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());
        //ftp推送
        //参数encode为文件夹名或文件名,remotePath为目标路径
        FileorDirUpload.fileFtpUpload(ftpAddress, ftpPort, ftpUsername, ftpPassword, PATH + format + ".json", remotePath);
    }

    //定时处理
    public void timeDisposal(Map map) throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date checkedDate = (Date) map.get("checked_date");
        checkedDate = new Date();
        String cron = Date2CronUtil.date2Cron(checkedDate);
        Calendar c = Calendar.getInstance();
        //设置时间
        c.setTime(checkedDate);
        String subscriptionId = map.get("subscription_id").toString();
        String duration = map.get("subscription_duration").toString();
        String s = duration.substring(0, duration.length() - 1);
        String d = duration.substring(duration.length() - 1);
        //定时ftp推送
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", subscriptionId);
        jsonObject.put("jobGroup", subscriptionId);
        jsonObject.put("description", "实时订单定时分发");
        jsonObject.put("requestType", "GET");
        jsonObject.put("url", "http://localhost:8111/ftpPush");
        jsonObject.put("params", "");
//        测试时间修改
        jsonObject.put("cronExpression", cron.replace(cron.substring(3, 5), cron.substring(3, 5) + "/15"));
        HttpClientUtil.postJson("http://localhost:8111/quartz/httpJob/add", jsonObject.toString());
//            "ss mm HH dd MM ? yyyy"
        switch (d) {
            case "日":
                c.add(Calendar.DATE, Integer.parseInt(s));
                break;
            case "周":
                //日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
                c.add(Calendar.WEEK_OF_MONTH, Integer.parseInt(s));
                break;
            case "月":
                c.add(Calendar.MONTH, Integer.parseInt(s));
                break;
            default:
                c.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(s));
        }
        Date date = c.getTime();
        Timestamp last = new Timestamp(date.getTime());
        Date date1 = new Date();
        Timestamp first = new Timestamp(date1.getTime());
        pushEnd(subscriptionId, "3", first, last);
    }

    public void deleteTime(Map map) {
        Date lastDistributeDate = (Date) map.get("last_distribute_date");
        String subscriptionId = map.get("subscription_id").toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobName", subscriptionId);
        jsonObject.put("jobGroup", subscriptionId);
        if (new Date().after(lastDistributeDate)) {
            HttpClientUtil.postJson("http://localhost:8111/quartz/job/delete", jsonObject.toString());
            pushEnd(subscriptionId, "-1");
        }
    }
}
