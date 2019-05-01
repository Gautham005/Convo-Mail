package com.example.convomail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

public class TabPrimaryFragment extends Fragment {
    static ArrayList<String> header = new ArrayList<String>();
    String body = "";
    private User user;
    Intent newIntent;
    private ListView list;
    private ArrayAdapter<String> adapter=null;
    private ProgressBar spinner;
    public static final String PREFS_NAME = "myPrefsFile";
    private String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String fileName;
    public SharedPreferences SharedPreferences;
    int flag = 0;
    InternetAddress person;
    Boolean fl = true;
    int getMessageDate;

    public void connectServer(User user, boolean var) {
        try {


                new RetrieveMessages(getContext()).execute(user.getUserID(), user.getPassword());

            Log.d("nnn", "rm");

        } catch (Exception e) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        flag = 0;
        fl = false;
        getMessageDate = -2
        ;
        Toast t = Toast.makeText(getContext(), "Please click on sync button to get new messages", Toast.LENGTH_SHORT);
        t.show();
        View rootview = inflater.inflate(R.layout.fragment_primary, container, false);
        list = rootview.findViewById(R.id.PrimaryList);
        ArrayList<String> s = getArguments().getStringArrayList("auth");
        Log.d("nnn", s.get(0));
        user = new User(s.get(1), s.get(2), s.get(0));
        spinner = rootview.findViewById(R.id.progressBar1);
        setHasOptionsMenu(true);
        fileName = user.getUserID()+user.getName()+"Primary";
        try {
            FileInputStream fis = getContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            User user1 = (User) is.readObject();
            is.close();
            fis.close();
            Inbox inbox = user1.getInbox();

            Date tempDate;
            header.clear();
            String tempSubject, tempHeader, tempFrom;
            for (int i = 0; i < inbox.getPrimary().getMessages().size(); i++) {

                tempDate = inbox.getPrimary().getMessages().get(i).getDate();

                tempSubject = inbox.getPrimary().getMessages().get(i).getSubject();
                if(tempSubject == null){
                    tempSubject = "(No subject)";
                }
                person = (InternetAddress)inbox.getPrimary().getMessages().get(i).getFromAddress()[0];

                tempFrom = person.getPersonal();
                tempFrom = person.getPersonal();
                if (tempFrom == null) {
                    tempFrom = person.getAddress();
                }
                tempHeader = tempFrom + "\n" + month[tempDate.getMonth()] + " " + tempDate.getDate() + " " + (tempDate.getYear() + 1900) + "\t\t" + tempSubject;
                Log.d("header", tempHeader);

                System.out.print(tempHeader);
                header.add(tempHeader);
            }
            adapter = new ArrayAdapter<String>(getContext(), R.layout.dataview, R.id.TextView, header);
            list = rootview.findViewById(R.id.PrimaryList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent in = new Intent(getContext(), EmailDetailView.class);
                    in.putExtra("file", fileName);
                    in.putExtra("position", i);
                    startActivity(in);
                }
            });
            list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            for(com.example.convomail.Message m:user1.getInbox().getPrimary().getMessages()){
                if(m.read==false){
                    list.setItemChecked(user1.getInbox().getPrimary().getMessages().indexOf(m), true);
                }
            }

        }catch(FileNotFoundException e){ flag = 1;
         fl = false;
        }catch (Exception e){
            Log.d("cacPri", e.toString());
        }
        setRetainInstance(true);
        return rootview;

    }

    public void setInbox(Inbox inbox){
        try{
            header.clear();
            Date tempDate;
            String tempSubject, tempHeader, tempFrom;
            for (int i = 0; i < inbox.getPrimary().getMessages().size(); i++) {

                tempDate = inbox.getPrimary().getMessages().get(i).getDate();

                tempSubject = inbox.getPrimary().getMessages().get(i).getSubject();
                if(tempSubject == null){
                    tempSubject = "(No subject)";
                }
                person = (InternetAddress)inbox.getPrimary().getMessages().get(i).getFromAddress()[0];

                tempFrom = person.getPersonal();
                tempFrom = person.getPersonal();
                if (tempFrom == null) {
                    tempFrom = person.getAddress();
                }
                tempHeader = tempFrom + "\n" + month[tempDate.getMonth()] + " " + tempDate.getDate() + " " + (tempDate.getYear() + 1900) + "\t\t" + tempSubject;
                Log.d("header", tempHeader);

                System.out.print(tempHeader);
                header.add(tempHeader);
            }

            if (header.size() == 0 && !fl) {
                header.add("No new messages");
            }
//            Log.d("size", h.get(0));
            user.setInbox(inbox);
//            user.saveData(this);
            adapter = new ArrayAdapter<String>(getContext(), R.layout.dataview, R.id.TextView ,header);
            list = getView().findViewById(R.id.PrimaryList);
            list.setAdapter(adapter);
            list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            for(com.example.convomail.Message m:user.getInbox().getPrimary().getMessages()){
                if(m.read==false){
                    list.setItemChecked(user.getInbox().getPrimary().getMessages().indexOf(m), true);
                }
            }
            FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(user);
            os.close();
            fos.close();
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent in = new Intent(getContext(), EmailDetailView.class);
                    in.putExtra("file", fileName);
                    in.putExtra("position", i);
                    startActivity(in);
                }
            });
        }
        catch (Exception e){
            Log.d("ErrorPri", e.toString());
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


        @Override
        protected Inbox doInBackground(String... strings) {
            Inbox inbox = new Inbox(new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()));
            try{
                // create properties field
                String host = this.getHost(strings[0]);
                Properties properties = this.getProp(strings[0]);

                Session emailSession = Session.getDefaultInstance(properties);
                Log.d("nnn", "ss");

                // create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("imaps");
                store.connect(host, strings[0], strings[1]);


                Folder emailFolder = store.getFolder(this.getFold(strings[0]));
                emailFolder.open(Folder.READ_ONLY);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MONTH, getMessageDate);

                SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, c.getTime());



                // retrieve the messages from the folder in an array and print it
                Message[] messages;

                messages = emailFolder.search(newerThan);


                ArrayList<Message> m = new ArrayList<Message>();
                messages = reverse(messages, messages.length);

                inbox.setPrimary(messages);
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
                Log.d("err1", e.toString()) ;

            }
            return inbox;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(this.context,"Retrieving messages","Please wait...",false,false);

            if(flag==1){
                spinner.setVisibility(View.VISIBLE);

            }
        }

        protected  void onPostExecute(Inbox inbox) {

//            progressDialog.dismiss();
if(flag==1) {
    spinner.setVisibility(View.GONE);

}
            flag = 0;
setInbox(inbox);
}

        }
    }
