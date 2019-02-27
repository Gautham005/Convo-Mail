package com.example.convomail;

import java.io.Serializable;
public class Login implements Serializable{
    private static final long serialVersionUID = 1874569235478615248L;

    private String userID;


    private String password;


    private String name;


    public Login(){
        this.userID = "";
        this.password = "";
        this.name = "";
    }

    public Login(String userIDIN,String passwordIN, String nname){
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