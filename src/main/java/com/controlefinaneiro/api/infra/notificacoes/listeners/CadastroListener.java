package com.controlefinaneiro.api.infra.notificacoes.listeners;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.controlefinaneiro.api.infra.email.EmailService;
import com.controlefinaneiro.api.infra.notificacoes.eventos.UsuarioCadastradoEvent;

@Component
public class CadastroListener {

    //Pode seguir esse padrão para todos os eventos do sistema.
    @Autowired
    EmailService emailService;

    @Async //Garante que ele executa a requisição primeiro e depois manda o e-mail
    @EventListener
    public void processarEmailCadastro(UsuarioCadastradoEvent evento){
        try {
            String destinatario = evento.usuario().getEmail();
            String assunto = "Bem vindo ao Controle Financeiro";
            String mensagem = "Olá! Seu cadastro foi realizado com sucesso. Comece a gerenciar suas finanças agora mesmo! \n\n" +
                                "Atenciosamente, Equipe de desenvolvimento";

            emailService.enviarEmail(destinatario, assunto, mensagem);
           
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }

    }

}
