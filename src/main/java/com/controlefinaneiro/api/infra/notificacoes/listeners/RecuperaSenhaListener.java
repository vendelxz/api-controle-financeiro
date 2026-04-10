package com.controlefinaneiro.api.infra.notificacoes.listeners;

import com.controlefinaneiro.api.infra.email.EmailService;
import com.controlefinaneiro.api.infra.notificacoes.eventos.RecuperarSenhaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RecuperaSenhaListener {

    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.url}")
    private String urlFrontend;


    @Async
    @EventListener
    public void processarRecuperaSenhaEvent(RecuperarSenhaEvent evento) {
        //Link que aponta para o front
        String urlReset = urlFrontend + "auth/redefinir-senha.html?token=" + evento.token();

        String assunto = "Recuperação de Senha - Controle Financeiro";
        String corpo = String.format(
                "Olá, %s!\n\n" +
                        "Recebemos uma solicitação para redefinir a sua senha.\n" +
                        "Para prosseguir, clique no link abaixo:\n\n%s\n\n" +
                        "Este link é válido por 10 minutos. Se você não solicitou isso, ignore este e-mail.",
                evento.usuario().getNome(),
                urlReset
        );


        emailService.enviarEmail(evento.usuario().getEmail(), assunto, corpo);
    }
}
