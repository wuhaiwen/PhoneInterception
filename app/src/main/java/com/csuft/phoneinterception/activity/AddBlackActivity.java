package com.csuft.phoneinterception.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.adapter.ProvinceAdapter;
import com.csuft.phoneinterception.db.DbManager;
import com.csuft.phoneinterception.util.Config;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddBlackActivity extends AppCompatActivity {

    private static final String TAG = "AddBlackActivity";
    @Bind(R.id.add_black_toolbar)
    Toolbar toolbar;

    @Bind(R.id.listView_black_list)
    ListView lv_black_list;

    @Bind(R.id.progressBar_load_province)
    ProgressBar progressBar;

    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> black_data = new ArrayList<>();

    ProvinceAdapter provinceAdapter;

    SQLiteDatabase db;
    DbManager manager = new DbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black);
        ButterKnife.bind(this);
        initToolbar();
        db = manager.getDatabase();
        black_data = (ArrayList<String>) getIntent().getSerializableExtra("black_data");
        new LoadProvinces().start();
        progressBar.setVisibility(View.INVISIBLE);
        initListView();
        IntentFilter filter = new IntentFilter(Config.INSERT_BLACK_LIST_SUCCESS);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        unregisterReceiver(receiver);
    }

    private void initListView() {
        provinceAdapter = new ProvinceAdapter(AddBlackActivity.this, data);
        lv_black_list.setAdapter(provinceAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("添加黑名单");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class LoadProvinces extends Thread {
        @Override
        public void run() {
            super.run();
            Cursor cursor = db.rawQuery("select sname from tcity where icityid<35", null);
            while (cursor.moveToNext()) {
                boolean exist = false;
                String sname = cursor.getString(0);
                for (String str : black_data
                        ) {
                    if (sname.equals(str)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist)
                    data.add(sname);
            }
            cursor.close();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position4", -1);
            //如果添加了白名单，则从列表移除
            data.remove(position);
            initListView();
        }
    };
}
