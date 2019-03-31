package com.example.convomail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import static android.content.Context.MODE_PRIVATE;

public class TabSpamFragment extends Fragment {
    static ArrayList<String> header = new ArrayList<String>();
    String body = "";
    private User user;
    Intent newIntent;
    private ListView list;
    private ArrayAdapter<String> adapter=null;
    private ProgressBar spinner;
    public static final String PREFS_NAME = "myPrefsFile";
    private String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public SharedPreferences SharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_spam, container, false);
        list = rootview.findViewById(R.id.SpamMailList);
        ArrayList<String> s = getArguments().getStringArrayList("auth");
        Log.d("sp", s.get(2));
        user = new User(s.get(1), s.get(2), s.get(0));
        spinner = rootview.findViewById(R.id.progressBar1);
        setHasOptionsMenu(true);

        connectServer(user);
        setRetainInstance(true);

        return rootview;
    }



    public  void connectServer(User user) {
        try {
            new RetrieveMessages(getContext()).execute(user.getUserID(), user.getPassword());
        }
        catch(Exception e){}


    }

    public void setInbox(Inbox inbox){
        try{
            header.clear();
            Date tempDate;
            String  tempSubject, tempHeader, tempFrom;
            for (int i = 0; i < inbox.getSpam().getMessages().size(); i++) {
                tempDate = null;
                tempSubject = "";
                tempHeader = "";
                tempDate = inbox.getSpam().getMessages().get(i).getDate();
                tempSubject = inbox.getSpam().getMessages().get(i).getSubject().toString();
                tempFrom = inbox.getSpam().getMessages().get(i).getFromAddress()[0].toString();
                tempHeader = tempFrom + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + month[tempDate.getMonth()]+ " " +  tempDate.getDate() +" "+ (tempDate.getYear()+1900)+ "\n\n" + tempSubject;
                Log.d("header", tempHeader);

                System.out.print(tempHeader);
                header.add(tempHeader);
            }
//            Log.d("size", h.get(0));
            user.setInbox(inbox);
//            user.saveData(this);
            adapter = new ArrayAdapter<String>(getContext(), R.layout.dataview, R.id.TextView ,header);
            list = (ListView) getView().findViewById(R.id.SpamMailList);
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
        Message[] reverse(Message a[], int n)
        {
            Message[] b = new Message[n];
            int j = n;
            for (int i = 0; i < n; i++) {
                b[j - 1] = a[i];
                j = j - 1;
            }

            return b;
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
                return "[Gmail]/Spam";
            }
            else if(s[1].equals("outlook.com")){
                return "Junk" ;
            }
            return "";
        }
        @Override
        protected Inbox doInBackground(String... strings) {
            Inbox inbox = new Inbox(new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()));
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
                messages = reverse(messages, messages.length);

                inbox.setSpam(messages);
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
//            progressDialog = ProgressDialog.show(this.context,"Retrieving messages","Please wait...",false,false);
            spinner.setVisibility(View.VISIBLE);
        }

        protected  void onPostExecute(Inbox inbox) {

//            progressDialog.dismiss();
            spinner.setVisibility(View.GONE);
            setInbox(inbox);

        }
    }

}