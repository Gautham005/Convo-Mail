package com.example.convomail;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

public class MyService extends Service {
    private static final String CHANNEL_ID = "ConvoMail";

    public MyService() {
    }

    User user;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Toast.makeText(this, "Started ", Toast.LENGTH_SHORT).show();
        String password = intent.getStringExtra("pass");
        String username = intent.getStringExtra("username");
        String name = intent.getStringExtra("Name");
        new Timer().scheduleAtFixedRate(new MyTimerTask(), 10, 10000);
        user = new User(username, password, name);
        return START_STICKY;
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            new RetrieveMessages(getApplicationContext()).execute(user.getUserID(), user.getPassword());
        }


    }

    public  void RaiseNotification() {
        Intent intent = new Intent(this, EmailList.class);
        intent.putExtra("Name", user.getName());
        intent.putExtra("username", user.getUserID());
        intent.putExtra("pass", user.getPassword());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.convomail)
                .setContentTitle("ConvoMail")
                .setContentText("You have new message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(this);
        notificationCompat.notify((int)System.currentTimeMillis(),builder.build());

    }

    class RetrieveMessages extends AsyncTask<String, Void, Inbox> {
        private Context context;
        ProgressDialog progressDialog;


        RetrieveMessages(Context c) {
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
                return "INBOX";
            } else if (s[1].equals("outlook.com")) {
                return "INBOX";
            }
            return "";
        }

        javax.mail.Message[] reverse(javax.mail.Message a[], int n) {
            javax.mail.Message[] b = new javax.mail.Message[n];
            int j = n;
            for (int i = 0; i < n; i++) {
                b[j - 1] = a[i];
                j = j - 1;
            }

            return b;
        }


        @Override
        protected Inbox doInBackground(String... strings) {
            Inbox inbox = new Inbox(new Mail(new ArrayList<Message>()), new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()), new Mail(new ArrayList<com.example.convomail.Message>()));
            try {
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
                int getMessageDate = -2;
                c.add(Calendar.MONTH, getMessageDate);

                SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, c.getTime());


                // retrieve the messages from the folder in an array and print it
                javax.mail.Message[] messages;

                messages = emailFolder.search(newerThan);


                ArrayList<javax.mail.Message> m = new ArrayList<javax.mail.Message>();
                messages = reverse(messages, messages.length);

                inbox.setPrimary(messages);
                if (emailFolder != null) {
                    emailFolder.close(false);
                }
                if (store != null) {
                    store.close();
                }

            } catch (AuthenticationFailedException e) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("Auth", "autherror");
                startActivity(i);
            } catch (Exception e) {
                Log.d("err1", e.toString());

            }
            return inbox;
        }


        protected void onPostExecute(Inbox inbox1) {
            String fileName = user.getUserID() + user.getName() + "Primary";
            try {
                FileInputStream fis = openFileInput(fileName);
                ObjectInputStream is = new ObjectInputStream(fis);
                User user1 = (User) is.readObject();
                is.close();
                fis.close();
                Inbox inbox = user1.getInbox();
                if (inbox1.getPrimary().getMessages().get(0).getMsgno() == inbox.getPrimary().getMessages().get(0).getMsgno()) {
                    Log.d("nnn","as");
                } else {
                    user1.setInbox(inbox1);
                    FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(user1);
                    os.close();
                    fos.close();
                    Log.d("nnn","qwe");

                    RaiseNotification();

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
