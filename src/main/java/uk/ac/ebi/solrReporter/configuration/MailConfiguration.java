package uk.ac.ebi.solrReporter.configuration;

import com.sun.istack.internal.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties("mail")
public class MailConfiguration {

    @NotNull
    private String protocol;
    @NotNull
    private String host;
    @NotNull
    private int port;
    @NotNull
    private boolean smtpAuth;
    @NotNull
    private boolean smtpStarttlsEnable;
    @NotNull
    private String from;
    @NotNull
    private String username;
    @NotNull
    private String password;

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public void setSmtpStarttlsEnable(boolean smtpStarttlsEnable) {
        this.smtpStarttlsEnable = smtpStarttlsEnable;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", smtpAuth);
        mailProperties.put("mail.smtp.starttls.enable", smtpStarttlsEnable);
        mailProperties.put("mail.from", from);

        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        return mailSender;
    }
}
