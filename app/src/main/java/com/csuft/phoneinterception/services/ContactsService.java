package com.csuft.phoneinterception.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.csuft.phoneinterception.mode.Contacts;
import com.csuft.phoneinterception.util.Config;

import java.util.ArrayList;

public class ContactsService extends Service {

    private static final String TAG = "ContactsService";
    ArrayList<Contacts> contactsList = new ArrayList<>();

    public ContactsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        new GetContactsListThread().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class GetContactsListThread extends Thread {
        @Override
        public void run() {
            super.run();
            ContentResolver resolver = getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = {
                    Phone._ID,
                    Phone.NUMBER,
                    Phone.DISPLAY_NAME
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
            Intent intent = new Intent(Config.CONTACTS_LIST);
            intent.putExtra(Config.CONTACTS_LIST, contactsList);
            sendBroadcast(intent);
        }
    }
}
