package com.example.convomail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class BodyClass extends AppCompatActivity {
    private static final String TAG = "BodyClass";
    Intent bodyIntent;
    private EditText bodyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bodyclass);
        bodyIntent = getIntent();
        String body = bodyIntent.getStringExtra("body");
        bodyText = (EditText)findViewById(R.id.body);
        //Log.d(TAG,body);
        bodyText.setText(body);
    }
}
