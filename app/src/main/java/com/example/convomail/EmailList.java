package com.example.convomail;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class EmailList extends TabActivity {

    Intent newIntent;
    User user;
    private static final String PRIMARY_SPEC = "Primary";
    private static final String DRAFT_SPEC = "Draft";
    private static final String TRASH_SPEC = "Trash";
    private static final String SENT_MAIL_SPEC = "SentMail";
    private static final String SPAM_SPEC = "Spam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);
        newIntent = getIntent();
        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");

        user = new User(username, password, name);
        TabHost tabHost = getTabHost();
        // Primary Tab
        TabSpec primarySpec = tabHost.newTabSpec(PRIMARY_SPEC);
        Intent primaryIntent = new Intent(this, PrimaryMailActivity.class);
        primaryIntent.putExtra("username", username);
        primaryIntent.putExtra("password", password);
        primaryIntent.putExtra("name", name);
        // Tab Content
        primarySpec.setContent(primaryIntent);
        primarySpec.setIndicator(PRIMARY_SPEC);
        // Sent Mail Tab
        TabSpec SentMailSpec = tabHost.newTabSpec(SENT_MAIL_SPEC);
        Intent SentMailIntent = new Intent(this, SentMailActivity.class);
        // Tab Content
        SentMailSpec.setIndicator(SENT_MAIL_SPEC);
        SentMailSpec.setContent(SentMailIntent);
        SentMailIntent.putExtra("username", username);
        SentMailIntent.putExtra("password", password);
        SentMailIntent.putExtra("name", name);
        //Draft Tab
        TabSpec  DraftSpec = tabHost.newTabSpec(DRAFT_SPEC);
        //Tab content
        Intent DraftIntent = new Intent(this, DraftMailActivity.class);
        DraftSpec.setContent(DraftIntent);
        DraftSpec.setIndicator(DRAFT_SPEC);
        DraftIntent.putExtra("username", username);
        DraftIntent.putExtra("password", password);
        DraftIntent.putExtra("name", name);
        //Spam Tab
        TabSpec  SpamSpec = tabHost.newTabSpec(SPAM_SPEC);
        //Tab content
        SpamSpec.setIndicator(SPAM_SPEC);
        Intent SpamIntent = new Intent(this, SpamMailActivity.class);
        SpamSpec.setContent(SpamIntent);
        SpamIntent.putExtra("username", username);
        SpamIntent.putExtra("password", password);
        SpamIntent.putExtra("name", name);
        //Trash Tab
        TabSpec  TrashSpec = tabHost.newTabSpec(TRASH_SPEC);
        TrashSpec.setIndicator(TRASH_SPEC);
        //Tab content
        Intent TrashIntent = new Intent(this, TrashActivity.class);
        TrashSpec.setContent(TrashIntent);
        TrashIntent.putExtra("username", username);
        TrashIntent.putExtra("password", password);
        TrashIntent.putExtra("name", name);
        // Adding all TabSpec to TabHost
        tabHost.addTab(primarySpec); // Adding Primary tab
        tabHost.addTab(SentMailSpec); // Adding SentMail tab
        tabHost.addTab(DraftSpec); // Adding Draft tab
        tabHost.addTab(SpamSpec); // Adding Spam tab
        tabHost.addTab(TrashSpec); // Adding Trash tab




//        user.loadData(this);
//
//
//        try {
//            Log.d("user", user.getUserID().toString());
//            String tempDate, tempSubject, tempHeader, tempFrom;
//            for (int i = 0; i < user.getInbox().getPrimary().getMessages().size(); i++) {
//                tempDate = "";
//                tempSubject = "";
//                tempHeader = "";
//                tempDate = user.getInbox().getPrimary().getMessages().get(i).getSentDate().toString();
//                tempSubject = user.getInbox().getPrimary().getMessages().get(i).getSubject().toString();
//                tempFrom = user.getInbox().getPrimary().getMessages().get(i).getFrom()[0].toString();
//                tempHeader = tempDate + "\n" + tempSubject + "\n" + tempFrom;
//                Log.d("header", tempHeader);
//
//                System.out.print(tempHeader);
//                header.add(tempHeader);
//            }
//            Log.d("null", header.size() + " ");
//
//            Log.d("user", header.toString());
//            adapter = new ArrayAdapter<String>(this, R.layout.dataview, R.id.TextView, header);
//            list = (ListView) this.findViewById(R.id.ListView);
//            list.setAdapter(adapter);
//        }
//        catch (Exception e){
//            Log.d("Erro 1r", e.toString());
//        }
//        Log.d("null", header.size()+" ");
//
//        connectServer(user);
////        pl = new PersistLogins(user);
////        pl.serializeOut(this);
//            Log.d("null", header.size()+" ");



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
//    protected void onActivityResult(int a, int b, Intent intent){
//        this.finish();
//    }



//    protected void launchbodyFetch() {
//        Intent a = new Intent(this, BodyClass.class);
//        String bodyText = body;
//        Log.d(TAG,bodyText);
//        a.putExtra("body", bodyText);
//        //a.putExtra("body", "HEY");
//        startActivity(a);
//    }


}

