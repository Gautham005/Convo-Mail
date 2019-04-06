package com.example.convomail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
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

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
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
    ProgressBar spinner;
    String uname, nname, pass;
    private AppCompatCheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        spinner = findViewById(R.id.progressBarmain);
        SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (SharedPreferences.contains("name")) {
            name1 = SharedPreferences.getString("name", "");
            email1 = SharedPreferences.getString("username", "");
            password1 = SharedPreferences.getString("password", "");
            Intent in = new Intent(this, EmailList.class);
            in.putExtra("Name", name1);
            in.putExtra("username", email1);
            in.putExtra("pass", password1);
            in.putExtra("first", "false");
            startActivity(in);
        } else {
            name = findViewById(R.id.NameID);
            email = findViewById(R.id.EmailID);
            password = findViewById(R.id.PassID);
            remember_me = findViewById(R.id.checkBox);
            login = findViewById(R.id.btnLogin);
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
    }

    public void AuthError() {
        Toast t = Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG);
        t.show();

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
                android.content.SharedPreferences.Editor e = SharedPreferences.edit();
                e.putString("name", nname);
                e.putString("username", uname);
                e.putString("password", pass);
                e.apply();
            }
            i.putExtra("Name", nname);
            i.putExtra("username", uname);
            i.putExtra("pass", pass);
            i.putExtra("first", "true");
            startActivity(i);
        }
    }

    public int AuthChecker(String u, String password1) {
        new AuthCheckerTask(getApplicationContext()).execute(u, password1);
        return auth;
    }

    class AuthCheckerTask extends AsyncTask<String, Void, Boolean> {
        private Context context;
        ProgressDialog progressDialog;

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

        private String getFold(String user) {
            String[] s = user.split("@");

            if (s[1].equals("gmail.com")) {
                return "[Gmail]/Trash";
            } else if (s[1].equals("outlook.com")) {
                return "Deleted";
            }
            return "";
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


                Folder emailFolder = store.getFolder(this.getFold(strings[0]));
                emailFolder.open(Folder.READ_ONLY);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        System.in));

                // retrieve the messages from the folder in an array and print it

                if (emailFolder != null) {
                    emailFolder.close(false);
                }
                if (store != null) {
                    store.close();
                }

            } catch (AuthenticationFailedException e) {
                Log.d("log", "!1");
                return true;
            } catch (Exception e) {
                Log.d("err", e.toString());

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
