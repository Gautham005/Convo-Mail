package com.example.convomail;


        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.net.Socket;
        import java.util.Properties;
        import java.util.Vector;
        import javax.mail.Folder;
        import javax.mail.Message;
        import javax.mail.Session;
        import javax.mail.Store;
        import javax.net.SocketFactory;
//import javax.swing.JOptionPane;

public class unsecImap {



    /** The user id. */
    String userID;

    /** The password. */
    String password;
    Message[] m;

    /** The response. */
    String response;
    // create PrintWriter for sending login to server
    /** The output. */
    PrintWriter output;
    //Create BufferedReader.
    /** The input. */
    BufferedReader input;

    public unsecImap(String userIDIn, String passwordIn){
        this.userID = userIDIn;
        this.password = passwordIn;


//        try {
//            props.load(new FileInputStream(new File("C:\\smtp.properties")));
//            Session session = Session.getDefaultInstance(props, null);
//
//            Store store = session.getStore("imaps");
//            store.connect("smtp.gmail.com", "*************@gmail.com", "your_password");
////
//            Folder inbox = store.getFolder("inbox");
//            inbox.open(Folder.READ_ONLY);
//            int messageCount = inbox.getMessageCount();
//
//            System.out.println("Total Messages:- " + messageCount);
//
//            Message[] messages = inbox.getMessages();
//            System.out.println("------------------------------");
//
//            for (int i = 0; i < 10; i++) {
//                System.out.println("Mail Subject:- " + messages[i].getSubject());
//            }

//            inbox.close(true);
//            store.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

//
//    public boolean loginMethod(){
//        boolean loginSuccess = false;
//        try{
//            String loginCommand= ". login " + this.userID + " " + this.password;
//            //Sends login command to the server
//            output.println(loginCommand);
//            output.flush();
//
//            // Read response from server
//            response = input.readLine();
//            response = input.readLine();
//            //Check to see if  Username and Password are correct
//            if(!(response.contains(". NO "))){
//                loginSuccess = true;
//                // Display response to user
//                // JOptionPane.showMessageDialog( null, response );
//                response = input.readLine();
//                // JOptionPane.showMessageDialog(null, response);
//                output.println(". select INBOX");
//                output.flush();
//                response = input.readLine();
//                response = input.readLine();
//                response = input.readLine();
//
//                while(!(response.equals(". OK [READ-WRITE] SELECT completed"))){
//                    response = input.readLine();
//                }
//            }
//            else{
//                //JOptionPane.showMessageDialog( null, "ERROR: Please make sure your Username and Password are correct." );
//                System.out.println("ERROR: Please make sure your Username and PAssword are correct.");
//                loginSuccess = false;
//            }
//
//        }
//        catch(IOException e){
//            return false;
//        }
//        return loginSuccess;
//
//    }


    public Message[] fromHeader(){

        Message[] messages=null;
        Properties props = new Properties();
        try{
            props.load(new FileInputStream(new File("C:\\smtp.properties")));
            Session session = Session.getDefaultInstance(props, null);

            Store store = session.getStore("imaps");
            store.connect("smtp.gmail.com", userID, password);
            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);
            messages = inbox.getMessages();
            this.m  = messages;
            inbox.close(true);
            store.close();

        }
        catch(Exception e){
        }
        return messages;
    }


    public String fetchBody(int mailNumber){
        String messageString = null;
        String test = "";
        if(mailNumber % 2 == 0){
            mailNumber = ((mailNumber/2)+1);
            messageString = m[mailNumber].toString();

        }
        return test;

    }

//    public void logout(){
//        try{
//            socket.close();
//            input.close();
//            output.close();
//
//
//        }catch(IOException e){
//            //JOptionPane.showMessageDialog(null, "ERROR: Error closing connection.");
//            System.out.println("ERROR: Error in closing the connection.");
//            e.printStackTrace();
//            System.exit(0);
//        }
//    }


}
