package me.asreal.markgenius.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

    @Value("${spring.mail.username}")
    private String emailUsername;
    @Value("${spring.mail.password}")
    private String emailPassword;

    //Send mail from spring to user inbox
    @Bean
    public JavaMailSender javaMailSender() {
        var mailSender = new JavaMailSenderImpl();
        //Configure mail sender
        mailSender.setHost("smtp.gmail.com");//gmail host
        mailSender.setPort(587);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);
        //Configure properties
        var mailProperties = mailSender.getJavaMailProperties();
        mailProperties.put("mail.transport.protocol", "smtp");
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.debug", "true");

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }
}
