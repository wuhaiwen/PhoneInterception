package com.csuft.phoneinterception.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wuhaiwen on 2016/8/23.
 */
public class DateBaseHelper extends SQLiteOpenHelper {


//    private static final java.lang.String CREATE_TABLE_SQL2 =
//            "create table white_list ()"

    public DateBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table  IF NOT EXISTS record(id integer primary key AUTOINCREMENT,"
                + "number text,"
                + "retreat text,"
                + "date text)");
        Log.d("databases", "表一已经执行");
        db.execSQL("create table  IF NOT EXISTS white_list(id integer primary key,"
                + "number text,"
                + "name text)");
        Log.d("databases", "表二已经执行");
        db.execSQL("create table  IF NOT EXISTS black_list(id integer primary key AUTOINCREMENT,"
                + "key text)");
        Log.d("databases", "表三已经执行");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void DoExecSQL(String sql, SQLiteDatabase db) {
        db.execSQL(sql);
        if (db != null)
            db.close();
    }
}
