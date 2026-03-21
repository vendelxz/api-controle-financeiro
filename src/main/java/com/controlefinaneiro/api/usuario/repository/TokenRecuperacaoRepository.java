package com.controlefinaneiro.api.usuario.repository;

import com.controlefinaneiro.api.usuario.models.TokenRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, UUID> {
    Optional<TokenRecuperacao> findByToken(String token);


    @Modifying
    @Query("UPDATE TokenRecuperacao t SET t.usado = true WHERE t.usuario.id = :usuarioId AND t.usado = false")
    void invalidarTokensAntigos(UUID usuarioId);

    void deleteByDataExpiracaoBefore(LocalDateTime now);
}
