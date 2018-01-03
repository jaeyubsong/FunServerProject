package com.example.user.funserverproject;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private Button contact_update;
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

        if (isLoggedin()) {
            getFriendList();
        }
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        loginButton.setFragment(this);

        loginWithFB();
        Log.d(TAG, "Below are the names inside contactlist");

        //while(!finished) {

//        }

        contact_update = (Button) view.findViewById(R.id.contact_updatebtn);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeContactList();
                if (isLoggedin()) {
                    getFriendList();
                }
                new JSONTask2().execute("http://13.125.110.222:3000/post");
                new JSONTask().execute("http://13.125.110.222:3000/users");
            }
        };
        contact_update.setOnClickListener(listener);


        Log.d(TAG, "Final line length of contact list = " +MainActivity.ContactList.size());

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
                //getFriendList();
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
                                //mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher_background),
                                //        dataObject.getString("name"), null);
                                addContact(new Contact(name, "-"));
                            }
                            //finished=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        Log.d("Length of contact list in the end = ", Integer.toString(MainActivity.ContactList.size()));

        return friendslist;
    }
    private void addContact(Contact contact) {
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

    public boolean isLoggedin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                Fragment1.this.mAdapter = new ListViewAdapter();
                JSONObject ja2 = (JSONObject) new JSONArray(result).get(0);

                JSONArray ja = (JSONArray) new JSONArray(ja2.getString("newcont"));

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);
                    addContact(new Contact(order.getString("name"), order.getString("number")));
                }
                Collections.sort(MainActivity.ContactList);
                for (int i = 0; i < MainActivity.ContactList.size(); i++ ) {
                    String name = MainActivity.ContactList.get(i).getName();
                    String phoneNum = MainActivity.ContactList.get(i).getNum();
                    mAdapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher_foreground),
                            name, phoneNum);
                }

                mListView.setAdapter(mAdapter);

            }
            catch (JSONException e){
                return ;
            }
        }

    }

    public class JSONTask2 extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONArray jsonArray = new JSONArray();

                for( int i = 0 ; i < MainActivity.ContactList.size(); i++) {
                    JSONObject jobject = new JSONObject();
                    jobject.put("name", MainActivity.ContactList.get(i).getName());
                    jobject.put("number", MainActivity.ContactList.get(i).getNum());
                    jsonArray.put(jobject);
                }

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://13.125.110.222:3000/post");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    //writer.write(jsonArray.toString());
                    //writer.flush();
                    //writer.close();//버퍼를 받아줌
                    outStream.write(jsonArray.toString().getBytes());
                    outStream.close();
                    con.getResponseCode();
                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


}