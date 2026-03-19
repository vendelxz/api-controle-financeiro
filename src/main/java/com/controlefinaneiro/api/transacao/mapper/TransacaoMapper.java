package com.controlefinaneiro.api.transacao.mapper;


import com.controlefinaneiro.api.transacao.dtos.TransacaoDTO;
import com.controlefinaneiro.api.transacao.models.Transacao;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {

    public Transacao toEntity(TransacaoDTO dto) {
        if(dto == null) return null;

        Transacao transacaoEntity = new Transacao();

        transacaoEntity.setIdUsuario(dto.idUsuario());
        transacaoEntity.setValor(dto.valor());
        transacaoEntity.setDataTransacao(dto.dataTransacao());
        transacaoEntity.setTipo(dto.tipo());
        transacaoEntity.setDescricao(dto.descricao());
        transacaoEntity.setCategoria(dto.categoria());
        transacaoEntity.setMetodoPagamento(dto.metodoPagamento());

        return transacaoEntity;
    }

    public TransacaoDTO toDTO(Transacao entity) {
        if(entity == null) return null;

        return new TransacaoDTO(
                entity.getIdUsuario(),
                entity.getValor(),
                entity.getTipo(),
                entity.getCategoria(),
                entity.getMetodoPagamento(),
                entity.getDescricao(),
                entity.getDataTransacao()
        );
    }
}
