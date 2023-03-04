package com.sdu.kob.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {

    public static String transformDatetime(Date datetime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(datetime.toInstant(), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now();
        long years = ChronoUnit.YEARS.between(dateTime, now);
        long months = ChronoUnit.MONTHS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        if (years > 0) {
            return years + "年前";
        } else if (months > 0) {
            return months + "个月前";
        } else if (days > 0) {
            return days + "天前";
        } else if (hours > 0) {
            return hours + "小时前";
        } else if (minutes > 0) {
            return minutes + "分钟前";
        } else {
            return "刚刚";
        }
    }
}
