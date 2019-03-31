package com.example.convomail;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.System.exit;


public class EmailList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Intent newIntent;
    User user;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static final String PREFS_NAME = "myPrefsFile";

    public SharedPreferences SharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);
//        Log.d("ddd", t.toString());
//        setSupportActionBar(t);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Compose a new mail", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView uname = (TextView) header.findViewById(R.id.UserName);
        TextView email = (TextView) header.findViewById(R.id.UserEmail);
        uname.setText(name);
        email.setText(username);
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = SharedPreferences.edit();
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            editor.remove("name");
            editor.apply();
            startActivity(new Intent(this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

