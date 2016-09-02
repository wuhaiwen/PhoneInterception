package com.csuft.phoneinterception.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.db.DateBaseHelper;
import com.csuft.phoneinterception.util.Config;
import com.csuft.phoneinterception.util.ToastShow;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wuhaiwen on 2016/8/29.
 */
public class BlackListAdapter extends BaseAdapter {
    List<String> data;
    Context context;
    LayoutInflater inflater;
    SQLiteDatabase db;

//    SQLiteDatabase db = new DateBaseHelper(context, "record.db", null, 1).getWritableDatabase();
//    Cursor cursor = db.rawQuery("select number,retreat,date,id from record order by id desc", null);

    public BlackListAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        db = new DateBaseHelper(context, "record.db", null, 1).getWritableDatabase();
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
            convertView = inflater.inflate(R.layout.contacts_mode_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageButtonListener listener = new ImageButtonListener();
        listener.setPosition(position);
        viewHolder.ibt_add.setOnClickListener(listener);
        viewHolder.black_name.setText(data.get(position));
        viewHolder.ibt_add.setImageResource(R.drawable.delete);
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.contacts_name)
        TextView black_name;
        @Bind(R.id.ibt_add_list)
        ImageButton ibt_add;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }

    class ImageButtonListener implements View.OnClickListener {

        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            try {
                String sql = "delete from black_list where key=" +"'"+ data.get(position)+"'";
                db.execSQL(sql);
//                db.execSQL("delete from white_list where id>-1");
                db.close();
                ToastShow.showToast(context, "删除成功");
                Intent intent = new Intent(Config.DELETE_BLACK_LIST_SUCCESS);
                intent.putExtra("position3", position);
                context.sendBroadcast(intent);
            }catch (Exception e){
                ToastShow.showToast(context, "删除失败");
                e.printStackTrace();
            }
        }
    }
}
