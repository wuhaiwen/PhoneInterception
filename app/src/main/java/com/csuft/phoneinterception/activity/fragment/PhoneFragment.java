package com.csuft.phoneinterception.activity.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.adapter.RecordAdapter;
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

    SQLiteDatabase db;

    @Bind(R.id.listView)
    ListView listView;

    @Bind(R.id.button_delete_all)
    Button btn_delete_all;

    @Bind(R.id.textView_nothing)
    TextView noting;

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
        btn_delete_all.setOnClickListener(new ButtonDeleteListener());
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
            PhoneRecord record = new PhoneRecord(date, String.valueOf(id), number, retreat);
            data.add(record);
        }
        cursor.close();
        if (!data.isEmpty()) {
            btn_delete_all.setVisibility(View.VISIBLE);
            noting.setVisibility(View.INVISIBLE);
        } else {
            btn_delete_all.setVisibility(View.INVISIBLE);
            noting.setVisibility(View.VISIBLE);
        }
        recordAdapter = new RecordAdapter(data, context);
        listView.setAdapter(recordAdapter);

    }

    class ButtonDeleteListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "确定全部删除?", Snackbar.LENGTH_LONG)
                    .setAction(
                            "确定",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        String sql = "delete from record where id>0";
                                        db.execSQL(sql);
                                        initListView();
                                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    )
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initListView();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if(db!=null){
            db.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接受广播，如果有新拦截，则更新界面
            initListView();
        }
    };
}
