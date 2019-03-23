package com.example.convomail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.hotspot2.pps.Credential;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String PREFER_NAME = null;
    public static final String PREFS_NAME = "myPrefsFile";
    public SharedPreferences SharedPreferences ;

    private EditText name;
    private EditText email;
    private EditText password;
    private String name1, email1, password1;
    private CheckBox remember_me;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        if(i.getStringExtra("Auth")!=null){
            AuthError();
        }
        SharedPreferences= getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if(SharedPreferences.contains("name")){
            name1 = SharedPreferences.getString("name", "");
            email1 = SharedPreferences.getString("username", "");
            password1 = SharedPreferences.getString("password", "");
            Intent in = new Intent(this, EmailList.class);
            in.putExtra("Name", name1);
            in.putExtra("username", email1);
            in.putExtra("pass", password1);
            startActivity(in);
        }
        else{
            name = (EditText) findViewById(R.id.NameID);
            email = (EditText) findViewById(R.id.EmailID);
            password = (EditText) findViewById(R.id.PassID);
            remember_me = findViewById(R.id.checkBox);
            login = (Button) findViewById(R.id.btnLogin);
        }
    }
    public void launchEmailList(View view){
        Intent i = new Intent(this, EmailList.class);
        String nname = name.getText().toString();
        String uname = email.getText().toString();
        String pass = password.getText().toString();
        if(remember_me.isChecked()){
            android.content.SharedPreferences.Editor e = SharedPreferences.edit();
            e.putString("name", nname);
            e.putString("username", uname);
            e.putString("password", pass);
            e.apply();
        }
        i.putExtra("Name", nname);
        i.putExtra("username", uname);
        i.putExtra("pass", pass);
        startActivity(i);
    }
    public void AuthError(){
        Toast t = Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG);
        for(int i=0;i<5;i++){
            t.show();
        }
    }
}
