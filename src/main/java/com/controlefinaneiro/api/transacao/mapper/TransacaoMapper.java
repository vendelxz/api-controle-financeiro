package com.controlefinaneiro.api.transacao.mapper;


import com.controlefinaneiro.api.transacao.dtos.TransacaoDTO;
import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
import com.controlefinaneiro.api.transacao.models.Transacao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {

    public static Transacao toEntity(TransacaoDTO dto, UUID idDoUsuario) {
        if(dto == null) return null;

        //Simplifiquei os mappers pra poder passar o ID sem ele transitar dentro do DTO.
        Transacao transacao = new Transacao(
            idDoUsuario,
            dto.valor(),
            dto.tipo(),
            dto.categoria(),
            dto.metodoPagamento(),
            dto.descricao(),
            dto.dataTransacao());

            return transacao;
    }

    //Todas as transações sempre vão retornar essa mapeação aqui: limpa e segura, sem nenhum ID de usuário.

    public static TransacaoResponse toResponse(Transacao transacao) {
        if(transacao == null) return null;

        TransacaoResponse transacaoResponse = new TransacaoResponse(
            transacao.getId(), //Precisei adicionar para poder deletar no front, mas os dados não passam em tabelas. É apenas para exclusão...
            transacao.getValor(),
            transacao.getTipo(),
            transacao.getCategoria(),
            transacao.getMetodoPagamento(),
            transacao.getDescricao(),
            transacao.getDataTransacao()
        );
        return transacaoResponse;
    }

    //Para métodos GET que for implementar, devolve a stream completa com uma pequena verificação antes
    //Possui uma em Usuario também...
    public static List<TransacaoResponse> listaDeTransacoes(List<Transacao> transacoes){
        if(transacoes.isEmpty()){
            return Collections.emptyList();
        }

        return transacoes.stream()
        .map(u -> toResponse(u))
        .toList();
    }
}
