package com.csuft.phoneinterception.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wuhaiwen on 2016/8/27.
 */
public class OperateDbHelper {
    static SQLiteDatabase db ;
    public static Cursor getData(SQLiteDatabase db,String sql){
        Cursor cursor;
        cursor = db.rawQuery(sql,null);
        return cursor;
    }
    public static void insertData(SQLiteDatabase db,String sql){
        db.execSQL(sql);
    }
}
