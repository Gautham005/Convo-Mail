package com.example.convomail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Report_a_problem extends AppCompatActivity {
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_a_problem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = new User(getIntent().getStringExtra("username"), getIntent().getStringExtra("pass"),getIntent().getStringExtra("Name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button send =(Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendProblem();
            }
        });
    }

    private void sendProblem() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"kvishnu9497@gmail.com","rahul.krishnan27@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Report your problem : ");
        EditText message = (EditText) findViewById(R.id.text);
        String feedback = message.getText().toString();
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, feedback);
        startActivity(Intent.createChooser(emailIntent, "Report your problem "));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent intent = new Intent(this, EmailList.class);
                intent.putExtra("Name", user.getName());
                intent.putExtra("username", user.getUserID());
                intent.putExtra("pass", user.getPassword());
                intent.putExtra("first", "false");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
