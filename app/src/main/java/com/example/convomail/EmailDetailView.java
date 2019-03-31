package com.example.convomail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.mail.internet.InternetAddress;

public class EmailDetailView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView fromAddress;
    TextView subject;
    TextView content;
    TextView date;
    SharedPreferences SharedPreferences;
    private String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static final String PREFS_NAME = "myPrefsFile";
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail_view);
        try{
            String fileName = getIntent().getStringExtra("file");
            int pos = getIntent().getIntExtra("position", 0);

            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            User user = (User) is.readObject();
            is.close();
            fis.close();
            Log.d("User", user.getUserID());
            Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);
            TextView uname = (TextView) header.findViewById(R.id.UserName);
            TextView email = (TextView) header.findViewById(R.id.UserEmail);
            uname.setText(user.getName());
            email.setText(user.getUserID());
            Message m = user.inbox.getPrimary().getMessages().get(pos);
            InternetAddress person = (InternetAddress)m.getFromAddress()[0];
            fromAddress = findViewById(R.id.fromAddr);
            subject = (TextView) findViewById(R.id.subject);
            content = (TextView) findViewById(R.id.content);
            date = findViewById(R.id.date1);
            date.setText(month[m.getDate().getMonth()]+ " " +  m.getDate().getDate() +" "+ (m.getDate().getYear()+1900));
            fromAddress.setText(person.getAddress());
            subject.setText(m.getSubject());
            content.setText(m.getContent());
        }catch (Exception e){
            Log.d("Error", e.toString());
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
}
