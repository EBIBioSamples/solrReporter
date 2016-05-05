package uk.ac.ebi.solrReporter.services;


import org.springframework.mail.SimpleMailMessage;

public interface MailService {

    SimpleMailMessage send();
}
