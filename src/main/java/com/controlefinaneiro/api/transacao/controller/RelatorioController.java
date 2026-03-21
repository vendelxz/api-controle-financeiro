package com.controlefinaneiro.api.transacao.controller;



import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
import com.controlefinaneiro.api.transacao.service.RelatorioService;
import com.controlefinaneiro.api.transacao.service.TransacaoService;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    //Fica faltando o método de enviar por email
}
