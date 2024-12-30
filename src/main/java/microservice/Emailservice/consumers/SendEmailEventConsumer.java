package microservice.Emailservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.Emailservice.dtos.SendEmailDto;
import microservice.Emailservice.utils.EmailUtil;
import org.apache.kafka.common.network.Send;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;


import java.util.Properties;

@Service
public class SendEmailEventConsumer {
    private ObjectMapper objectMapper;
    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    // to triggers the method when an event received from kafka.
    @KafkaListener(topics="sendEmail",groupId = "emailService") // this annotation used to trigger a method when any event of particular type is occured.
    public void handleSendEmailEvent(String message) throws JsonProcessingException {// so this method keep on listening to sendEmail event form kafka, if occured then this method gets triggered.

        SendEmailDto sendEmailDto=objectMapper.readValue(  // it reads the message and Converts this  message into object of this SendEmailDto.class
                message,
                SendEmailDto.class
        );

        //after converting message(JSON) to object we get the details of user and we assign it to the Dto we have to use the user details to send email
        String to =sendEmailDto.getTo();
        String subject = sendEmailDto.getSubject();
        String body=sendEmailDto.getBody();


        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("support@scaler.com", "support4520");//these are the credentials needed for thr authentication of the entity who is sending the email.the password is of app passowrd not gmail passowrd, you need to create app passowrd the gmail account and need to use that app password.
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, to, subject,body); // sending the email to with to "to email" and with subject and body from the info we got in dto.





    }
}
