package com.example.quartz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertLog(Object[] list) {
        String sql = "insert into log(log_date,encode,send_method,send_target) value (?,?,?,?)";
        jdbcTemplate.update(sql, list);
    }
}
