package com.controlefinaneiro.api.infra.notificacoes.listeners;

import com.controlefinaneiro.api.infra.email.EmailService;
import com.controlefinaneiro.api.infra.exceptions.OrigemInvalidaException;
import com.controlefinaneiro.api.infra.notificacoes.eventos.RecuperarSenhaEvent;
import com.controlefinaneiro.api.infra.utils.OriginValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RecuperaSenhaListener {

    @Autowired
    private EmailService emailService;


    @Async
    @EventListener
    public void processarRecuperaSenhaEvent(RecuperarSenhaEvent evento) {
        //Link que aponta para o front

        String urlBase = evento.origem();
        //Pega a URL de acordo com o que vem...
        String urlReset = OriginValidator.validadorDeOrigem(urlBase);

        if(urlReset == null){
            //Se for nulo trata para não estourar NullPointerException...
            throw new OrigemInvalidaException("Origem não identificada para montar o link.");
        }

        String urlCompleta = urlBase + urlReset + evento.token();

        String assunto = "Recuperação de Senha - Controle Financeiro";
        String corpo = String.format(
                "Olá, %s!\n\n" +
                        "Recebemos uma solicitação para redefinir a sua senha.\n" +
                        "Para prosseguir, clique no link abaixo:\n\n%s\n\n" +
                        "Este link é válido por 10 minutos. Se você não solicitou isso, ignore este e-mail.",
                evento.usuario().getNome(),
                urlCompleta
        );


        emailService.enviarEmail(evento.usuario().getEmail(), assunto, corpo);
    }
}
