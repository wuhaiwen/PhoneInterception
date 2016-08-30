package com.csuft.phoneinterception.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.adapter.BlackListAdapter;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.ToastShow;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlackListActivity extends AppCompatActivity {
    @Bind(R.id.add_white_toobar)
    Toolbar toolbar;
    @Bind(R.id.tv_noting)
    TextView tv_noting;

    @Bind(R.id.listView_black_list)
    ListView Lv_black_list;

    SQLiteDatabase db;

    ArrayList<String> data;

    BlackListAdapter blackListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        ButterKnife.bind(this);
        db =  new DateBaseHelper(this, "record.db", null, 1).getWritableDatabase();;
        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db!=null){
            db.close();
        }
        unregisterReceiver(receiver);
    }

    private void initListView() {
        blackListAdapter = new BlackListAdapter(BlackListActivity.this, data);
        Lv_black_list.setAdapter(blackListAdapter);
        if (!data.isEmpty()) {
            tv_noting.setVisibility(View.INVISIBLE);
//            Log.d("size", String.valueOf(db_data.size()));
        }else {
            tv_noting.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("黑名单");
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
        inflater.inflate(R.menu.add_black_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_province:
//                finish();
                Intent intent = new Intent(this,AddBlackActivity.class);
                intent.putExtra("black_data",data);
                startActivity(intent);
                break;
            case R.id.action_define_black_own:
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.define_black_own,null);
                final EditText black_num = (EditText) linearLayout.findViewById(R.id.editText_black_num);
               new AlertDialog.Builder(this)
                        .setView(linearLayout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String num = black_num.getText().toString().replace(" ","");
                                if(num.length()==3){
                                    num = num+" 开头的号码";
                                }
                                if(!TextUtils.isEmpty(num)) {
                                    boolean is_exist = false;
                                    String sql = "insert into black_list(key) values(" + "'" + num + "'" + ")";
                                    for (String str : data
                                            ) {
                                        if (str.replace(" ", "").equals(num)) {
                                            ToastShow.showToast(BlackListActivity.this, "该名单已存在");
                                            is_exist = true;
                                            break;
                                        }
                                    }
                                    Log.d("gaga", sql);
                                    if (!is_exist) {
                                        try {
                                            db.execSQL(sql);
                                            initView();
                                            ToastShow.showToast(BlackListActivity.this, "添加成功");
                                        } catch (Exception e) {
                                            ToastShow.showToast(BlackListActivity.this, "添加失败");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
                break;
        }
        return true;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position3",-1);
            data.remove(position);
            initListView();
        }
    };

    public void initView(){
        data = new ArrayList<>();
        String sql = "select key from black_list";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            String key = cursor.getString(0);
            data.add(key);
        }
        cursor.close();
        IntentFilter filter = new IntentFilter(Config.DELETE_BLACK_LIST_SUCCESS);
        registerReceiver(receiver,filter);
        initListView();
    }
}
