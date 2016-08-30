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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.adapter.WhiteListAdapter;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.mode.Contacts;
import com.csuft.phoneinterception.util.Config;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WhiteListActivity extends AppCompatActivity {


    private static final String TAG = "WhiteListActivity";
    @Bind(R.id.listView2_white_list)
    ListView lv2_white_list;

    @Bind(R.id.add_white_toobar)
    Toolbar toolbar;
    @Bind(R.id.tv_noting)
    TextView tv_noting;

    @Bind(R.id.layout_white_list)
    LinearLayout layout_white;
    SQLiteDatabase db;
    WhiteListAdapter whiteAdapter;

    ArrayList<Contacts> db_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);
        ButterKnife.bind(this);
        initToolbar();
//        initListView();
        Log.d(TAG,"onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        db_data = new ArrayList<>();
        db = new DateBaseHelper(this, "record.db", null, 1).getWritableDatabase();
        Cursor cursor = db.rawQuery("select number,name,id from white_list order by id desc", null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            String name = cursor.getString(1);
            int id = cursor.getInt(2);
            Contacts contacts = new Contacts(name, id, number);
            db_data.add(contacts);
        }
        Log.d("size2",String.valueOf(db_data.size()));
        cursor.close();
        IntentFilter filter = new IntentFilter(Config.DELETE_LIST_SUCCESS);
        registerReceiver(receiver,filter);
        initListView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        Log.d(TAG,"onDestroy");
    }

    private void initListView() {
        whiteAdapter = new WhiteListAdapter(WhiteListActivity.this, db_data);
        lv2_white_list.setAdapter(whiteAdapter);
        if (!db_data.isEmpty()) {
            tv_noting.setVisibility(View.INVISIBLE);
//            Log.d("size", String.valueOf(db_data.size()));
            layout_white.setVisibility(View.VISIBLE);
        }else {
            tv_noting.setVisibility(View.VISIBLE);
        }
    }
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("白名单");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        //装填R.menu.my_menu对应的菜单，并添加到menu中
        inflater.inflate(R.menu.add_white_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_from_contacts:
//                finish();
                Intent intent = new Intent(this,AddWhiteActivity.class);
                intent.putExtra("db_data",db_data);
                startActivity(intent);
                break;
        }
        return true;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position1",-1);
            //如果添加了白名单，则从列表移除
            db_data.remove(position);
            initListView();
//            notify();
        }
    };
}
