package uk.ac.ebi.solrReporter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class MailServiceImp implements MailService{

    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public SimpleMailMessage send() {
        Calendar cal = Calendar.getInstance();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("");
        mailMessage.setSubject("Report " + dateFormat.format(cal.getTime()));
        mailMessage.setText("");

        javaMailSender.send(mailMessage);

        return mailMessage;
    }

}
