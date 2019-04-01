package com.example.convomail;

import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
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
    int msgno;
    Message(Address fromAddress[], Date d, String s, Object c, String contentType){
        this.fromAddress = fromAddress;
        this.date = d;
        this.contentType = contentType;
        this.subject = s;
        if(s==null){
            this.subject = "(No subject)";
        }
        try{
            if(contentType == "text/plain"){
                this.content = c.toString();
            }
            else{
                MimeMultipart contentmsg = (MimeMultipart) c;
                this.content = getTextFromMimeMultipart(contentmsg);
            }
        }catch (Exception e){
            Log.d("ERr", e.toString());
        }
    }
    Message(Address fromAddress[], Date d, String s, int msgno, String contentType){
        this.fromAddress = fromAddress;
        this.date = d;
        this.subject = s;
        this.contentType = contentType;
        this.msgno = msgno;if(s==null){
            this.subject = "(No subject)";
        }
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


    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }
}
