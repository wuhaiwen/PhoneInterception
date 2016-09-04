package com.csuft.phoneinterception.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.activity.MainActivity;
import com.csuft.phoneinterception.contentutil.ContactUtil;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.db.DbManager;
import com.csuft.phoneinterception.db.OperateDbHelper;
import com.csuft.phoneinterception.mode.Contacts;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.JudgeCurrentTime;
import com.csuft.phoneinterception.util.PhoneUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = "message";
    private static final String ACTION = "android.intent.action.PHONE_STATE";

    SQLiteDatabase db;
    SQLiteDatabase city_db;
    DbManager dbManager;

    String guishudi = null;

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

    //联系人列表
    ArrayList<Contacts> data_contacts;
    //免打扰模式是否打开
    boolean is_open_no_disturb;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String phoneNumber = intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        String action = intent.getAction();
//        Log.d(TAG,action);
        init(context);
        if (action.equals(ACTION)) {
            //先判断拦截是否已经打开
            if (is_open) {
                Log.i(TAG, "开启");
                //判断免打扰是否开启
                if (is_open_no_disturb) {
                    Log.d("zuoyexifengdiaobishu", String.valueOf(judgeInDuration(context)));
                    //判断当前时间是否处于免打扰时间段
                    if (judgeInDuration(context)) {
                        //如果处于该时间段，则调用挂断
                        try {
                            doJudge(context, intent, phoneNumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                } else {
                    //如果没有打开，则按照定义拦截的拦截模式进行拦截
                    try {
                        doJudge(context, intent, phoneNumber);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void init(Context context) {
        db = (new DateBaseHelper(context, "record.db", null, 1)).getWritableDatabase();
        SharedPreferences sharedPreferences1 = context.getSharedPreferences(Config.IS_OPEN, 0);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences(Config.LET_WHITE_LIST, 0);
        SharedPreferences sharedPreferences3 = context.getSharedPreferences(Config.REJECT_BLACK_LIST, 0);
        SharedPreferences sharedPreferences4 = context.getSharedPreferences(Config.REJECT_ALL, 0);
        SharedPreferences sharedPreferences5 = context.getSharedPreferences(Config.LET_CONTACTS_LIST, 0);
        SharedPreferences no_disturb = context.getSharedPreferences(Config.NO_DISTURB, 0);
        //
        is_open = sharedPreferences1.getBoolean(Config.IS_OPEN, false);
        let_white = sharedPreferences2.getBoolean(Config.LET_WHITE_LIST, false);
        reject_black = sharedPreferences3.getBoolean(Config.REJECT_BLACK_LIST, false);
        reject_all = sharedPreferences4.getBoolean(Config.REJECT_ALL, false);
        let_contacts = sharedPreferences5.getBoolean(Config.LET_CONTACTS_LIST, false);
        is_open_no_disturb = no_disturb.getBoolean(Config.NO_DISTURB, false);

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
                    iTelephony.endCall();//自动挂断电话
//                    Log.d(TAG, phoneNumber + dateString);
                    data_contacts = new ArrayList<>();
                    //标志该号码是否来自联系人
                    data_contacts = ContactUtil.getData(context);
                    boolean is_from_contacts = false;
                    for (Contacts c :
                            data_contacts) {
                        String str = c.getNumber().replace(" ", "");
                        if (phoneNumber.equals(str)) {
                            Log.d(TAG, str + " " + phoneNumber);
                            phoneNumber = phoneNumber + "(" + c.getContacts_name() + ")";
                            is_from_contacts = true;
                            break;
                        }
                    }
                    if (!is_from_contacts)
                        phoneNumber = phoneNumber + "(未知)";
//                    iTelephony.answerRingingCall();//自动接通电话
                    guishudi = guishudi == null ? "未知归属地" : guishudi;
//                    Toast.makeText(context, guishudi, Toast.LENGTH_SHORT).show();
                    String sql = "insert into record (number,retreat,date) values " +
                            "(" + "'" + phoneNumber + "'," + "'" + guishudi + "'," + "'" + dateString + "'" + ");";
                    db.execSQL(sql);
                    //插入成功则发送广播更新界面
                    Intent intent1 = new Intent(Config.UPDATE);
                    context.sendBroadcast(intent1);
                    //发送拦截消息到通知栏
                    SendNotification(context, phoneNumber, guishudi);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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
        builder.setSmallIcon(R.drawable.reject)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.reject))
                .setContentTitle("新拦截")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(num + " " + retreat);
        Notification notification = builder.build();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context, MainActivity.class));//用ComponentName得到class对象
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);//将经过设置了的Intent绑定给PendingIntent
        notification.contentIntent = contentIntent;// 通知绑定 PendingIntent
        notification.flags = Notification.FLAG_AUTO_CANCEL;//设置自动取消
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.mID, notification);
    }

    public void getGuiShudi(Context context, String phoneNumber) {
        dbManager = new DbManager(context);
        city_db = dbManager.getDatabase1();
        try {
            Log.d("hah", phoneNumber);
            String str = phoneNumber.substring(0, 7);
            String sql2 = "select area_name from phone_view where phone_number=" + str;
            Log.d("hah", phoneNumber.substring(0, 7));
            Cursor cursor_city = OperateDbHelper.getData(city_db, sql2);
            while (cursor_city.moveToNext()) {
                guishudi = cursor_city.getString(0);
            }
            Log.d("hah", guishudi);
            cursor_city.close();
            city_db.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public boolean judgeInDuration(Context context) {
        boolean in_duration = false;
        SharedPreferences duration_info = context.getSharedPreferences(Config.DISTURB_INFO, 0);
        String duration_info_str = duration_info.getString(Config.DISTURB_INFO, "");
        int length = duration_info_str.length();
        if (length > 0) {
            try {
                in_duration = JudgeCurrentTime.isDuration(duration_info_str);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return in_duration;
    }

    public void doJudge(Context context, Intent intent, String phoneNumber) {
        String sql = "select number from white_list";
        //获得电话号码
        getGuiShudi(context, phoneNumber);
        if (let_white) {
            //白名单模式
            boolean is_white = false;
            Log.d(TAG, "白");
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                number = number.replace(" ", "");
                if (phoneNumber.equals(number)) {
                    is_white = true;
                    break;
                }
            }
            if (!is_white)
                doReceivePhone(context, intent);
        } else if (reject_black) {
            //黑名单模式
            boolean is_from_black;
            sql = "select key from black_list";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String key = cursor.getString(0);
                key = key.replace(" ", "");
                //由于城市数据库跟归属地数据库存在差异，先格式化一下
                String province;
                if (key.substring(0, 3).equals("黑龙江")) {
                    province = "黑龙江";
                } else if (key.substring(0, 3).equals("内蒙古")) {
                    province = "内蒙古";
                } else {
                    province = key.substring(0, 2);
                }
                String key3 = key.replace(" ", "").substring(0, 3);
                String num3 = phoneNumber.substring(0, 3);
                if (phoneNumber.equals(key)) {
                    //首先匹配号码
                    is_from_black = true;
                } else if (num3.equals(key3)) {
                    //如果号码不匹配，在匹配开头的运营商字段
                    is_from_black = true;
                } else if (guishudi.substring(0, guishudi.indexOf(" ")).equals(province)) {
                    Log.d(TAG, guishudi.substring(0, guishudi.indexOf(" ")));
                    //匹配城市黑名单
                    is_from_black = true;
                } else {
                    //没有匹配
                    is_from_black = false;
                }
                Log.d(TAG, key.replace(" ", "").substring(0, 3) + " " + phoneNumber);
                if (is_from_black) {
                    Log.d(TAG, "拦截黑名单");
                    doReceivePhone(context, intent);
                    break;
                }
            }
            cursor.close();
        } else if (reject_all) {
            Log.i(TAG, "全部1");
            doReceivePhone(context, intent);
        } else if (let_contacts) {
            //是否放行联系人模式
            data_contacts = new ArrayList<>();
            //标志该号码是否来自联系人
            boolean is_contacts = false;
            data_contacts = ContactUtil.getData(context);
            for (Contacts c :
                    data_contacts) {
                String str = c.getNumber().replace(" ", "");
                if (phoneNumber.equals(str)) {
                    Log.d(TAG, str + " " + phoneNumber);
                    is_contacts = true;
                    break;
                }
            }

            //如果不是来自联系人，则挂断
            if (!is_contacts)
                doReceivePhone(context, intent);
        } else {
            Log.i(TAG, "全部");
            //用于首次使用该应用，如果开启拦截且没有设置拦截模式，则会挂断所有电话
            doReceivePhone(context, intent);
        }
    }
}
