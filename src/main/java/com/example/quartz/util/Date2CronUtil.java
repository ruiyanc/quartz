package com.example.quartz.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Date2CronUtil {
    public static String date2Cron(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
        return dateFormat.format(date);
    }
}
