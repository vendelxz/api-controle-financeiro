package com.controlefinaneiro.api.transacao.controller;



import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
import com.controlefinaneiro.api.transacao.service.RelatorioService;
import com.controlefinaneiro.api.transacao.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private TransacaoService transacaoService;


    //Esse é pra download direto
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam int mes, @RequestParam int ano) {
        List<TransacaoResponse> dados = transacaoService.filtrarPorPeriodo(mes,ano);

        byte[] pdf = relatorioService.gerarRelatorioCompleto(dados,mes,ano);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=relatorioFinanceiro.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }

    //Esse é o método de enviar por email
    @PostMapping("/email")
    public ResponseEntity<String> enviarRelatorioEmail(@RequestParam int mes, @RequestParam int ano) {
        relatorioService.solicitarRelatorioEmail(mes,ano);
        return ResponseEntity.accepted().build(); //Indica que recebeu e esta processando --> 202
    }
}
