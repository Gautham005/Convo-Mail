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
