package com.controlefinaneiro.api.transacao.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.controlefinaneiro.api.transacao.enums.Categoria;
import com.controlefinaneiro.api.transacao.enums.MetodoPagamento;
import com.controlefinaneiro.api.transacao.enums.TipoTransacao;

public record TransacaoResponse(
        BigDecimal valor,
        TipoTransacao tipo,
        Categoria categoria,
        MetodoPagamento metodoPagamento,
        String descricao,
        LocalDate dataTransacao
) {

}
