package com.example.user.funserverproject;

import java.util.ArrayList;

/**
 * Created by user on 2018-01-03.
 */

public class CalendarEvent {
    Date startDate;
    Date endDate;
    String title;
    String description;

    public CalendarEvent(Date startDate, Date endDate, String title, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
    }

    public CalendarEvent(Date startDate, Date endDate, String title) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = "";
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAllDate() {
        ArrayList<Date> resultArray = new ArrayList<Date>();
        resultArray.add(getStartDate());
        resultArray.add(getEndDate());
        String result = "";
        result = resultArray.get(0).DateFormat() + "\n~" + resultArray.get(0).DateFormat();
        return result;
    }

    public String getDescription() {
        return description;
    }


}
