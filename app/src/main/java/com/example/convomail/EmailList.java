package com.example.convomail;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailList extends AppCompatActivity {

    public static final String TAG = "EmailList";
    protected static final int CHOICE_MODE_SINGLE = 0;
    static Vector<String> dateHeader;
    static Vector<String> fromHeader;
    static Vector<String> subjectHeader;
    static ArrayList<String> header = new ArrayList<String>();
    static int EmailNumber;
    String body = "";
    private Login login;
    Intent newIntent;
    private ListView list ;
     private ArrayAdapter<String> adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);
        newIntent = getIntent();
        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");

        login = new Login(username, password, name);
        connectServer(login);
//        pl = new PersistLogins(login);
//        pl.serializeOut(this);
            Log.d("null", header.size()+" ");



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
    @Override
    protected void onActivityResult(int a, int b, Intent intent){
        this.finish();
    }

    public  void connectServer(Login login) {
        try {

            String host = "pop.gmail.com";
            String mailStoretype = "pop3";
            new RetrieveMessages().execute(login.getUserID(), login.getPassword());
        }
        catch(Exception e){}


    }
    public void setHeader(ArrayList<String> h){
        header = h;
        Log.d("size", h.get(0));
        adapter = new ArrayAdapter<String>(this, R.layout.dataview, R.id.TextView ,header);
        list = (ListView) this.findViewById(R.id.ListView);
        list.setAdapter(adapter);


    }

    protected void launchbodyFetch() {
        Intent a = new Intent(this, BodyClass.class);
        String bodyText = body;
        Log.d(TAG,bodyText);
        a.putExtra("body", bodyText);
        //a.putExtra("body", "HEY");
        startActivity(a);
    }
    class RetrieveMessages extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> header = new ArrayList<String>();
            try{
                // create properties field
                String pop3Host = "pop.gmail.com";
                Properties properties = new Properties();
                properties.put("mail.store.protocol", "pop3");
                properties.put("mail.pop3.host", pop3Host);
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");
                Session emailSession = Session.getDefaultInstance(properties);
                // emailSession.setDebug(true);

                // create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("pop3s");
                store.connect(pop3Host, strings[0], strings[1]);

                // create the folder object and open it
                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        System.in));

                // retrieve the messages from the folder in an array and print it
                Message[] messages = emailFolder.getMessages();
                String tempDate, tempSubject, tempHeader, tempFrom;
                for (int i = 0; i < messages.length; i++) {
                    tempDate = "";
                    tempSubject = "";
                    tempHeader = "";
                    tempDate = messages[i].getSentDate().toString();
                    tempSubject = messages[i].getSubject().toString();
                    tempFrom = messages[i].getFrom()[0].toString();
                    tempHeader = tempDate + "\n" + tempSubject + "\n" + tempFrom;
                    Log.d("header", tempHeader);

                    System.out.print(tempHeader);
                    header.add(tempHeader);
                }
                if(emailFolder!=null){
                    emailFolder.close(false);
                }
                if(store!=null){
                    store.close();
                }

            }
            catch (Exception e){
                Log.d("err", e.toString()) ;
            }
            return header;
        }

        protected  void onPostExecute(ArrayList<String> strings) {
            setHeader(strings);
        }
    }

}

