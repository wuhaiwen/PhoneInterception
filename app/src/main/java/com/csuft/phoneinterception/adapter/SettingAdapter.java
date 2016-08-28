package com.csuft.phoneinterception.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csuft.phoneinterception.R;

/**
 * Created by wuhaiwen on 2016/8/26.
 */
public class SettingAdapter extends BaseAdapter {

    String[] data;
    Context context;
    LayoutInflater inflater;

    public SettingAdapter(Context context,String[] data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView setting_name;
        convertView = inflater.inflate(R.layout.setting_mode, parent, false);
        setting_name = (TextView) convertView.findViewById(R.id.setting_name);
        setting_name.setText(data[position]);
        return convertView;
    }
}
