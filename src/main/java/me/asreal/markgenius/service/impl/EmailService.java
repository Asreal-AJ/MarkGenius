package me.asreal.markgenius.service.impl;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired //TODO: Not recc figure alternative
    private JavaMailSender mailSender;

    public void sendEmail(String receiver, String subject, String content) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true);//Multipart allows for attachments and html content
        //Set helper properties
        helper.setTo(receiver);
        helper.setSubject(subject);
        helper.setText(content, true);//HTML allows for customized email messages

        mailSender.send(message);
    }

}
