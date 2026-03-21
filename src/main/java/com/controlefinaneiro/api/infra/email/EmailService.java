package com.controlefinaneiro.api.infra.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailADM;


    @Autowired
    private JavaMailSender mailSender;

    //Troquei aqui pelo Mime pq ele faz a mesma coisa, então não fazia sentido ficar com duas bibliotecas diferentes
    public void enviarEmail(String destinatario, String assunto, String corpo){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(emailADM);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo);

            mailSender.send(message);
            
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }

    public void enviarEmailComAnexo(String destinatario, String assunto, String corpo, byte[] anexo, String nomeArquivo){
        try{

            //O simpleMail não suporta a parte de enviar arquivo como anexo, precisei trocar
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailADM);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo);

            helper.addAttachment(nomeArquivo, new ByteArrayResource(anexo));
            mailSender.send(message);

        }catch (Exception e){
            throw new RuntimeException("Erro ao enviar e-mail com anexo", e);
        }
    }




}
