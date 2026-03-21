package com.controlefinaneiro.api.infra.seguranca;


import com.controlefinaneiro.api.usuario.repository.TokenRecuperacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class ClearToken {

    @Autowired
    private TokenRecuperacaoRepository tokenRepository;

    //Vai limpar todos os tokens que já expiraram à meia noite de todos os dias
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deletarTokenExpirados(){
        tokenRepository.deleteByDataExpiracaoBefore(LocalDateTime.now());
    }
}
