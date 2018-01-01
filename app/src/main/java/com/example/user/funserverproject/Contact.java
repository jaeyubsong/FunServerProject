package com.example.user.funserverproject;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by user on 2018-01-02.
 */

public class Contact implements Comparable {
    String name;
    String num;

    public Contact(String name, String num) {
        this.name = name;
        this.num = num;
    }

    public Contact(String name) {
        this.name = name;
        this.num = "";
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        Contact other = (Contact) o;
        return name.compareTo(other.getName());
    }
}
