package com.example.convomail;

import java.io.File;
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