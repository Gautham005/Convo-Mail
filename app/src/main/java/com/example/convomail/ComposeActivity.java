package com.example.convomail;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
    EditText subject = null;
    EditText compose = null;
    User user;
    int i = 0;
    RelativeLayout att1, att2;
    ImageButton rm1, rm2;
    TextView attn1, attn2, ext;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    AutoCompleteTextView to;
    String type;
    int msgno;
    private static final int PICKFILE_RESULT_CODE = 1;
    String folder;
    String repl;
    ArrayList<String> fileNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Toolbar toolbar = findViewById(R.id.toolbar);
        from = findViewById(R.id.from);
        newIntent = getIntent();
        att1 = findViewById(R.id.attachment1);
        att2 = findViewById(R.id.attachment2);
        attn1 = findViewById(R.id.attachment_name1);
        attn2 = findViewById(R.id.attachment_name2);
        ext = findViewById(R.id.extraatt);
        rm1 = findViewById(R.id.remove_attachment1);
        rm2 = findViewById(R.id.remove_attachment2);
        String password = newIntent.getStringExtra("pass");
        String username = newIntent.getStringExtra("username");
        String name = newIntent.getStringExtra("Name");
        from.setText(username);
        to = findViewById(R.id.to);
        to.setThreshold(1);
        subject = findViewById(R.id.subject);
        compose = findViewById(R.id.compose);
        user = new User(username, password, name);
        type = newIntent.getStringExtra("type");
        if (type.equals("Reply")) {
            repl = newIntent.getStringExtra("replyto");
            String sub = newIntent.getStringExtra("subject");
            subject.setText("RE:" + sub);
            to.setText(repl);
            Log.d("repl", repl);
            String msg = newIntent.getStringExtra("msgno");
            msgno = Integer.parseInt(msg);
            folder = newIntent.getStringExtra("folder");
        } else if (type.equals("Forward")) {
            String sub = newIntent.getStringExtra("subject");
            subject.setText("Fwd: " + sub);
            String msg = newIntent.getStringExtra("msgno");
            msgno = Integer.parseInt(msg);
            folder = newIntent.getStringExtra("folder");
            compose.setText(newIntent.getStringExtra("content"));

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 200
            );

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveAttachment(0);
            }
        });
        rm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveAttachment(1);
            }
        });
        addAdapterToViews();
    }


    public ArrayList<String> getNameEmailDetails() {
        ArrayList<String> names = new ArrayList<String>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.e("Name :", name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.e("Email", email);
                    if (email != null) {
                        names.add(email);
                    }
                }
                cur1.close();
            }
        }
        return names;
    }
    private void addAdapterToViews() {
        Log.d("emails", "1");


        to.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getNameEmailDetails()));

    }
    public void RemoveAttachment(int i) {
        fileNames.remove(i);
        setAttachment();
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
        if (id == R.id.send) {
            SendMail("Send");
            Log.d("To", to.getText().toString());
            if(to.getText().equals("")){
                Toast.makeText(this, "Enter a valid email id", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.attach) {
            PickFile();
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

    public void PickFile() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200
            );

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    File f = new File(FilePath);
                    Uri u = Uri.fromFile(f);
                    Log.d("File", f.getAbsolutePath());

                    String[] s = u.getPath().split(":");
                    if (s[1].contains("/storage/emulated/0")) {
                        if(fileNames.contains(s[1])){
                            Toast.makeText(this, "Attachment already added", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            fileNames.add(s[1]);
                        }
                    } else {
                        if(fileNames.contains("/storage/emulated/0/" + s[1])){
                            Toast.makeText(this, "Attachment already added", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            fileNames.add("/storage/emulated/0/" + s[1]);
                        }
                    }
                    Log.d("filename", fileNames.size()+"");
                    setAttachment();

                }
        }
    }
    public void setAttachment(){
        att1.setVisibility(View.GONE);
        att2.setVisibility(View.GONE);
        ext.setVisibility(View.GONE);
        if (fileNames.size() == 1) {
            att1.setVisibility(View.VISIBLE);
            String s[] = fileNames.get(0).split("/");
            attn1.setText(s[s.length-1]);
            attn1.setVisibility(View.VISIBLE);
            i++;
        }
        if (fileNames.size() == 2) {
            att1.setVisibility(View.VISIBLE);
            String s[] = fileNames.get(0).split("/");
            attn1.setText(s[s.length-1]);
            attn1.setVisibility(View.VISIBLE);
            att2.setVisibility(View.VISIBLE);
            s = fileNames.get(1).split("/");
            attn2.setText(s[s.length-1]);
            attn2.setVisibility(View.VISIBLE);
            i++;
        } else if(fileNames.size()>2){
            att1.setVisibility(View.VISIBLE);
            String s[] = fileNames.get(0).split("/");
            attn1.setText(s[s.length-1]);
            attn1.setVisibility(View.VISIBLE);
            att2.setVisibility(View.VISIBLE);
            s = fileNames.get(1).split("/");
            attn2.setText(s[s.length-1]);
            attn2.setVisibility(View.VISIBLE);
            String str = "And " + (fileNames.size() - 2) + "more files";
            ext.setText(str);
            ext.setVisibility(View.VISIBLE);
        }
    }
    public void SendMail(String inp) {
        String toAddress = to.getText().toString();
        Log.d("Toaddress", toAddress);
        if (to.getText() == null) {
            Toast.makeText(this, "Enter a valid email id", Toast.LENGTH_SHORT).show();
        }
        else {
            String subject1 = subject.getText().toString();
            String messagebody = compose.getText().toString();
            new SendMailTask(this.getApplicationContext()).execute(user.getUserID(), user.getPassword(), toAddress, subject1, messagebody, inp);
        }

    }


    class SendMailTask extends AsyncTask<String, Void, Integer> {
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

        private String getFold1(String user) {
            String[] s = user.split("@");

            if (s[1].equals("gmail.com")) {

                if (folder.contains("Primary")) {
                    return "INBOX";
                } else if (folder.contains("Draft")) {
                    return "[Gmail]/Drafts";
                } else if (folder.contains("Spam")) {
                    return "[Gmail]/Spam";
                } else if (folder.contains("Trash")) {
                    return "[Gmail]/Trash";
                } else if (folder.contains("SentMail")) {
                    return "[Gmail]/Sent Mail";
                }
            } else if (s[1].equals("outlook.com")) {
                if (folder.contains("Primary")) {
                    return "INBOX";
                } else if (folder.contains("Draft")) {
                    return "Drafts";
                } else if (folder.contains("Spam")) {
                    return "Junk";
                } else if (folder.contains("Trash")) {
                    return "Deleted";
                } else if (folder.contains("SentMail")) {
                    return "Sent";
                }
            }
            return "";
        }

        protected Integer doInBackground(String... strings) {
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
                if (type.equals("Compose")) {
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(to));
                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(username));

                    // Set To: header field of the header.

                    // Set Subject: header field
                    message.setSubject(strings[3]);

                    // Create the message part
                    BodyPart messageBodyPart = new MimeBodyPart();

                    // Now set the actual message
                    messageBodyPart.setText(strings[4]);

                    // Create a multipart message
                    Multipart multipart = new MimeMultipart();

                    // Set text message part
                    multipart.addBodyPart(messageBodyPart);
                    //                        Part two is attachment
                    if (!fileNames.isEmpty()) {
                        for (String filename : fileNames) {
                            Log.d("fileName", fileNames.size()+"");
                            DataSource source = new FileDataSource(filename);
                            messageBodyPart.setDataHandler(new DataHandler(source));
                            messageBodyPart.setFileName(new File(filename).getName());
                            multipart.addBodyPart(messageBodyPart);
                        }
                    }

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
                        return 2;
                    }
                } else if (type.equals("Reply")) {
                    Store imapsStore = session.getStore("imaps");
                    imapsStore.connect(host, strings[0], strings[1]);
                    Folder replyFolder = imapsStore.getFolder(getFold1(username));
                    replyFolder.open(Folder.READ_ONLY);

                    Message m = replyFolder.getMessage(msgno);
                    String subject = m.getSubject();
                    message = (MimeMessage) m.reply(false);
                    message.setReplyTo(m.getReplyTo());
                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(username));

                    // Set To: header field of the header.

                    // Set Subject: header field
                    message.setSubject(strings[3]);

                    // Create the message part
                    BodyPart messageBodyPart = new MimeBodyPart();

                    // Now set the actual message
                    messageBodyPart.setText(strings[4]);

                    // Create a multipart message
                    Multipart multipart = new MimeMultipart();

                    // Set text message part
                    multipart.addBodyPart(messageBodyPart);
                    //                        Part two is attachment
                    if (!fileNames.isEmpty()) {
                        for (String filename : fileNames) {

                            DataSource source = new FileDataSource(filename);
                            messageBodyPart.setDataHandler(new DataHandler(source));
                            messageBodyPart.setFileName(new File(filename).getName());
                            multipart.addBodyPart(messageBodyPart);
                        }
                    }

                    // Send the complete message parts
                    message.setContent(multipart);
                    if (strings[5].equals("Send")) {
                        // Send message
                        Transport t = session.getTransport("smtp");
                        t.connect(strings[0], strings[1]);
                        t.sendMessage(message, message.getAllRecipients());
                        t.close();
                        replyFolder.close(true);
                        System.out.println("Sent message successfully....");

                    } else {
                        Store imapsStore1 = session.getStore("imaps");
                        imapsStore1.connect(host, strings[0], strings[1]);
                        Folder draftsMailBoxFolder = imapsStore.getFolder(getFold(username));//[Gmail]/Drafts
                        draftsMailBoxFolder.open(Folder.READ_WRITE);
                        message.setFlag(Flags.Flag.DRAFT, true);
                        MimeMessage draftMessages[] = {message};
                        draftsMailBoxFolder.appendMessages(draftMessages);
                        System.out.println("Message saved to draft");
                        return 2;
                    }
                } else if (type.contains("Forward")) {
                    Store imapsStore2 = session.getStore("imaps");
                    imapsStore2.connect(host, strings[0], strings[1]);
                    Folder forwardFolder = imapsStore2.getFolder(getFold1(username));
                    forwardFolder.open(Folder.READ_ONLY);
                    Message m = forwardFolder.getMessage(msgno);


                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(to));
                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(username));

                    // Set To: header field of the header.

                    // Set Subject: header field
                    message.setSubject(strings[3]);

                    // Create the message part
                    MimeBodyPart messageBodyForwardPart = new MimeBodyPart();
                    // Now set the actual message
//                    messageBodyPart.setText(strings[4]);
                    // Create a multipart message
                    Multipart multipart = new MimeMultipart();
                    messageBodyForwardPart.setText("Oiginal message:\n\n");
                    messageBodyForwardPart.setContent(m, "message/rfc822");
                    messageBodyForwardPart.setDataHandler(m.getDataHandler());
                    multipart.addBodyPart(messageBodyForwardPart);


                    message.setContent(multipart);
                    if (strings[5].equals("Send")) {
                        // Send message
                        Transport t = session.getTransport("smtp");
                        t.connect(strings[0], strings[1]);
                        t.sendMessage(message, message.getAllRecipients());
                        t.close();
                        forwardFolder.close(true);
                        System.out.println("Forwarded message successfully....");

                    } else {
                        Store imapsStore = session.getStore("imaps");
                        imapsStore.connect(host, strings[0], strings[1]);
                        Folder draftsMailBoxFolder = imapsStore.getFolder(getFold(username));//[Gmail]/Drafts
                        draftsMailBoxFolder.open(Folder.READ_WRITE);
                        message.setFlag(Flags.Flag.DRAFT, true);
                        MimeMessage draftMessages[] = {message};
                        draftsMailBoxFolder.appendMessages(draftMessages);
                        System.out.println("Message saved to draft");
                        return 2;
                    }
                }


                return 1;
            } catch (Exception e) {
                Log.d("SendError", e.toString());
                return 0;
            }
        }

        protected void onPostExecute(Integer b) {
            if (b == 1) {
                Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            } else if (b == 0) {
                Toast.makeText(getApplicationContext(), "Message sending failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Message saved to drafts", Toast.LENGTH_LONG).show();

            }

        }
    }
}
