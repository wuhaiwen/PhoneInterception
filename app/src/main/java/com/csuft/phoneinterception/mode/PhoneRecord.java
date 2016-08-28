package com.csuft.phoneinterception.mode;

/**
 * Created by wuhaiwen on 2016/8/23.
 */
public class PhoneRecord {
    String number;
    String retreat;
    String date;
    String id;

    public PhoneRecord(String date, String id, String number, String retreat) {
        this.date = date;
        this.id = id;
        this.number = number;
        this.retreat = retreat;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getRetreat() {
        return retreat;
    }
}
