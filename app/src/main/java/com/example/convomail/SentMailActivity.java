package com.example.convomail;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class SentMailActivity extends Activity {
    static ArrayList<String> header = new ArrayList<String>();
    String body = "";
    private User user;
    Intent newIntent;
    private ListView list;
    private ArrayAdapter<String> adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_mail);
        newIntent = getIntent();
        String password = newIntent.getStringExtra("password");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("name");

        user = new User(username, password, name);
        connectServer(user);
    }
    public  void connectServer(User user) {
        try {


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
            list = (ListView) this.findViewById(R.id.SentMailList);
            list.setAdapter(adapter);
        }
        catch (Exception e){
            Log.d("Error", e.toString());
        }


    }
    class RetrieveMessages extends AsyncTask<String, Void, Inbox> {
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
                return "[Gmail]/Sent Mail";
            }
            else if(s[1].equals("outlook.com")){
                return "Sent" ;
            }
            return "";
        }
        @Override
        protected Inbox doInBackground(String... strings) {
            Inbox inbox = new Inbox(new Mail(new ArrayList<Message>()), new Mail(new ArrayList<Message>()), new Mail(new ArrayList<Message>()), new Mail(new ArrayList<Message>()));
            try{
                // create properties field
                String host = this.getHost(strings[0]);
                Properties properties = this.getProp(strings[0]);

                Session emailSession = Session.getDefaultInstance(properties);
                // emailSession.setDebug(true);

                // create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("imaps");
                store.connect(host, strings[0], strings[1]);

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
                for (int i = messages.length-1; i >=0; i--) {
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
            catch (AuthenticationFailedException e){
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("Auth", "autherror");
                startActivity(i);
            }
            catch (Exception e){
                Log.d("err", e.toString()) ;

            }
            return inbox;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progDailog.setMessage("Loading...");
//            progDailog.setIndeterminate(false);
//            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progDailog.setCancelable(true);
//            progDailog.show();
            progressDialog = ProgressDialog.show(this.context,"Retrieving messages","Please wait...",false,false);

        }

        protected  void onPostExecute(Inbox inbox) {

            progressDialog.dismiss();

            setInbox(inbox);
        }
    }
}
