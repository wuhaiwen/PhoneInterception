package com.csuft.phoneinterception.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.adapter.ContactsAdapter;
import com.csuft.phoneinterception.mode.Contacts;
import com.csuft.phoneinterception.services.ContactsService;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.ToastShow;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddWhiteActivity extends AppCompatActivity {

    private static final String TAG = "AddWhiteActivity";
    @Bind(R.id.add_white_toobar)
    Toolbar toolbar;

    @Bind(R.id.listView_white_list)
    ListView lv_white_list;

    ArrayList<Contacts> data = new ArrayList<>();

    ContactsAdapter contactsAdapter;

    Intent contacts_intent;
    ArrayList<Contacts> db_data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_white);
        ButterKnife.bind(this);
        initToolbar();
//        initListView();
        Intent intent = getIntent();
        db_data = (ArrayList<Contacts>) intent.getSerializableExtra("db_data");
        contacts_intent = new Intent(this, ContactsService.class);
        startService(contacts_intent);
        IntentFilter filter = new IntentFilter(Config.CONTACTS_LIST);
        registerReceiver(contactsReceiver, filter);
        IntentFilter filter2 = new IntentFilter(Config.INSERT_LIST_SUCCESS);
        registerReceiver(receiver, filter2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initListView() {
        contactsAdapter = new ContactsAdapter(AddWhiteActivity.this, data);
        lv_white_list.setAdapter(contactsAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("添加白名单");
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


    BroadcastReceiver contactsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Contacts> contactsArrayList = (ArrayList<Contacts>) intent.getSerializableExtra(Config.CONTACTS_LIST);
            if (contactsArrayList.isEmpty()) {
                ToastShow.showToast(AddWhiteActivity.this, "还没有联系人");
            } else {
                for (int i = 0; i < db_data.size(); i++) {
                    for (int j = 0; j < contactsArrayList.size(); j++) {
                        if (db_data.get(i).getId() == contactsArrayList.get(j).getId()) {
                            contactsArrayList.remove(j);
                        }
                    }
                }
                data = contactsArrayList;
                initListView();
            }
            context.stopService(contacts_intent);
            context.unregisterReceiver(contactsReceiver);
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", -1);
            //如果添加了白名单，则从列表移除
            data.remove(position);
            initListView();
        }
    };
}
