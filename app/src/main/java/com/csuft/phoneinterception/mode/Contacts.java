package com.csuft.phoneinterception.mode;

import java.io.Serializable;

/**
 * Created by wuhaiwen on 2016/8/26.
 */
public class Contacts implements Serializable{
    long id;
    String number;
    String contacts_name;

    public Contacts(String contacts_name, long id, String number) {
        this.contacts_name = contacts_name;
        this.id = id;
        this.number = number;
    }

    public String getContacts_name() {
        return contacts_name;
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}
