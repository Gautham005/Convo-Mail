package com.example.convomail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;

public class EmailList extends AppCompatActivity {

    public static final String TAG = "EmailList";
    protected static final int CHOICE_MODE_SINGLE = 0;
    static Vector<String> dateHeader;
    static Vector<String> fromHeader;
    static Vector<String> subjectHeader;
    static ArrayList<String> header;
    static int EmailNumber;
    static unsecImap us;
    String body = "";
    private Login login;
    Intent newIntent;

    private ListView list = null;
    private ArrayAdapter<String> adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);
        newIntent = getIntent();
        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");
//        connectServer(username, password);
//        login = new Login(username, password, name);
////        pl = new PersistLogins(login);
////        pl.serializeOut(this);
//        adapter = new ArrayAdapter<String>(this, R.layout.dataview, header);
//        list = (ListView) this.findViewById(R.id.ListView);
//        list.setAdapter(adapter);
//        list.setTextFilterEnabled(true);
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Log.d(TAG, "PARSING PORT NUMBER" + position);
//                // We pass in the value and only fetch the non empty list item
//                // We then pass the call to the fetch to get the bodies
//                if (position % 2 == 0) {
//                    body = us.fetchBody(position);
//                    launchbodyFetch();
//                }
//            }
//        });
    }
    @Override
    protected void onActivityResult(int a, int b, Intent intent){
        this.finish();
    }

    /*public static void connectServer(String userNameIn, String passwordIn) {
        try {
            String username = userNameIn;
            String pass = passwordIn;

            us = new unsecImap(username, pass);


            String tempDate = "";
            String tempSubject = "";
            String tempFrom = "";
            String tempHeader = "";
            header = new ArrayList<String>();

            Message[] m = us.fromHeader();
            for (int i = 0; i < m.length; i++) {
                tempDate = "";
                tempSubject = "";
                tempHeader = "";
                tempDate = m[i].getSentDate().toString();
                tempSubject = m[i].getSubject().toString();
                tempFrom = m[i].getFrom().toString();
                tempHeader = tempDate + "\n" + tempSubject + "\n" + tempFrom;
                header.add(tempHeader);

            }
        }
        catch(Exception e){}


    }
    protected void launchbodyFetch() {
        Intent a = new Intent(this, BodyClass.class);
        String bodyText = body;
        Log.d(TAG,bodyText);
        a.putExtra("body", bodyText);
        //a.putExtra("body", "HEY");
        startActivity(a);
    }*/
}
