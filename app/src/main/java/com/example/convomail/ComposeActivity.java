package com.example.convomail;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ComposeActivity extends AppCompatActivity {
    Intent newIntent;
    TextView from = null;
    EditText to = null;
    EditText subject = null;
    EditText compose = null;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Toolbar toolbar = findViewById(R.id.toolbar);
        from = findViewById(R.id.from);
        newIntent = getIntent();

        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");
        from.setText(username);
        to = findViewById(R.id.to);
        subject = findViewById(R.id.subject);
        compose = findViewById(R.id.compose);
        user = new User(username, password, name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (to.getText() != null && subject.getText() != null && compose.getText() != null) {
            SendMail("Draft");
        }
        // Showing Alert Message
        Intent intent = new Intent(this, EmailList.class);
        intent.putExtra("Name", user.getName());
        intent.putExtra("username", user.getUserID());
        intent.putExtra("pass", user.getPassword());
        intent.putExtra("first", "false");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.send) {
            SendMail("Send");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.attach) {
            Snackbar.make(this.findViewById(R.id.attach), "Attach a file", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        } else if (id == android.R.id.home) {
            // todo: goto back activity from here

            if (to.getText() != null && subject.getText() != null && compose.getText() != null) {
                SendMail("Draft");
            }
            // Showing Alert Message
            Intent intent = new Intent(this, EmailList.class);
            intent.putExtra("Name", user.getName());
            intent.putExtra("username", user.getUserID());
            intent.putExtra("pass", user.getPassword());
            intent.putExtra("first", "false");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void SendMail(String inp) {
        String toAddress = to.getText().toString();
        String subject1 = subject.getText().toString();
        String messagebody = compose.getText().toString();
        new SendMailTask(this.getApplicationContext()).execute(user.getUserID(), user.getPassword(), toAddress, subject1, messagebody, inp);

    }

    protected void onPostExceute(Boolean b) {
        if (b) {
            Toast.makeText(this, "Message sent", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show();
        }
    }

    class SendMailTask extends AsyncTask<String, Void, Boolean> {
        Context context;

        SendMailTask(Context context) {
            this.context = context;
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
                return "[Gmail]/Drafts";
            } else if (s[1].equals("outlook.com")) {
                return "Drafts";
            }
            return "";
        }
        protected Boolean doInBackground(String... strings) {
            // Recipient's email ID needs to be mentioned.
            String to = strings[2];


            final String username = strings[0];
            final String password = strings[1];

            // Assuming you are sending email through relay.jangosmtp.net
            String host = this.getHost(strings[0]);
            Properties properties = this.getProp(strings[0]);

            // Get the Session object.
            Session session = Session.getInstance(properties,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(username));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject(strings[3]);

                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();

                // Now set the actual message
                messageBodyPart.setText(strings[4]);

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is attachment
//                messageBodyPart = new MimeBodyPart();
//                String filename = "/home/manisha/file.txt";
//                DataSource source = new FileDataSource(filename);
//                messageBodyPart.setDataHandler(new DataHandler(source));
//                messageBodyPart.setFileName(filename);
//                multipart.addBodyPart(messageBodyPart);

                // Send the complete message parts
                message.setContent(multipart);

                if (strings[5].equals("Send")) {
                    // Send message
                    Transport.send(message);
                    System.out.println("Sent message successfully....");

                } else {
                    Store imapsStore = session.getStore("imaps");
                    imapsStore.connect(host, strings[0], strings[1]);
                    Folder draftsMailBoxFolder = imapsStore.getFolder(getFold(username));//[Gmail]/Drafts
                    draftsMailBoxFolder.open(Folder.READ_WRITE);
                    message.setFlag(Flags.Flag.DRAFT, true);
                    MimeMessage draftMessages[] = {message};
                    draftsMailBoxFolder.appendMessages(draftMessages);
                    System.out.println("Message saved to draft");

                }

                return true;
            } catch (Exception e) {
                Log.d("SendError", e.toString());
                return false;
            }
        }
    }
}
