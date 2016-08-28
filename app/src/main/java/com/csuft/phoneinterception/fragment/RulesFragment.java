package com.csuft.phoneinterception.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.activity.WhiteListActivity;
import com.csuft.phoneinterception.adapter.SettingAdapter;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.ToastShow;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RulesFragment extends Fragment {

    private static final String TAG = "RulesFragment";


    @Bind(R.id.switch_open)
    Switch aSwitch;

    @Bind(R.id.listView_more_setting)
    ListView lv_more_setting;
    @Bind(R.id.listView_list_add)
    ListView listView_list_add;

    //拦截模式
    String msg;
    //拦截模式信息显示
    @Bind(R.id.show_info)
    TextView tv_show_info;

    //设置拦击模式
    SharedPreferences mode_reject_all;
    SharedPreferences mode_reject_black_list;
    SharedPreferences mode_let_white_list;
    SharedPreferences reject_mode_info;
    SharedPreferences mode_let_contacts;

    SharedPreferences.Editor editor1;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    SharedPreferences.Editor editor4;
    SharedPreferences.Editor editor5;

    //定时器等设置布局
    @Bind(R.id.layout_more_setting)
    RelativeLayout layout_more_setting;

    public RulesFragment() {
        // Required empty public constructor
    }

    Context context;

    SettingAdapter settingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rules, container, false);
        ButterKnife.bind(this, v);
        context = getActivity();
        //初始化选项存储
        initPreferences();
        //
        initListView1();
        initListView2();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.IS_OPEN, 0);
        boolean is_open = sharedPreferences.getBoolean(Config.IS_OPEN, false);
        layout_more_setting.setVisibility(is_open? View.VISIBLE:View.INVISIBLE);
        aSwitch.setChecked(is_open);
        msg = reject_mode_info.getString(Config.REJECT_MODE_INFO, "默认全部拦截");
        tv_show_info.setText(msg);
        return v;
    }

    private void initPreferences() {
        mode_reject_all = context.getSharedPreferences(Config.REJECT_ALL, Context.MODE_PRIVATE);
        mode_reject_black_list = context.getSharedPreferences(Config.REJECT_BLACK_LIST, Context.MODE_PRIVATE);
        mode_let_white_list = context.getSharedPreferences(Config.LET_WHITE_LIST, Context.MODE_PRIVATE);
        mode_let_contacts = context.getSharedPreferences(Config.LET_CONTACTS_LIST, Context.MODE_PRIVATE);
        reject_mode_info = context.getSharedPreferences(Config.REJECT_MODE_INFO, Context.MODE_PRIVATE);
        editor1 = mode_reject_all.edit();
        editor2 = mode_reject_black_list.edit();
        editor3 = mode_let_white_list.edit();
        editor4 = reject_mode_info.edit();
        editor5 = mode_let_contacts.edit();
    }

    private void initListView2() {
        final String[] data = {"白名单", "黑名单"};
        settingAdapter = new SettingAdapter(context, data);
        listView_list_add.setAdapter(settingAdapter);
        listView_list_add.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data[position].equals("白名单")) {
                    Intent intent = new Intent(context, WhiteListActivity.class);
                    context.startActivity(intent);
                } else {
                    ToastShow.showToast(context, "黑名单");
                }
            }
        });
    }

    private void initListView1() {
        final String[] data = {"模式设置", "免打扰时段"};
        settingAdapter = new SettingAdapter(context, data);
        lv_more_setting.setAdapter(settingAdapter);
        lv_more_setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, data[position]);
                if (data[position].equals("模式设置")) {
                    final RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.reject_mode, null);
                    final RadioButton rb_reject_all = (RadioButton) layout.findViewById(R.id.rb_reject_all);
                    final RadioButton rb_reject_black = (RadioButton) layout.findViewById(R.id.rb_reject_black);
                    final RadioButton rb_let_white = (RadioButton) layout.findViewById(R.id.rb_let_white);
                    final RadioButton rb_let_contacts = (RadioButton) layout.findViewById(R.id.rb_let_contacts);
                    rb_reject_all.setChecked(mode_reject_all.getBoolean(Config.REJECT_ALL, false));
                    rb_reject_black.setChecked(mode_reject_black_list.getBoolean(Config.REJECT_BLACK_LIST, false));
                    rb_let_white.setChecked(mode_let_white_list.getBoolean(Config.LET_WHITE_LIST, false));
                    rb_let_contacts.setChecked(mode_let_contacts.getBoolean(Config.LET_CONTACTS_LIST, false));
                    new AlertDialog.Builder(context)
                            .setTitle("请选择模式")
                            .setView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    editor1.putBoolean(Config.REJECT_ALL,rb_reject_all.isChecked()? true:false);
                                    if (rb_reject_all.isChecked()) {
                                        editor1.putBoolean(Config.REJECT_ALL, true);
                                        msg = "(全部拦截)";
                                    } else
                                        editor1.putBoolean(Config.REJECT_ALL, false);
                                    if (rb_reject_black.isChecked()) {
                                        editor2.putBoolean(Config.REJECT_BLACK_LIST, true);
                                        msg = "(拦截黑名单)";
                                    } else
                                        editor2.putBoolean(Config.REJECT_BLACK_LIST, false);
                                    if (rb_let_white.isChecked()) {
                                        editor3.putBoolean(Config.LET_WHITE_LIST, true);
                                        msg = "(只放行白名单)";
                                    }  else
                                        editor3.putBoolean(Config.LET_WHITE_LIST, false);
                                    if (rb_let_contacts.isChecked()) {
                                        editor5.putBoolean(Config.LET_CONTACTS_LIST, true);
                                        msg = "(放行联系人)";
                                    }else
                                        editor5.putBoolean(Config.LET_CONTACTS_LIST, false);

                                    //在设置这一栏显示拦截模式信息
                                    tv_show_info.setText(msg);
                                    editor4.putString(Config.REJECT_MODE_INFO, msg);
                                    editor1.commit();
                                    editor2.commit();
                                    editor3.commit();
                                    editor4.commit();
                                    editor5.commit();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .create()
                            .show();
                } else {
                    ToastShow.showToast(context, "点击了");
                }
            }
        });
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = context.getSharedPreferences(Config.IS_OPEN, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (aSwitch.isChecked()) {
                    tv_show_info.setVisibility(View.VISIBLE);
                    editor.putBoolean(Config.IS_OPEN, true);
                    editor.commit();
                    Toast.makeText(getActivity(), "拦截已经开启", Toast.LENGTH_SHORT).show();
                    layout_more_setting.setVisibility(View.VISIBLE);
                } else {
                    tv_show_info.setVisibility(View.INVISIBLE);
                    layout_more_setting.setVisibility(View.INVISIBLE);
                    editor.putBoolean(Config.IS_OPEN, false);
                    editor.commit();
                }
            }
        });
    }


}
