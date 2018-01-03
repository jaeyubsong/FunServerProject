package com.example.user.funserverproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 2017-12-30.
 */



public class Fragment3 extends Fragment{

    private final String TAG = "Fragment3";
    Button btnFloat;
    int selected_year = 0;
    int selected_month = 0;
    int selected_day = 0;

    public Fragment3() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment3","d");
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.calendar_main, container, false);
        CalendarView simpleCalendarView = (CalendarView) view.findViewById(R.id.calendarView);

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date = year + "/" + (month + 1) + "/" + day;
                selected_year = year;
                selected_month = month + 1;
                selected_day = day;
                Log.d(TAG, "OnSelectedDayChange"+date);
            }
        });

        btnFloat = (Button) view.findViewById(R.id.addButton);
        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Button Selected date is "+Integer.toString(selected_year)+"/"
                        +Integer.toString(selected_month)+"/"+Integer.toString(selected_day));
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_date, null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                final EditText startDate = (EditText)mView.findViewById(R.id.startDate);
                final EditText endDate = (EditText)mView.findViewById(R.id.endDate);
                startDate.setText(Integer.toString(selected_year)+"/"
                        +Integer.toString(selected_month)+"/"+Integer.toString(selected_day));
                endDate.setText(Integer.toString(selected_year)+"/"
                        +Integer.toString(selected_month)+"/"+Integer.toString(selected_day));
                final EditText title = (EditText)mView.findViewById(R.id.eventTitle);
                final EditText description = (EditText)mView.findViewById(R.id.description);

                Button mCancel = (Button) mView.findViewById(R.id.cancelButton);
                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Button mOk = (Button) mView.findViewById(R.id.okButton);
                mOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!checkDateFormat( startDate.getText().toString() ) || !checkDateFormat( endDate.getText().toString() ) ) {
                            Toast.makeText(getActivity().getBaseContext(), "Wrong Date Format", Toast.LENGTH_SHORT).show();
                        } else if (title.getText().toString().length() == 0) {
                            Toast.makeText(getActivity().getBaseContext(), "Please fill in the title of event", Toast.LENGTH_SHORT).show();
                        } else {
                                Toast.makeText(getActivity().getBaseContext(), "Correct Date Format", Toast.LENGTH_SHORT).show();
                                String[] startArray = startDate.getText().toString().split("/");
                                String[] endArray = endDate.getText().toString().split("/");
                                Date start = new Date (startArray[0], startArray[1], startArray[2]);
                                Date end = new Date (endArray[0], endArray[1], endArray[2]);
                                if (description == null) {
                                    MainActivity.EventList.add( new CalendarEvent(start, end, title.getText().toString()) );
                                } else {
                                    MainActivity.EventList.add( new CalendarEvent(start, end, title.getText().toString(), description.getText().toString() ) );
                                }
                                dialog.dismiss();
                        }
                    }
                });


            }
        });

        ListView mListView = (ListView) view.findViewById(R.id.event_list);

        EventAdapter customAdapter = new EventAdapter();

        mListView.setAdapter(customAdapter);







        Log.d(TAG, "The selected date is 0"+Long.toString(simpleCalendarView.getDate()));
        return view;
    }

    private boolean checkDateFormat (String date) {
        String[] subStrings = date.split("/");
        if (subStrings.length != 3) {
            return false;
        } else if ( subStrings[0].length() != 4 || !isInteger(subStrings[0]) ) {
            return false;
        } else if ( !isInteger(subStrings[1]) || !isInteger(subStrings[2]) ) {
            return false;
        } else if ( Integer.parseInt(subStrings[1]) < 0 || Integer.parseInt(subStrings[1]) > 12 ) {
            return false;
        } else if ( Integer.parseInt(subStrings[2]) < 0 || Integer.parseInt(subStrings[2]) > 31 ) {
            return false;
        }
        return true;
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    class EventAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MainActivity.EventList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.one_event, null);

            TextView date = (TextView) view.findViewById(R.id.dateRange);
            TextView title = (TextView) view.findViewById(R.id.event_title);
            TextView description = (TextView) view.findViewById(R.id.event_description);

            date.setText( MainActivity.EventList.get(i).getAllDate() );
            title.setText( MainActivity.EventList.get(i).getTitle() );
            description.setText( MainActivity.EventList.get(i).getDescription() );
            return view;
        }
    }

}
