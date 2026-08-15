package com.controlefinaneiro.api.infra.notificacoes.listeners;


import com.controlefinaneiro.api.infra.email.EmailService;
import com.controlefinaneiro.api.infra.notificacoes.eventos.RelatorioSolicitadoEvent;
import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
import com.controlefinaneiro.api.transacao.service.RelatorioService;
import com.controlefinaneiro.api.transacao.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RelatorioEventListener {

    @Autowired
    private RelatorioService relatorioService;
    @Autowired
    private TransacaoService transacaoService;
    @Autowired
    private EmailService emailService;


    @Async("taskExecutor")
    @EventListener
    public void processarRelatorioSolicitado(RelatorioSolicitadoEvent evento){
        log.info("Iniciando processamento de relatório solicitado para {} ({}/{})",
                evento.usuario().getEmail(), evento.mes(), evento.ano());
        try {
            //busca os dados filtrados
            List<TransacaoResponse> dados = transacaoService.filtrarPorPeriodo(evento.mes(), evento.ano());

            //gera o pdf
            byte[] pdf = relatorioService.gerarRelatorioCompleto(dados, evento.mes(), evento.ano());

            String destinatario = evento.usuario().getEmail();
            String assunto = "Seu Relatório Financeiro - " + evento.mes() + "/" + evento.ano();
            String corpo =  "Olá " + evento.usuario().getNome() + ", segue em anexo o seu relatório solicitado.";
            String nomeArquivo = "Relatorio_" + evento.mes() + "_" + evento.ano() + ".pdf";

            emailService.enviarEmailComAnexo(destinatario,assunto,corpo,pdf,nomeArquivo);
            log.info("Processamento de relatório concluído para {} ({}/{})",
                    destinatario, evento.mes(), evento.ano());

        }catch (Exception e){
            log.error("Falha ao processar relatório solicitado para usuário {} ({}/{}): {}",
                    evento.usuario().getEmail(), evento.mes(), evento.ano(), e.getMessage(), e);
        }
    }
}
