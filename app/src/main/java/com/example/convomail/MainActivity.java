package com.example.convomail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.hotspot2.pps.Credential;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    public static android.content.SharedPreferences SharedPreferences = null;
    private static final String PREFER_NAME = null;

    private EditText name;
    private EditText email;
    private EditText password;

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.NameID);
        email = (EditText) findViewById(R.id.EmailID);
        password = (EditText) findViewById(R.id.PassID);
        login = (Button) findViewById(R.id.btnLogin);
    }
    public void launchEmailList(View view){
        Intent i = new Intent(this, EmailList.class);
        String nname = name.getText().toString();
        String uname = email.getText().toString();
        String pass = password.getText().toString();
        i.putExtra("Name", nname);
        i.putExtra("username", uname);
        i.putExtra("pass", pass);
        startActivity(i);
    }
}
