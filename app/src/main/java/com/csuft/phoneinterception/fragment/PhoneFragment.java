package com.csuft.phoneinterception.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.adapter.RecordAdapter;
import com.csuft.phoneinterception.broadcast.PhoneReceiver;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.mode.PhoneRecord;
import com.csuft.phoneinterception.util.Config;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneFragment extends Fragment {

    private static final String TAG = "PhoneFragment";

    //活动上下文
    Context context;

    TelecomManager telecomManager;

    PhoneReceiver phoneReceiver;
    SQLiteDatabase db;

    @Bind(R.id.listView)
    ListView listView;

    List<PhoneRecord> data;

    RecordAdapter recordAdapter;

    public PhoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();

        IntentFilter filter = new IntentFilter(Config.UPDATE);
        //注册广播
        context.registerReceiver(receiver, filter);
        initListView();
        return view;
    }

    private void initListView() {
        db = new DateBaseHelper(context, "record.db", null, 1).getWritableDatabase();
        data = new ArrayList<>();
        Cursor cursor = db.rawQuery("select number,retreat,date,id from record order by id desc", null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            String retreat = cursor.getString(1);
            String date = cursor.getString(2);
            int id = cursor.getInt(3);
            PhoneRecord record = new PhoneRecord(date,String.valueOf(id), number, retreat);
            data.add(record);
        }
        cursor.close();
        if (db != null) {
            db.close();
        }
//        int size = data.size();
//        List<PhoneRecord> data2 = new ArrayList<>();
//        for(int i = 0;i<data.size();i++){
//            data2.add(data.get(size-1));
//            size--;
//        }
        recordAdapter = new RecordAdapter(data, context);
        listView.setAdapter(recordAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }

    public static class SettingFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //加载xml资源文件

            //addPreferencesFromResource(R.xml.settings);
        }

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接受广播，如果有新拦截，则更新界面
            initListView();
        }
    };
}
