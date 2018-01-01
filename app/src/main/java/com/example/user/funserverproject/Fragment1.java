package com.example.user.funserverproject;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by user on 2017-12-30.
 */

public class Fragment1 extends Fragment{

    //private ArrayList<Contact> ContactList = MainActivity.ContactList;
    private final String TAG = "Fragment1";
    private ListView mListView;
    private ListViewAdapter mAdapter = new ListViewAdapter();
    boolean isFirst = true;

    boolean finished = false;


    TextView txtStatus;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private String firstName,lastName, email,birthday,gender;
    private URL profilePicture;
    private String userId;


    public Fragment1() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"Accessed");
        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getApplicationContext());


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
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        loginButton.setFragment(this);

        loginWithFB();
        getFriendList();
        Log.d(TAG, "Below are the names inside contactlist");

        //while(!finished) {

//        }


        Log.d(TAG, "Final line length of contact list = " +MainActivity.ContactList.size());

        Collections.sort(MainActivity.ContactList);
        for (int i = 0; i < MainActivity.ContactList.size(); i++ ) {
            String name = MainActivity.ContactList.get(i).getName();
            String phoneNum = MainActivity.ContactList.get(i).getNum();
            if (mAdapter.isDuplicate(name, phoneNum)){
                continue;
            }else {
                mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher_foreground),
                        name, phoneNum);
                addContact(new Contact(name, phoneNum));
            }
        }

        return view;

    }

    private void makeContactList() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor mCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        mCursor.moveToFirst();


        while (true){
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

            addContact(new Contact(name, phoneNum));


            if (!mCursor.moveToNext()) {
                break;
            }


        }
        //Collections.sort(mAdapter.getItemList(), new CompareNameDesc());
        mListView.setAdapter(mAdapter);
    }

    static class CompareNameDesc implements Comparator<ListViewItem> {
        @Override
        public int compare(ListViewItem o1, ListViewItem o2){
            return o1.getName().compareTo(o2.getName());
        }
    }

    private void loginWithFB() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //txtStatus.setText("Login Success\n"+loginResult.getAccessToken());
                Log.d(TAG,"Login Success");
            }

            @Override
            public void onCancel() {
                //txtStatus.setText("Login cancelled.");
                Log.d(TAG,"Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                //txtStatus.setText("Login Error: "+error.getMessage());
            }
        });
    }
    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<String> getFriendList() {
        final ArrayList<String> friendslist = new ArrayList<String>();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/taggable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject responseObject = response.getJSONObject();
                        try {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String name = dataObject.getString("name");
                                //Log.d("Adding ", dataObject.toString());
                                Log.d("Adding ", name);
                                //mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher_background),
                                //        dataObject.getString("name"), null);
                                addContact(new Contact(name));
                                Log.d("Length of contact list = ", Integer.toString(MainActivity.ContactList.size()));
                            }
                            //finished=true;
                            Log.d(TAG,"Finished");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        Log.d("Length of contact list in the end = ", Integer.toString(MainActivity.ContactList.size()));

        return friendslist;
    }
    public void addContact(Contact contact) {
        Contact oldContact;
        boolean existDup = false;
        for (int i = 0; i < MainActivity.ContactList.size(); i++) {
            oldContact = MainActivity.ContactList.get(i);
            if(oldContact.getName().equals(contact.getName())) { //Equal name
                if(oldContact.getNum().length() < contact.getName().length()) { //Length of old contact < length of new contact
                    MainActivity.ContactList.remove(i);
                    MainActivity.ContactList.add(contact);
                    existDup = true;
                    break;
                } else {
                    existDup = true;
                    break;
                }
            }
        }
        if(!existDup) {
            MainActivity.ContactList.add(new Contact(contact.getName(), contact.getNum()));
        }
    }


}
