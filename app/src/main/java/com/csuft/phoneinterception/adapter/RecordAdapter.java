package com.csuft.phoneinterception.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
        this.context =  context;
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
                            intent.setData(Uri.parse("tel:"+data.get(position).getNumber()));
                            context.startActivity(intent);
                            break;
                        case R.id.action_add_white_list:
                            ToastShow.showToast(context, "加入白名单" + position);
                            break;
                        case R.id.action_add_black_list:
                            ToastShow.showToast(context, "加入黑名单" + position);
                            break;
                        case R.id.action_delete:
                            try {
                                String sql = "delete from record where id="+Integer.parseInt(data.get(position).getId())+";";
                                Log.d("gaga",sql);
                                dateBaseHelper.execSQL(sql);
                                //删除成功后发送一个广播给fragment用来更新界面
                                Intent intent1 = new Intent(Config.UPDATE);
                                context.sendBroadcast(intent1);
                                if(dateBaseHelper!=null){
                                    dateBaseHelper.close();
                                }
                            }catch (SQLException e){
                                e.printStackTrace();
                                dateBaseHelper.close();
                            }
                            break;
                    }
                    return true;
                }
            });
            menu.show();
//            ToastShow.showToast(context, "点击了gaga");
        }
    }

}
