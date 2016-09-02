package com.csuft.phoneinterception.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wuhaiwen on 2016/8/30.
 */
public class JudgeCurrentTime {

    public static boolean isDuration(int start_hour, int start_minute, int end_hour, int end_minute) {
        boolean is_in_duration = false;
        SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
        Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
        String strs = formatters.format(curDates);
//        //开始时间
//        int start_hour = hour_from.getValue();
//        int start_minute = minute_from.getValue();
        //分钟数
        int start_total_minute = start_hour * 60 + start_minute;
        //结束时间
//        int end_hour = hour_to.getValue();
//        int end_minute = minute_to.getValue();
        //结束的分钟数
        int end_total_minute = end_hour * 60 + end_minute;

        String[] dds = new String[]{};

        // 分取系统时间 小时分
        dds = strs.split(":");
        int current_hour = Integer.parseInt(dds[0]);
        int current_minute = Integer.parseInt(dds[1]);
        //当前分钟数
        int current_total_minute = current_hour * 60 + current_minute;
//        Log.d("hahahahah",String.valueOf(start_total_minute)+" "+String.valueOf(end_total_minute)+" "+String.valueOf(current_total_minute));

        if (start_hour < end_hour) {
            //当前是同一天
            if (start_total_minute <= current_total_minute && current_total_minute <= end_total_minute) {
                is_in_duration = true;
            } else {
                is_in_duration = false;
            }
        } else if (start_hour >= end_hour) {
            //当前是从第一点到第二天
            if (start_total_minute <= current_total_minute && current_total_minute <= end_total_minute + 24 * 60) {
                is_in_duration = true;
            } else {
                is_in_duration = false;
            }
        }
        return is_in_duration;
    }
}
