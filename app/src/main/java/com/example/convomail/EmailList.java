package com.example.convomail;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class EmailList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Intent newIntent;
    User user;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String[] emails = {"rahul123@gmail.com", "nsk77@gmail.com"};
    public static final String PREFS_NAME = "myPrefsFile";
    public static String fileName;
    public SharedPreferences SharedPreferences;
    TabPrimaryFragment tp = new TabPrimaryFragment();
    TabSentMailFragment tsm=new TabSentMailFragment();
    TabDraftFragment td=new TabDraftFragment();
    TabSpamFragment tsp=new TabSpamFragment();
    TabTrashFragment tt=new TabTrashFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);
        newIntent = getIntent();
        SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Log.d("Emails", SharedPreferences.getString("username",""));
        emails = SharedPreferences.getString("username","").split("::");
        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");
        String bo = newIntent.getStringExtra("first");
        bo = "true";
        fileName = username+password+"Primary";
        ArrayList<String> s = new ArrayList<>();
        s.add(name);
        s.add(username);
        s.add(password);
        user = new User(username, password, name);
        Bundle b = new Bundle();
        b.putStringArrayList("auth",s);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 200
            );

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
        }
        tp.setArguments(b);
        tsm.setArguments(b);
        td.setArguments(b);
        tsp.setArguments(b);
        tt.setArguments(b);
        setContentView(R.layout.activity_email_list);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(tp, "Primary");
        adapter.addFragment(tsm, "Sent Mail");
        adapter.addFragment(td, "Draft");
        adapter.addFragment(tsp, "Spam");
        adapter.addFragment(tt, "Trash");
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView uname = header.findViewById(R.id.UserName);
        TextView email = header.findViewById(R.id.UserEmail);
        uname.setText(name);
        email.setText(username);
        if(bo.equals("true")){
            Log.d("login", bo);

            tp.connectServer(user, true);
            tsm.connectServer(user, true);
            td.connectServer(user, true);
            tsp.connectServer(user, true);
            tt.connectServer(user, true);
        }

    }
    public void onTabSelected(TabLayout.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        MenuItem menuItem = menu.findItem(R.id.sync1);
        menuItem.setVisible(true);
        menuItem = menu.findItem(R.id.deletebtn);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sync1) {
            tp.connectServer(user, false);
            tsm.connectServer(user, false);
            td.connectServer(user, false);
            tsp.connectServer(user, false);
            tt.connectServer(user, false);
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

        if (id == R.id.log_out) {
            editor.remove("name");
            editor.apply();
            try {
                new FileOutputStream(fileName).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.switch_account) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EmailList.this);
            builder.setTitle("Switch account")
                    .setItems(emails, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            String name1 = SharedPreferences.getString("name", "");
                            String email1 = SharedPreferences.getString("username", "");
                            String password1 = SharedPreferences.getString("password", "");
                            String names[] = name1.split("::");
                            String usernames[] = email1.split("::");
                            String passwords[] = password1.split("::");
                            name1 = names[position];
                            email1 = usernames[position];
                            password1 = passwords[position];
                            Intent in = new Intent(getApplicationContext(), EmailList.class);
                            in.putExtra("Name", name1);
                            in.putExtra("username", email1);
                            in.putExtra("pass", password1);
                            in.putExtra("first", "false");
                            startActivity(in);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (id == R.id.add_account) {
            Intent i = new Intent(this,MainActivity.class);
            i.putExtra("Type","Addaccount");
            startActivity(i);
        }
        else if (id == R.id.help) {
            Intent in = new Intent(this, Report_a_problem.class);
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Compose(View view) {
        Intent in = new Intent(this, ComposeActivity.class);
        in.putExtra("Name", user.getName());
        in.putExtra("username", user.getUserID());
        in.putExtra("pass", user.getPassword());
        in.putExtra("type", "Compose");
        startActivity(in);
    }



}

