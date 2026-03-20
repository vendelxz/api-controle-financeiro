package com.controlefinaneiro.api.transacao.controller;


import com.controlefinaneiro.api.transacao.dtos.TransacaoDTO;
import com.controlefinaneiro.api.transacao.service.TransacaoService;
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


    @PostMapping
    public ResponseEntity<TransacaoDTO> criar(@RequestBody TransacaoDTO transacaoDTO) {
        TransacaoDTO novaTransacao = transacaoService.criar(transacaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<TransacaoDTO>> listarPorUsuario(@PathVariable UUID idUsuario) {
        List<TransacaoDTO> listaTransacoes = transacaoService.filtrarPorPeriodo(idUsuario,
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear());
        return ResponseEntity.ok(listaTransacoes);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id, UUID idUsuarioLogado) {
        transacaoService.deletar(id, idUsuarioLogado);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<TransacaoDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody TransacaoDTO transacaoDTO,
             UUID idUsuarioLogado){

        TransacaoDTO atualizada = transacaoService.atualizar(id, transacaoDTO, idUsuarioLogado);
        return ResponseEntity.ok(atualizada);
    }


    @GetMapping("/balanco/{idUsuario}")
    public ResponseEntity<BigDecimal> obterBalanco(@PathVariable UUID idUsuario) {
        BigDecimal balanco = transacaoService.calcularBalanco(idUsuario);
        return ResponseEntity.ok(balanco);
    }


    @GetMapping("/filtro")
    public ResponseEntity<List<TransacaoDTO>> filtrar(
            @RequestParam UUID idUsuario,
            @RequestParam int mes,
            @RequestParam int ano) {

        List<TransacaoDTO> filtrados = transacaoService.filtrarPorPeriodo(idUsuario, mes, ano);
        return ResponseEntity.ok(filtrados);
    }




}
