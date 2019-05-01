package com.example.convomail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "myPrefsFile";
    public SharedPreferences SharedPreferences;

    private EditText name;
    private EditText email;
    private EditText password;
    private String name1, email1, password1;
    private CheckBox remember_me;
    private Button login;
    int auth = -1;
    String s;
    ProgressBar spinner;
    String uname, nname, pass;
    private CheckBox checkbox;
    boolean notPressed = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        spinner = findViewById(R.id.progressBarmain);
        SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        s = i.getStringExtra("Type");
        if(s==null) {
            if (SharedPreferences.contains("name")) {
                name1 = SharedPreferences.getString("name", "");
                email1 = SharedPreferences.getString("username", "");
                password1 = SharedPreferences.getString("password", "");
                String names[] = name1.split("::");
                String usernames[] = email1.split("::");
                String passwords[] = password1.split("::");
                name1 = names[0];
                email1 = usernames[0];
                password1 = passwords[0];
                Intent in = new Intent(this, EmailList.class);
                in.putExtra("Name", name1);
                in.putExtra("username", email1);
                in.putExtra("pass", password1);
                in.putExtra("first", "false");
                startActivity(in);
            } else {
                name = findViewById(R.id.name);
                email = findViewById(R.id.userEmailId);
                password = findViewById(R.id.password);
                remember_me = findViewById(R.id.checkBox);
                login = findViewById(R.id.signUpBtn);
                checkbox = findViewById(R.id.checkBox1);
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            // show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            // hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                    }
                });
            }
        }
        else if(s.equals("Addaccount")){
            name = findViewById(R.id.name);
            email = findViewById(R.id.userEmailId);
            password = findViewById(R.id.password);
            remember_me = findViewById(R.id.checkBox);
            login = findViewById(R.id.signUpBtn);
            checkbox = findViewById(R.id.checkBox1);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        // show password
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        // hide password
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });
        }
    }

    public void launchEmailList(View view) {
        if (notPressed) {
            nname = name.getText().toString();
            uname = email.getText().toString();
            pass = password.getText().toString();
            if (!uname.equals("") && !pass.equals("")) {
                spinner.setVisibility(View.VISIBLE);
                int b = AuthChecker(uname, pass);

            } else {
                Toast t = Toast.makeText(getApplicationContext(), "Please enter email id and password", Toast.LENGTH_LONG);
                t.show();
            }
            notPressed = false;
        }
    }

    public void AuthError() {
        Toast t = Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG);
        t.show();

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void setAuth(Boolean b) {
        if(b){
            AuthError();
        }
        else{
            String action;
            Uri uri;
            Intent i = new Intent(this, EmailList.class);
            if (remember_me.isChecked()) {
                if(s==null){
                    android.content.SharedPreferences.Editor e = SharedPreferences.edit();
                    e.putString("name", nname);
                    e.putString("username", uname);
                    e.putString("password", pass);
                    e.apply();
                }
                else if(s.equals("Addaccount")){
                    String n = SharedPreferences.getString("name","");
                    String u = SharedPreferences.getString("username", "");
                    String p = SharedPreferences.getString("password","");
                    if(getPosition(u.split("::"), nname)==-1){
                        String namenew = n + "::" + nname;
                        String usernew = u + "::" + uname;
                        String passnew = p + "::" + pass;
                        android.content.SharedPreferences.Editor e = SharedPreferences.edit();
                        e.putString("name", namenew);
                        e.putString("username", usernew);
                        e.putString("password", passnew);
                        e.apply();
                    }
                    else{
                        Toast.makeText(this, "This account is already added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            i.putExtra("Name", nname);
            i.putExtra("username", uname);
            i.putExtra("pass", pass);
            i.putExtra("first", "true");
            startActivity(i);
        }
    }
    public int getPosition(String[] username, String u){
        for(int i=0;i<username.length;i++){
            if(username[i].equals(u)){
                return i;
            }
        }
        return -1;
    }
    public int AuthChecker(String u, String password1) {
        new AuthCheckerTask(getApplicationContext()).execute(u, password1);
        return auth;
    }

    class AuthCheckerTask extends AsyncTask<String, Void, Boolean> {
        private Context context;

        AuthCheckerTask(Context c) {
            this.context = c;
        }

        private String getHost(String user) {
            String[] s = user.split("@");
            Properties properties = new Properties();
            if (s[1].equals("gmail.com")) {
                return "smtp.gmail.com";
            } else if (s[1].equals("outlook.com")) {
                return "smtp.office365.com";
            }
            return "";
        }

        private Properties getProp(String user) {
            String[] s = user.split("@");
            Properties properties = new Properties();
            if (s[1].equals("gmail.com")) {
                properties.put("mail.smtp.host", this.getHost(user));
                properties.put("mail.smtp.socketFactory.port", "587");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
            } else if (s[1].equals("outlook.com")) {
                properties.put("mail.smtp.host", this.getHost(user));
                properties.put("mail.smtp.socketFactory.port", "465");
                properties.put("mail.smtp.port", "465");
                properties.put("mail.smtp.auth", "true");
            }
            return properties;
        }


        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                // create properties field
                String host = this.getHost(strings[0]);
                Properties properties = this.getProp(strings[0]);

                Session emailSession = Session.getDefaultInstance(properties);
                // emailSession.setDebug(true);

                // create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("imaps");
                store.connect(host, strings[0], strings[1]);


//                Folder emailFolder = store.getFolder(this.getFold(strings[0]));
//                emailFolder.open(Folder.READ_ONLY);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        System.in));

                // retrieve the messages from the folder in an array and print it

//                if (emailFolder != null) {
//                    emailFolder.close(false);
//                }
                if (store != null) {
                    store.close();
                }

            } catch (Exception e) {
                Log.d("err", e.toString());
                return true;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            spinner.setVisibility(View.VISIBLE);


        }

        protected void onPostExecute(Boolean b) {


            spinner.setVisibility(View.GONE);
            setAuth(b);

        }
    }
}
