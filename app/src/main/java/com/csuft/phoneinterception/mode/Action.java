package com.csuft.phoneinterception.mode;

/**
 * Created by wuhaiwen on 2016/8/26.
 */
public class Action {
    String setting_name;
    int resource;

    public Action(int resource, String setting_name) {
        this.resource = resource;
        this.setting_name = setting_name;
    }

    public int getResource() {
        return resource;
    }

    public String getSetting_name() {
        return setting_name;
    }
}
