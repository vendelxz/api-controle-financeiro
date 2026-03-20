package com.controlefinaneiro.api.transacao.dtos;

import com.controlefinaneiro.api.transacao.enums.Categoria;
import com.controlefinaneiro.api.transacao.enums.MetodoPagamento;
import com.controlefinaneiro.api.transacao.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoDTO(
        UUID idUsuario,
        BigDecimal valor,
        TipoTransacao tipo,
        Categoria categoria,
        MetodoPagamento metodoPagamento,
        String descricao,
        LocalDate dataTransacao
) {}
