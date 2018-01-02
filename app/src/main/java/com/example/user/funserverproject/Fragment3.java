package com.example.user.funserverproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;

/**
 * Created by user on 2017-12-30.
 */

public class Fragment3 extends Fragment{
    public Fragment3() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment3","d");
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.calendar_main, container, false);
        CalendarView simpleCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        return view;
    }
}
