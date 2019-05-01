package com.example.convomail;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class Message implements Serializable {
    Address[] fromAddress;
    Date date;
    String subject;
    String content;
    String contentType;
    ArrayList<String> attachmentFileList;
    ArrayList<String> attachmentFileType;
    boolean downloaded;
    int msgno;
    boolean read=true;

    Message(Address fromAddress[], Date d, String s, int msgno, String contentType){
        this.fromAddress = fromAddress;
        this.date = d;
        this.subject = s;
        this.contentType = contentType;
        this.msgno = msgno;if(s==null){
            this.subject = "(No subject)";
        }
        downloaded = false;
    }
    Message(Address fromAddress[], Date d, String s, int msgno, String contentType, boolean b){
        this.fromAddress = fromAddress;
        this.date = d;
        this.subject = s;
        this.contentType = contentType;
        this.msgno = msgno;if(s==null){
            this.subject = "(No subject)";
        }
        downloaded = false;
        read = b;
    }
    int getMsgno(){
        return msgno;
    }
    Address[] getFromAddress(){
        return fromAddress;
    }
    Date getDate(){
        return date;
    }
    String getSubject(){
        return subject;
    }
    String getContent(){
        return content;
    }
    String getContentType(){
        return contentType;
    }

    void setMessage(String content, ArrayList<String> f, ArrayList<String> type) {
        this.content = content;
        this.attachmentFileList = f;
        this.attachmentFileType = type;
        downloaded = true;
    }

    void setFalse() {
        downloaded = false;
    }

    void setContent(String s, String contentType){
        this.content = s;
        this.contentType = contentType;
        this.downloaded = true;
    }
}
