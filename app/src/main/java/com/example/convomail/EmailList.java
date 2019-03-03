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
    public static void fetch(String pop3Host, String storeType, String user,
                             String password) {
        try {
            // create properties field
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            store.connect(pop3Host, user, password);

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
                tempFrom = messages[i].getFrom().toString();
                tempHeader = tempDate + "\n" + tempSubject + "\n" + tempFrom;
                Log.d("header", tempHeader);
//                header.add(tempHeader);
                System.out.print(tempHeader);
            }
//            for (int i = 0; i < messages.length; i++) {
//                Message message = messages[i];
//                System.out.println("---------------------------------");
//                writePart(message);
//                String line = reader.readLine();
//                if ("YES".equals(line)) {
//                    message.writeTo(System.out);
//                } else if ("QUIT".equals(line)) {
//                    break;
//                }
//            }

            // close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            Log.d("error", e.toString()) ;
        }    }
    public static Message[] doit(String username, String password) throws MessagingException, IOException {
        Folder folder = null;
        Store store = null;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com",username, password);
            folder = store.getFolder("Inbox");
            /* Others GMail folders :
             * [Gmail]/All Mail   This folder contains all of your Gmail messages.
             * [Gmail]/Drafts     Your drafts.
             * [Gmail]/Sent Mail  Messages you sent to other people.
             * [Gmail]/Spam       Messages marked as spam.
             * [Gmail]/Starred    Starred messages.
             * [Gmail]/Trash      Messages deleted from Gmail.
             */
            folder.open(Folder.READ_WRITE);
            Message messages[] = folder.getMessages();
            System.out.println("No of Messages : " + folder.getMessageCount());
            System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
            for (int i=0; i < messages.length; ++i) {
                System.out.println("MESSAGE #" + (i + 1) + ":");
                Message msg = messages[i];
            }
            String tempDate, tempSubject, tempHeader, tempFrom;
            for (int i = 0; i < messages.length; i++) {
                tempDate = "";
                tempSubject = "";
                tempHeader = "";
                tempDate = messages[i].getSentDate().toString();
                tempSubject = messages[i].getSubject().toString();
                tempFrom = messages[i].getFrom().toString();
                tempHeader = tempDate + "\n" + tempSubject + "\n" + tempFrom;
                header.add(tempHeader);
                System.out.print(tempHeader);
            }
        /*

          if we don''t want to fetch messages already processed
          if (!msg.isSet(Flags.Flag.SEEN)) {
             String from = "unknown";
             ...
          }
        */
//                String from = "unknown";
//                if (msg.getReplyTo().length >= 1) {
//                    from = msg.getReplyTo()[0].toString();
//                }
//                else if (msg.getFrom().length >= 1) {
//                    from = msg.getFrom()[0].toString();
//                }
//                String subject = msg.getSubject();
//                System.out.println("Saving ... " + subject +" " + from);
//                // you may want to replace the spaces with "_"
//                // the TEMP directory is used to store the files
//                String filename = "c:/temp/" +  subject;
//                saveParts(msg.getContent(), filename);
//                msg.setFlag(Flags.Flag.SEEN,true);
//                // to delete the message
//                // msg.setFlag(Flags.Flag.DELETED, true);

            return messages;
        }
        finally {
            if (folder != null) { folder.close(true); }
            if (store != null) { store.close(); }
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

