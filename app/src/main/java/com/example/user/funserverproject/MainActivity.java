package com.example.user.funserverproject;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static ArrayList<Contact> ContactList = new ArrayList<Contact>();
    private final int Contact_Fragment = 1;
    private final int Gallery_Fragment = 2;
    private final int Custom_Fragment = 3;
    private final String TAG = "MainActivity";
    private static boolean frag2_first_time = true;

    private Button btn_tab1, btn_tab2, btn_tab3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_tab1 = (Button)findViewById(R.id.btn_tab1);
        btn_tab2 = (Button)findViewById(R.id.btn_tab2);
        btn_tab3 = (Button)findViewById(R.id.btn_tab3);

        btn_tab1.setOnClickListener(this);
        btn_tab2.setOnClickListener(this);
        btn_tab3.setOnClickListener(this);




    }

    public void callFragment (int frag_num) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (frag_num) {
            case 1:
                Fragment1 fragment1 = new Fragment1();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case 2:
                Fragment2 fragment2 = new Fragment2();
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;

            case 3:
                Fragment3 fragment3 = new Fragment3();
                transaction.replace(R.id.fragment_container, fragment3);
                transaction.commit();
                break;
        }
    }

    public void onClick() {

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "Button selected");
        switch (view.getId()) {
            case R.id.btn_tab1:
                callFragment(Contact_Fragment);
                break;

            case R.id.btn_tab2:
                callFragment(Gallery_Fragment);
                break;

            case R.id.btn_tab3:
                callFragment(Custom_Fragment);
                break;
        }
    }

    public void addContact(Contact contact) {
        Contact oldContact;
        boolean existDup = false;
        for (int i = 0; i < ContactList.size(); i++) {
            oldContact = ContactList.get(i);
            if(oldContact.getName().equals(contact.getName())) { //Equal name
                if(oldContact.getNum().length() < contact.getName().length()) { //Length of old contact < length of new contact
                    ContactList.remove(i);
                    ContactList.add(contact);
                    existDup = true;
                    break;
                }
            }
        }
        if(!existDup) {
            ContactList.add(new Contact(contact.getName(), contact.getNum()));
        }
    }

    public static boolean get_frag2First() {
        if(frag2_first_time){
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Contact> returnContactList() {
        return ContactList;
    }



}
