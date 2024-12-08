package com.weatherapp.WeatherApp.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateUtil {

    private SimpleDateFormat simpleDateFormatter;

    @PostConstruct
    public void init() {
        simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    public String getServerTime() {
        return simpleDateFormatter.format(new Date());
    }
}
