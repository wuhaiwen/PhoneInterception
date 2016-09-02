package com.csuft.phoneinterception.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.mode.PhoneRecord;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.ToastShow;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wuhaiwen on 2016/8/23.
 */
public class RecordAdapter extends BaseAdapter {

    List<PhoneRecord> data;
    Context context;
    LayoutInflater inflater;
    SQLiteDatabase dateBaseHelper;

    public RecordAdapter(List<PhoneRecord> data, Context context) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.record_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PhoneRecord phoneRecord = data.get(position);
        ButtonListener listener = new ButtonListener();
        //获得当前项
        listener.setPosition(position);
        //给按钮注册监听器
        viewHolder.BindDate(phoneRecord);
        viewHolder.action_more.setOnClickListener(listener);
        return convertView;
    }


    public static class ViewHolder {

        @Bind(R.id.number)
        TextView number;
        @Bind(R.id.retreat)
        TextView retreat;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.more_action_operation)
        ImageButton action_more;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void BindDate(PhoneRecord phoneRecord) {
            number.setText(phoneRecord.getNumber());
            retreat.setText(phoneRecord.getRetreat());
            date.setText(phoneRecord.getDate());
        }

    }

    class ButtonListener implements ImageButton.OnClickListener {
        int position;

        private void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            dateBaseHelper = new DateBaseHelper(context, "record.db", null, 1).getWritableDatabase();
            PopupMenu menu = new PopupMenu(context, v);
            menu.inflate(R.menu.button_menu);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_dial_back:
                            //获得电话号码然后用意图启动拨号界面，不会直接拨出去
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + data.get(position).getNumber()));
                            context.startActivity(intent);
                            break;
                        case R.id.action_add_black_list:
                            String num_info = data.get(position).getNumber();
                            String num = num_info.substring(0,num_info.indexOf("("));
//                            ToastShow.showToast(context, num);
                            boolean is_exist = false;
                            String sql2 = "insert into black_list(key) values(" + "'" + num + "'" + ")";
                            String sql1 = "select key from black_list";
                            Cursor cursor = dateBaseHelper.rawQuery(sql1, null);
                            while (cursor.moveToNext()) {
                                String key = cursor.getString(0);
                                if (key.replace(" ", "").equals(num)) {
                                    ToastShow.showToast(context, "该名单已存在");
                                    is_exist = true;
                                    break;
                                }
                            }
                            if (!is_exist) {
                                try {
                                    dateBaseHelper.execSQL(sql2);
                                    ToastShow.showToast(context, "添加成功");
                                } catch (Exception e) {
                                    ToastShow.showToast(context, "添加失败");
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case R.id.action_delete:
                            try {
                                String sql = "delete from record where id=" + Integer.parseInt(data.get(position).getId()) + ";";
                                Log.d("gaga", sql);
                                dateBaseHelper.execSQL(sql);
                                //删除成功后发送一个广播给fragment用来更新界面
                                Intent intent1 = new Intent(Config.UPDATE);
                                ToastShow.showToast(context, "已删除");
                                context.sendBroadcast(intent1);
                                if (dateBaseHelper != null) {
                                    dateBaseHelper.close();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                dateBaseHelper.close();
                            }
                            break;
                        case R.id.action_add_contacts_list:
                            Intent intent2 = new Intent();
                            intent2.setAction(Intent.ACTION_INSERT_OR_EDIT);
                            intent2.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                            intent2.putExtra(ContactsContract.Intents.Insert.PHONE, data.get(position).getNumber());
                            context.startActivity(intent2);
                            break;
                        case R.id.action_send_message:
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Config.DEFINE_MSG_CONTENT, Context.MODE_PRIVATE);
                            String msg = sharedPreferences.getString(Config.DEFINE_MSG_CONTENT, "你好，我现在不方便接电话，等下打给你");
                            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.send_message_mode, null);
                            final EditText message_content = (EditText) linearLayout.findViewById(R.id.et_message_content);
                            message_content.setText(msg);
                            TextView accept_number = (TextView) linearLayout.findViewById(R.id.accept_number);
                            final String number = data.get(position).getNumber()
                                    .substring(0,data.get(position).getNumber().indexOf("("));
                            accept_number.setText("对方:" + number);
                            Log.d("gaga",number);
                            new AlertDialog.Builder(context)
                                    .setView(linearLayout)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String content = message_content.getText().toString();
                                            SmsManager manager = SmsManager.getDefault();
                                            PendingIntent pendingIntent = PendingIntent.getActivity(
                                                    context,
                                                    0,
                                                    new Intent(),
                                                    0
                                            );
                                            if (content.length() > 0){
                                                manager.sendTextMessage(
                                                       number,
                                                        null,
                                                        content,
                                                        pendingIntent,
                                                        null
                                                );
                                                ToastShow.showToast(context, "发送成功");
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();
                            break;

                    }
                    return true;
                }
            });
            menu.show();
//            if(dateBaseHelper!=null){
//                dateBaseHelper.close();
//            }
//            ToastShow.showToast(context, "点击了gaga");
        }
    }

}
