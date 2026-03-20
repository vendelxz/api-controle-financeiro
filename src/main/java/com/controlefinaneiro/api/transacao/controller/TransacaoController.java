package com.controlefinaneiro.api.transacao.controller;


import com.controlefinaneiro.api.transacao.dtos.TransacaoDTO;
import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
import com.controlefinaneiro.api.transacao.service.TransacaoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;


    @PostMapping("/criar")
    public ResponseEntity<TransacaoResponse> criar(@Valid @RequestBody TransacaoDTO transacaoDTO) {
        TransacaoResponse novaTransacao = transacaoService.criar(transacaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<TransacaoResponse>> listarPorUsuario() {
        List<TransacaoResponse> listaTransacoes = transacaoService.filtrarPorPeriodo(
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear());
        return ResponseEntity.ok(listaTransacoes);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        transacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody TransacaoDTO transacaoDTO
             ){

        TransacaoResponse atualizada = transacaoService.atualizar(id, transacaoDTO);
        return ResponseEntity.ok(atualizada);
    }

    //O token se encarrega de saber de quem vai ser o balanço, não há necessidade de passar ID
    @GetMapping("/balanco")
    public ResponseEntity<BigDecimal> obterBalanco() {
        BigDecimal balanco = transacaoService.calcularBalanco();
        return ResponseEntity.ok(balanco);
    }


    @GetMapping("/filtro")
    public ResponseEntity<List<TransacaoResponse>> filtrar(
            @RequestParam int mes,
            @RequestParam int ano) {

        List<TransacaoResponse> filtrados = transacaoService.filtrarPorPeriodo( mes, ano);
        return ResponseEntity.ok(filtrados);
    }




}
