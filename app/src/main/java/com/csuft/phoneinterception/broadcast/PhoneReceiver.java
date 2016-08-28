package com.csuft.phoneinterception.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.db.OperateDbHelper;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.PhoneUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = "message";
    private static final String ACTION = "android.intent.action.PHONE_STATE";


    SQLiteDatabase db;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        db = (new DateBaseHelper(context, "record.db", null, 1)).getWritableDatabase();
        List<String> numberList = new ArrayList<>();
        String sql = "select number from white_list";
        //拦截开关是否打开
        boolean is_open = false;
        //放行白名单
        boolean let_white = false;
        //拦截黑名单
        boolean reject_black = false;
        //全部拦截
        boolean reject_all = false;
        //放行联系人
        boolean let_contacts = false;
        SharedPreferences sharedPreferences1 = context.getSharedPreferences(Config.IS_OPEN, 0);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences(Config.LET_WHITE_LIST, 0);
        SharedPreferences sharedPreferences3 = context.getSharedPreferences(Config.REJECT_BLACK_LIST, 0);
        SharedPreferences sharedPreferences4 = context.getSharedPreferences(Config.REJECT_ALL, 0);
        SharedPreferences sharedPreferences5 = context.getSharedPreferences(Config.LET_CONTACTS_LIST, 0);
        is_open = sharedPreferences1.getBoolean(Config.IS_OPEN, false);
        //放行白名单
        let_white = sharedPreferences2.getBoolean(Config.LET_WHITE_LIST, false);
        reject_black = sharedPreferences3.getBoolean(Config.REJECT_BLACK_LIST, false);
        reject_all = sharedPreferences4.getBoolean(Config.REJECT_ALL, false);
        let_contacts = sharedPreferences5.getBoolean(Config.LET_CONTACTS_LIST, false);
        Log.d(TAG, String.valueOf(let_white + " " + reject_black + " " + let_contacts + " " + reject_all));
        String action = intent.getAction();
        //获得电话号码
        String phoneNumber = intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (action.equals(ACTION)) {
            boolean is_white = false;
            if (is_open) {
                if (is_open && let_white) {
                    Log.d(TAG, "白");
                    Cursor cursor = OperateDbHelper.getData(db, sql);
                    while (cursor.moveToNext()) {
                        String number = cursor.getString(0);
                        number = number.replace(" ","");
                        Log.d(TAG, number+" "+phoneNumber);
                        numberList.add(number);
                    }
                    cursor.close();
                    for (String str :
                            numberList) {
                        Log.d(TAG, str+" "+phoneNumber);
                        if (phoneNumber.equals(str)) {
                            is_white = true;
                            break;
                        }
                    }
                    if (!is_white)
                        doReceivePhone(context, intent);
                } else if (is_open && reject_black) {
                    Log.d(TAG, "黑");
                } else if (is_open && reject_all) {
                    Log.d(TAG, "全部");
                    doReceivePhone(context, intent);
                } else if (is_open && let_contacts) {
                    Log.d(TAG, "联系人");
                } else {
                    Log.d(TAG, "默认");
                    doReceivePhone(context, intent);
                }
            } else {
//                doReceivePhone(context, intent);
                return;
            }
        }
    }

    public void doReceivePhone(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(TAG, "[Broadcast]等待接电话=" + phoneNumber);
                try {
                    ITelephony iTelephony = PhoneUtils.getITelephony(telephony);
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM月dd号 HH:mm");
                    String dateString = simpleDateFormat1.format(date);
                    Toast.makeText(context, phoneNumber + " " + dateString, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, phoneNumber + dateString);
//                    iTelephony.answerRingingCall();//自动接通电话
                    String sql = "insert into record (number,retreat,date) values " +
                            "(" + "'" + phoneNumber + "'," + "'" + "未知归属地" + "'," + "'" + dateString + "'" + ");";
                    db.execSQL(sql);
                    if (db != null) {
                        db.close();
                    }
                    iTelephony.endCall();//自动挂断电话
                    //插入成功则发送广播更新界面
                    Intent intent1 = new Intent(Config.UPDATE);
                    context.sendBroadcast(intent1);
                    //发送拦截消息到通知栏
                    SendNotification(context, phoneNumber, "未知归属地");
                } catch (Exception e) {
                    Log.e(TAG, "[Broadcast]Exception=" + e.getMessage(), e);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(TAG, "挂断=" + phoneNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(TAG, "通话中=" + phoneNumber);
                break;
        }
    }

    public void SendNotification(Context context, String num, String retreat) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_phone_missed_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.reject))
                .setContentTitle("新拦截")
                .setContentText(num + " " + retreat);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.mID, notification);
    }
}