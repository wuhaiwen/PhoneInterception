package com.csuft.phoneinterception.util;

/**
 * Created by wuhaiwen on 2016/8/22.
 */
public class Config {

    public static final String  MY_PHONE="ACTION_PHONE_COMING";
    public static final String PHONE_STATE ="TelephonyManager.ACTION_PHONE_STATE_CHANGED";
    //拦截是否开关是否打开
    public static final String IS_OPEN ="is_open";
    //取到拦截记录并插入数据库成功
    public static final String UPDATE ="success";
    //拦截所有
    public static final String REJECT_ALL ="reject_all";
    //拦截黑名单
    public static final String REJECT_BLACK_LIST ="reject_black_list";
    //放行白名单
    public static final String LET_WHITE_LIST ="let_white_list";
    //放行联系人
    public static final String LET_CONTACTS_LIST ="let_contacts_list";


    public static int mID = 8;

    //联系人列表
    public static final String CONTACTS_LIST ="contacts_list";
    //拦截模式显示
    public static final String REJECT_MODE_INFO ="reject_mode_info";
    //插入白名单完成
    public static final String INSERT_LIST_SUCCESS ="insert_list_success";
    public static final String DELETE_LIST_SUCCESS ="delete_list_success";
    //插入黑名单完成
    public static final String INSERT_BLACK_LIST_SUCCESS ="insert_black_list_success";
    public static final String DELETE_BLACK_LIST_SUCCESS ="delete_black_list_success";
    //自定义短信内容
    public static final String DEFINE_MSG_CONTENT ="define_msg_content";
}
