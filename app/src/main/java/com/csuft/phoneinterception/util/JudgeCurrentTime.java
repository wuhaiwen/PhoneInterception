package com.csuft.phoneinterception.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wuhaiwen on 2016/8/30.
 */
public class JudgeCurrentTime {

    public static boolean isDuration(String duration_info_str) throws Exception {
        boolean is_in_duration = false;
        int length = duration_info_str.length();
        String str1 = duration_info_str.substring(0, duration_info_str.indexOf("-"));
        String str2 = duration_info_str.substring(duration_info_str.indexOf("-") + 2, length);
        System.out.println(str1 + " " + str2);
        String[] dds1 = new String[] {};
        String[] dds2 = new String[] {};

        // 分取系统时间 小时分
        dds1 = str1.split(":");
        dds2 = str2.split(":");

        int start_hour = Integer.parseInt(dds1[0]);
        int start_minute = Integer.parseInt(dds1[1]);
        int end_hour = Integer.parseInt(dds2[0]);
        int end_minute = Integer.parseInt(dds2[1]);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd");
        String current_str = simpleDateFormat1.format(new Date(System.currentTimeMillis()));
        String next_day = simpleDateFormat1.format(new Date(System.currentTimeMillis()+60*60*24*1000));
        System.out.println(current_str+" "+next_day);

        long start = simpleDateFormat.parse(current_str+" "+ str1).getTime();
        long end = simpleDateFormat.parse(current_str+" "+ str2).getTime();
        long current = System.currentTimeMillis();
        System.out.println(start+" "+end+" "+current);
        long end2 = simpleDateFormat.parse(next_day+" " + str2).getTime();
        System.err.println(end+" "+end2);
        if (start_hour < end_hour) {
            // 当前是同一天
            if(start<=current&&current<=end){
                is_in_duration = true;
            }else {
                is_in_duration=  false;
            }
        } else if(start_hour>end_hour){
            //当前不是同一天
            if(start<=current&&current<=end2){
                is_in_duration = true;
            }else {
                is_in_duration = false;
            }
        }else {
            //当前不是同一天且小时相等，	如果开始的分钟数大于结束的分钟数
            if(start_minute>end_minute){
                if(start<=current&&current<=end2){
                    is_in_duration = true;
                }else {
                    is_in_duration = false;
                }
            }else if (start_minute<end_minute) {
                if(start<=current&&current<=end){
                    is_in_duration = true;
                }else {
                    is_in_duration=  false;
                }
            }else {
                //如果两个时间都相等，就是一整天都在时间段内
                is_in_duration = true;
            }
        }
        return is_in_duration;
    }
}
