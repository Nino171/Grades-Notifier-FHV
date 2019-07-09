package Quellcode_URL;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private String _username = "GradesNotifierFHV@gmail.com";
    private String _password = "Metegan123";


    public void sendMail() {
        String to = "Email1,Email2,...";  //TODO - hier werden die EMAILS eingetragen WICHTIG - mit ',' getrennt

        Address[] addresses = new Address[5];

        try {
             addresses = InternetAddress.parse(to);
        }catch (AddressException ae){
            ae.printStackTrace();
        }

        String from = _username;

        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.starttls.enable","true");
        properties.setProperty("mail.smtp.auth","true");
        properties.setProperty("mail.smtp.host",host);
        properties.setProperty("mail.smtp.port","587");


        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(_username,_password);
            }
        });

        try{
            //default meme object
            MimeMessage message = new MimeMessage(session);
            //set from header
            message.setFrom(new InternetAddress(from));
            //set to: header field of the header
            message.addRecipients(Message.RecipientType.TO,addresses);
            message.setSubject("NOTEN!!");
            message.setText("Die Noten wurden aktualisiert!");

            //send message
            Transport.send(message);

        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

}
