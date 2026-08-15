package com.controlefinaneiro.api.infra.notificacoes.listeners;

import com.controlefinaneiro.api.infra.email.EmailService;
import com.controlefinaneiro.api.infra.notificacoes.eventos.RecuperarSenhaEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RecuperaSenhaListener {

    @Autowired
    private EmailService emailService;


    @Async
    @EventListener
    public void processarRecuperaSenhaEvent(RecuperarSenhaEvent evento) {
        log.info("Iniciando processamento de e-mail de recuperação de senha para {}", evento.usuario().getEmail());
        try {
            //A origem já foi validada de forma síncrona em AuthService.solicitarRecuperacao;
            //aqui só resta montar e enviar o e-mail com o link já pronto.
            String assunto = "Recuperação de Senha - Controle Financeiro";
            String corpo = String.format(
                    "Olá, %s!\n\n" +
                            "Recebemos uma solicitação para redefinir a sua senha.\n" +
                            "Para prosseguir, clique no link abaixo:\n\n%s\n\n" +
                            "Este link é válido por 10 minutos. Se você não solicitou isso, ignore este e-mail.",
                    evento.usuario().getNome(),
                    evento.urlCompleta()
            );

            emailService.enviarEmail(evento.usuario().getEmail(), assunto, corpo);
            log.info("Processamento de recuperação de senha concluído para {}", evento.usuario().getEmail());

        } catch (Exception e) {
            log.error("Falha ao processar recuperação de senha para {}: {}", evento.usuario().getEmail(), e.getMessage(), e);
        }
    }
}
