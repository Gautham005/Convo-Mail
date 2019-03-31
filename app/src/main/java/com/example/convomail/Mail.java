package com.example.convomail;

import java.io.Serializable;
import java.util.ArrayList;


public class Mail implements Serializable {
    ArrayList<Message> Messages;
    Mail(ArrayList<Message> m){
        this.Messages = m;
    }
    public void setMessages(ArrayList<Message> a){
        this.Messages = a;
    }
    public ArrayList<Message> getMessages(){
        return Messages;
    }
//    public String toString(){
//        ArrayList<String> m = new ArrayList<String>();
//        for( )
//    }
}
