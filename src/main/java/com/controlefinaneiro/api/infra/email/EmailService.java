package com.controlefinaneiro.api.infra.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailADM;


    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmail(String destinatario, String assunto, String corpo ){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailADM);
            message.setTo(destinatario);
            message.setSubject(assunto);
            message.setText(corpo);

            mailSender.send(message);
            
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }




}
