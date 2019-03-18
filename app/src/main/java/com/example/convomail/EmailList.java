package com.example.convomail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Folder;
import javax.mail.Message;
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
    private User user;
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

        user = new User(username, password, name);
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
        Log.d("null", header.size()+" ");

        connectServer(user);
//        pl = new PersistLogins(user);
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

    public  void connectServer(User user) {
        try {

            String host = "pop.gmail.com";
            String mailStoretype = "pop3";
            new RetrieveMessages(this).execute(user.getUserID(), user.getPassword());
        }
        catch(Exception e){}


    }
    public void setInbox(Inbox inbox){
        try{
            header.clear();
            String tempDate, tempSubject, tempHeader, tempFrom;
            for (int i = 0; i < inbox.getPrimary().getMessages().size(); i++) {
                tempDate = "";
                tempSubject = "";
                tempHeader = "";
                tempDate = inbox.getPrimary().getMessages().get(i).getSentDate().toString();
                tempSubject = inbox.getPrimary().getMessages().get(i).getSubject().toString();
                tempFrom = inbox.getPrimary().getMessages().get(i).getFrom()[0].toString();
                tempHeader = tempDate + "\n" + tempSubject + "\n" + tempFrom;
                Log.d("header", tempHeader);

                System.out.print(tempHeader);
                header.add(tempHeader);
            }
//            Log.d("size", h.get(0));
            user.setInbox(inbox);
//            user.saveData(this);
            Log.d("user", user.getInbox().primary.getMessages().get(0).getSubject());
            adapter = new ArrayAdapter<String>(this, R.layout.dataview, R.id.TextView ,header);
            list = (ListView) this.findViewById(R.id.ListView);
            list.setAdapter(adapter);
        }
        catch (Exception e){
            Log.d("Error", e.toString());
        }


    }

    protected void launchbodyFetch() {
        Intent a = new Intent(this, BodyClass.class);
        String bodyText = body;
        Log.d(TAG,bodyText);
        a.putExtra("body", bodyText);
        //a.putExtra("body", "HEY");
        startActivity(a);
    }
    class RetrieveMessages extends AsyncTask<String, Void, Inbox>{
        private Context context;
        ProgressDialog progressDialog;
        RetrieveMessages(Context c){
            this.context = c;
        }
        private String getHost(String user){
            String[]s = user.split("@");
            Properties properties = new Properties();
            if(s[1].equals("gmail.com")){
                return "smtp.gmail.com";
            }
            else if(s[1].equals("outlook.com")){
                return "smtp.office365.com";
            }
            return "";
        }
        private Properties getProp(String user){
            String[] s = user.split("@");
            Properties properties = new Properties();
            if(s[1].equals("gmail.com")){
                properties.put("mail.smtp.host", this.getHost(user));
                properties.put("mail.smtp.socketFactory.port", "587");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
            }
            else if(s[1].equals("outlook.com")){
                properties.put("mail.smtp.host", this.getHost(user));
                properties.put("mail.smtp.socketFactory.port", "465");
                properties.put("mail.smtp.port", "465");
                properties.put("mail.smtp.auth", "true");
            }
            return properties;
        }
        private String getFold(String user){
            String[] s = user.split("@");

            if(s[1].equals("gmail.com")){
                return "INBOX";
            }
            else if(s[1].equals("outlook.com")){
                return "INBOX" ;
            }
            return "";
        }
        @Override
        protected Inbox doInBackground(String... strings) {
            Inbox inbox = new Inbox(new Mail(new ArrayList<Message>()), new Mail(new ArrayList<Message>()), new Mail(new ArrayList<Message>()), new Mail(new ArrayList<Message>()));
            try{
                // create properties field
                String pop3Host = this.getHost(strings[0]);
                Properties properties = this.getProp(strings[0]);

                Session emailSession = Session.getDefaultInstance(properties);
                // emailSession.setDebug(true);

                // create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("imaps");
                store.connect(pop3Host, strings[0], strings[1]);

                // create the folder object and open it
                Folder[] f = store.getDefaultFolder().list();
                for(int i=0;i<f.length;i++){
                    Log.d("Folder", f[i].toString());
                }

                Folder emailFolder = store.getFolder(this.getFold(strings[0]));
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
                ArrayList<Message> m = new ArrayList<Message>();
                for(Message j : messages){
                    m.add(j);
                }
                inbox.setPrimary(new Mail(m));
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
            return inbox;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(this.context,"Retrieving messages","Please wait...",false,false);

        }

        protected  void onPostExecute(Inbox inbox) {

            progressDialog.dismiss();

            setInbox(inbox);
        }
    }

}

