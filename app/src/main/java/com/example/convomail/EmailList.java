package com.example.convomail;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;


public class EmailList extends AppCompatActivity {

    Intent newIntent;
    User user;
    private static final String PRIMARY_SPEC = "Primary";
    private static final String DRAFT_SPEC = "Draft";
    private static final String TRASH_SPEC = "Trash";
    private static final String SENT_MAIL_SPEC = "SentMail";
    private static final String SPAM_SPEC = "Spam";
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);
        Toolbar t = findViewById(R.id.tool_bar);
        Log.d("ddd", t.toString());
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        newIntent = getIntent();
        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");

        ArrayList<String> s = new ArrayList<>();
        s.add(name);
        s.add(username);
        s.add(password);
        user = new User(username, password, name);
        Bundle b = new Bundle();
        b.putStringArrayList("auth",s);
        TabPrimaryFragment tp = new TabPrimaryFragment();
        TabSentMailFragment tsm = new TabSentMailFragment();
        TabDraftFragment td = new TabDraftFragment();
        TabSpamFragment tsp = new TabSpamFragment();
        TabTrashFragment tt = new TabTrashFragment();
        tp.setArguments(b);
        tsm.setArguments(b);
        td.setArguments(b);
        tsp.setArguments(b);
        tt.setArguments(b);
        setContentView(R.layout.activity_email_list);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(tp, "Primary");
        adapter.addFragment(tsm, "Sent Mail");
        adapter.addFragment(td, "Draft");
        adapter.addFragment(tsp, "Spam");
        adapter.addFragment(tt, "Trash");
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


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
    public void onTabSelected(TabLayout.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }
    public void onBackPressed() {
        finish();
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

