package com.csuft.phoneinterception.contentutil;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.csuft.phoneinterception.mode.Contacts;

import java.util.ArrayList;

/**
 * Created by wuhaiwen on 2016/8/29.
 */
public class ContactUtil {
    static ArrayList<Contacts> contactsList;
    static  Context context;
    public static ArrayList<Contacts> getData(Context context){
        contactsList = new ArrayList<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        Cursor cursor = resolver.query(
                uri,
                projection,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String number = cursor.getString(1);
            String contacts_name = cursor.getString(2);
            Contacts contacts = new Contacts(contacts_name, id, number);
            contactsList.add(contacts);
        }
        cursor.close();
        return contactsList;
    }

}
