package com.controlefinaneiro.api.infra.notificacoes.eventos;

import com.controlefinaneiro.api.usuario.models.Usuario;

public record RelatorioSolicitadoEvent(
        Usuario usuario,
        int mes,
        int ano) {
}
