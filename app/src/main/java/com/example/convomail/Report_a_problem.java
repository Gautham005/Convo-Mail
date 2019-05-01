package com.example.convomail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

public class Report_a_problem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_a_problem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sendProblem();
    }

    private void sendProblem() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kvishnu9497@gmail.com","rahul.krishnan27@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report your problem : ");
        EditText message = (EditText) findViewById(R.id.text);
        String feedback = message.getText().toString();
        emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);
        startActivity(Intent.createChooser(emailIntent, "Report your problem "));
    }
}
