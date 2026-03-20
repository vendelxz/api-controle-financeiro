package com.controlefinaneiro.api.transacao.repository;

import com.controlefinaneiro.api.transacao.enums.Categoria;
import com.controlefinaneiro.api.transacao.enums.TipoTransacao;
import com.controlefinaneiro.api.transacao.models.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    List<Transacao> findByIdUsuario(UUID idUsuario);

    List<Transacao> findByIdUsuarioAndDataTransacaoBetween (UUID idUsuario, LocalDate dataInicio, LocalDate dataFim);

    List<Transacao> findByIdUsuarioAndTipo(UUID idUsuario, TipoTransacao tipo);

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.idUsuario = :idUsuario AND t.tipo = :tipo")
    BigDecimal somarValorPorTipo(@Param("idUsuario") UUID idUsuario, @Param("tipo") TipoTransacao tipo);

    @Query("SELECT t FROM Transacao t WHERE t.idUsuario = :idUsuario AND t.categoria = :categoria")
    List<Transacao> buscarPorCategoria(@Param("idUsuario") UUID idUsuario, @Param("categoria")Categoria categoria);
}
