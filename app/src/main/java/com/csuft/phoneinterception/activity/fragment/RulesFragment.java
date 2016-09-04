package com.csuft.phoneinterception.activity.fragment;


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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.activity.BlackListActivity;
import com.csuft.phoneinterception.activity.WhiteListActivity;
import com.csuft.phoneinterception.adapter.SettingAdapter;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.ToastShow;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RulesFragment extends Fragment implements Formatter {

    private static final String TAG = "RulesFragment";


    @Bind(R.id.switch_open)
    Switch aSwitch;

    Switch Switch_no_disturb;

    @Bind(R.id.listView_more_setting)
    ListView lv_more_setting;
    @Bind(R.id.listView_list_add)
    ListView listView_list_add;

    //拦截模式
    String msg;
    //拦截模式信息显示
    @Bind(R.id.show_info)
    TextView tv_show_info;

    //免打扰
    @Bind(R.id.tv_show_duration_info)
    TextView tv_show_duration_info;

    @Bind(R.id.linearLayout_no_disturb)
    LinearLayout linearLayout;

    //
    @Bind(R.id.tv_define_msg_content)
    TextView tv_define_msg_content;

    NumberPicker hour_from;
    NumberPicker hour_to;
    NumberPicker minute_from;
    NumberPicker minute_to;

    //短信内容
    String define_msg_content_str;

    String duration_info_str;

    //设置拦击模式
    SharedPreferences mode_reject_all;
    SharedPreferences mode_reject_black_list;
    SharedPreferences mode_let_white_list;
    SharedPreferences reject_mode_info;
    SharedPreferences mode_let_contacts;
    SharedPreferences define_msg_content;
    SharedPreferences duration_info;
    SharedPreferences no_disturb;

    SharedPreferences.Editor editor1;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    SharedPreferences.Editor editor4;
    SharedPreferences.Editor editor5;
    SharedPreferences.Editor editor6;


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
        aSwitch.setChecked(is_open);
        msg = reject_mode_info.getString(Config.REJECT_MODE_INFO, "(默认全部拦截)");
        define_msg_content_str = define_msg_content.getString(Config.DEFINE_MSG_CONTENT, "你好，我现在不方便接电话，等下打给你");
        tv_define_msg_content.setText("(短信内容：" + define_msg_content_str + ")");
        tv_show_info.setText(msg);
        tv_show_info.setVisibility(is_open? View.VISIBLE:View.INVISIBLE);
        linearLayout.setVisibility(is_open? View.VISIBLE:View.INVISIBLE);
        return v;
    }

    private void initPreferences() {
        duration_info = context.getSharedPreferences(Config.DISTURB_INFO, 0);
        mode_reject_all = context.getSharedPreferences(Config.REJECT_ALL, Context.MODE_PRIVATE);
        mode_reject_black_list = context.getSharedPreferences(Config.REJECT_BLACK_LIST, Context.MODE_PRIVATE);
        mode_let_white_list = context.getSharedPreferences(Config.LET_WHITE_LIST, Context.MODE_PRIVATE);
        mode_let_contacts = context.getSharedPreferences(Config.LET_CONTACTS_LIST, Context.MODE_PRIVATE);
        reject_mode_info = context.getSharedPreferences(Config.REJECT_MODE_INFO, Context.MODE_PRIVATE);
        define_msg_content = context.getSharedPreferences(Config.DEFINE_MSG_CONTENT, Context.MODE_PRIVATE);
        no_disturb = context.getSharedPreferences(Config.NO_DISTURB, 0);
        editor1 = mode_reject_all.edit();
        editor2 = mode_reject_black_list.edit();
        editor3 = mode_let_white_list.edit();
        editor4 = reject_mode_info.edit();
        editor5 = mode_let_contacts.edit();
        editor6 = define_msg_content.edit();
    }

    private void initListView2() {
        final String[] data = {"白名单", "黑名单", "短信设置"};
        settingAdapter = new SettingAdapter(context, data);
        listView_list_add.setAdapter(settingAdapter);
        listView_list_add.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data[position].equals("白名单")) {
                    Intent intent = new Intent(context, WhiteListActivity.class);
                    context.startActivity(intent);
                } else if (data[position].equals("黑名单")) {
                    Intent intent = new Intent(context, BlackListActivity.class);
                    context.startActivity(intent);
                } else {
                    LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.define_msg_content_mode, null);
                    final EditText message_content = (EditText) linearLayout.findViewById(R.id.editText_define_msg_content);
                    new AlertDialog.Builder(context)
                            .setView(linearLayout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String content = message_content.getText().toString().replace(" ", "");
                                    if (content.length() > 0) {
                                        editor6.putString(Config.DEFINE_MSG_CONTENT, content);
                                        editor6.commit();
                                        tv_define_msg_content.setText("(短信内容：" + content + ")");
                                        ToastShow.showToast(context, "修改成功");
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                }
            }
        });
    }

    private void initListView1() {
        if(no_disturb.getBoolean(Config.NO_DISTURB,false)){
            duration_info_str = duration_info.getString(Config.DISTURB_INFO, "");
            tv_show_duration_info.setText("(免打扰已经开启 " + duration_info_str+")");
        }else {
            tv_show_duration_info.setText("(免打扰已关闭)");
        }
        final String[] data = {"模式设置", "免打扰时段"};
        settingAdapter = new SettingAdapter(context, data);
        lv_more_setting.setAdapter(settingAdapter);
        lv_more_setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                                    } else
                                        editor3.putBoolean(Config.LET_WHITE_LIST, false);
                                    if (rb_let_contacts.isChecked()) {
                                        editor5.putBoolean(Config.LET_CONTACTS_LIST, true);
                                        msg = "(放行联系人)";
                                    } else
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
                    final SharedPreferences.Editor editor1 = no_disturb.edit();
                    final SharedPreferences.Editor editor2 = duration_info.edit();
                    final boolean is_open = no_disturb.getBoolean(Config.NO_DISTURB, false);
                    final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.duration_mode, null);
                    hour_from = (NumberPicker) layout.findViewById(R.id.hour_picker_from);
                    hour_to = (NumberPicker) layout.findViewById(R.id.hour_picker_to);
                    minute_from = (NumberPicker) layout.findViewById(R.id.minute_picker_from);
                    minute_to = (NumberPicker) layout.findViewById(R.id.minute_picker_to);
                    Switch_no_disturb = (Switch) layout.findViewById(R.id.switch_open_no_disturb);
                    Switch_no_disturb.setChecked(is_open);
                    init();
                    new AlertDialog.Builder(context)
                            .setTitle("请设置相关信息")
                            .setView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Switch_no_disturb.isChecked()) {
                                        duration_info_str = format(hour_from.getValue()) + ":" +
                                                format(minute_from.getValue()) + "--"+
                                                format(hour_to.getValue()) + ":" +
                                                format(minute_to.getValue());
                                        tv_show_duration_info.setText("(免打扰已经开启 " + duration_info_str + ")");
                                        editor1.putBoolean(Config.NO_DISTURB, true);
                                        editor2.putString(Config.DISTURB_INFO, duration_info_str);
                                        Log.d(TAG, hour_from.getValue() + "");
                                    } else {
                                        tv_show_duration_info.setText("(免打扰已关闭)");
                                        editor1.putBoolean(Config.NO_DISTURB, false);
                                    }
                                    editor1.commit();
                                    editor2.commit();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();

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
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                    tv_show_info.setVisibility(View.INVISIBLE);
//                    layout_more_setting.setVisibility(View.INVISIBLE);
//                    linearLayout.setVisibility(View.INVISIBLE);
                    editor.putBoolean(Config.IS_OPEN, false);
                    editor.commit();
                }
            }
        });
    }

//    //解决scrollView和listView冲突问题
//    public void setListViewHeightBasedOnChildren(ListView listView) {
//        // 获取ListView对应的Adapter
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
//            // listAdapter.getCount()返回数据项的数目
//            View listItem = listAdapter.getView(i, null, listView);
//            // 计算子项View 的宽高
//            listItem.measure(0, 0);
//            // 统计所有子项的总高度
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        // listView.getDividerHeight()获取子项间分隔符占用的高度
//        // params.height最后得到整个ListView完整显示需要的高度
//        listView.setLayoutParams(params);
//    }

    public void init() {
        hour_from.setFormatter(this);
        hour_from.setMaxValue(23);
        hour_from.setMinValue(0);
        hour_from.setValue(10);

        minute_from.setFormatter(this);
        minute_from.setMaxValue(59);
        minute_from.setMinValue(0);
        minute_from.setValue(0);

        hour_to.setFormatter(this);
        hour_to.setMaxValue(23);
        hour_to.setMinValue(0);
        hour_to.setValue(10);

        minute_to.setFormatter(this);
        minute_to.setMaxValue(59);
        minute_to.setMinValue(0);
        minute_to.setValue(0);
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }
}
