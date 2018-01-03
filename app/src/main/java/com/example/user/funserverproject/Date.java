package com.example.user.funserverproject;

/**
 * Created by user on 2018-01-03.
 */

public class Date {
    int year;
    int month;
    int day;

    public Date (int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Date (String year, String month, String day) {
        this.year = Integer.parseInt(year);
        this.month = Integer.parseInt(month);
        this.day = Integer.parseInt(day);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String DateFormat() {
        return year + "/" + month + "/" + day;
    }

}
