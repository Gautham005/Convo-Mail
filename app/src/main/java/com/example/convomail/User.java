package com.example.convomail;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
public class User implements Serializable{

    private String userID;


    private String password;


    private String name;
    Inbox inbox;

    public User(){
        this.userID = "";
        this.password = "";
        this.name = "";
    }

    public User(String userIDIN, String passwordIN, String nname){
        this.userID = userIDIN;
        this.password = passwordIN;
        this.name = nname;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public Inbox getInbox() {
        return inbox;
    }

    public void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }
    private static File mFolder;

    public void saveData(Activity pContext) {

        //this could be initialized once onstart up
        if(mFolder == null){
            mFolder = pContext.getExternalFilesDir(null);
        }
        ObjectOutput out;
        try {
            File outFile = new File(mFolder,
                    "Mail.data");
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            Log.d("user", e.toString());
        }
    }

    public void loadData(Context pContext) {
        if (mFolder == null) {
            mFolder = pContext.getExternalFilesDir(null);
        }
        ObjectInput in;
        User lUser = null;
        try {
            FileInputStream fileIn = new FileInputStream(mFolder.getPath() + File.separator + "Mail.data");
            in = new ObjectInputStream(fileIn);
            lUser = (User) in.readObject();
            in.close();
        } catch (Exception e) {
            Log.d("user", e.toString());
        }
        if (lUser != null) {
            this.setUser(lUser);
        }
    }

    public void setUser(User u){
        this.name = u.getName();
        this.userID = u.getUserID();
        this.password = u.getPassword();
        this.inbox = u.getInbox();
    }
    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

}