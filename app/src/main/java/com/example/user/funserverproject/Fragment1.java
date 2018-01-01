package com.example.user.funserverproject;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 2017-12-30.
 */

public class Fragment1 extends Fragment{

    private final String TAG = "Fragment1";
    private ListView mListView;
    private ListViewAdapter mAdapter = new ListViewAdapter();
    boolean isFirst = true;

    public Fragment1() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"Accessed");
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.contact_main, container, false);
        mListView = (ListView) view.findViewById(R.id.contact_list);

        Activity thisActivity = getActivity();
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;
        Log.d(TAG, "starting log");

        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Getting permission");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)) {
                Log.d(TAG, "Show an explanation to the user log");

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                Log.d(TAG, "Request permission");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                SystemClock.sleep(2000);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        if (isFirst) {
            makeContactList();
            isFirst = false;
        } else {
            mListView.setAdapter(mAdapter);
        }
        return view;

    }

    private void makeContactList() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor mCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        mCursor.moveToFirst();

        while (mCursor.moveToNext()){
            int phoneIdx = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIdx = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            String name = mCursor.getString(nameIdx);
            String phoneNum = mCursor.getString(phoneIdx);

            // make phone number pretty
            if (!phoneNum.contains("-")){
                if (phoneNum.length() == 8){
                    phoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 8) + "-" + phoneNum.substring(8);
                }
                else{
                    phoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7);
                }
            }

            // check if adaptor has same contact
            if (mAdapter.isDuplicate(name, phoneNum)){
                continue;
            }else {
                mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher_background),
                        name, phoneNum);
            }


        }
        Collections.sort(mAdapter.getItemList(), new CompareNameDesc());
        mListView.setAdapter(mAdapter);
    }

    static class CompareNameDesc implements Comparator<ListViewItem> {
        @Override
        public int compare(ListViewItem o1, ListViewItem o2){
            return o1.getName().compareTo(o2.getName());
        }
    }
}
