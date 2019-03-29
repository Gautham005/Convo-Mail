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
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import static android.content.Context.MODE_PRIVATE;

public class TabPrimaryFragment extends Fragment {
    static ArrayList<String> header = new ArrayList<String>();
    String body = "";
    private User user;
    Intent newIntent;
    private ListView list;
    private ArrayAdapter<String> adapter=null;
    private ProgressBar spinner;
    public static final String PREFS_NAME = "myPrefsFile";

    public SharedPreferences SharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_primary, container, false);
        list = rootview.findViewById(R.id.PrimaryList);
        ArrayList<String> s = getArguments().getStringArrayList("auth");
        Log.d("nnn", s.get(1));
        user = new User(s.get(1), s.get(2), s.get(0));
        spinner = rootview.findViewById(R.id.progressBar1);
        setHasOptionsMenu(true);

        connectServer(user);
        setRetainInstance(true);

        return rootview;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.menuitems, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        SharedPreferences = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = SharedPreferences.edit();

        if(menuItem.getItemId()==R.id.sign_out){
            editor.remove("name");
            editor.apply();
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public  void connectServer(User user) {
        try {
            Log.d("nnn", "rm");
            new RetrieveMessages(getContext()).execute(user.getUserID(), user.getPassword());
            Log.d("nnn", "rm");

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
            adapter = new ArrayAdapter<String>(getContext(), R.layout.dataview, R.id.TextView ,header);
            list = (ListView) getView().findViewById(R.id.PrimaryList);
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
                String host = this.getHost(strings[0]);
                Properties properties = this.getProp(strings[0]);

                Session emailSession = Session.getDefaultInstance(properties);
                // emailSession.setDebug(true);
                Log.d("nnn", "ss");

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