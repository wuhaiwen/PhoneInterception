package com.csuft.phoneinterception.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wuhaiwen on 2016/8/24.
 */
public class ToastShow {

    public static void showToast(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
