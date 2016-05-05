package uk.ac.ebi.solrReporter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImp implements MailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public SimpleMailMessage send() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("");
        mailMessage.setFrom("");
        mailMessage.setSubject("");
        mailMessage.setText("");

        javaMailSender.send(mailMessage);

        return mailMessage;
    }
}
