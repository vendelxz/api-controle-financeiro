package com.controlefinaneiro.api.infra.notificacoes.eventos;

import com.controlefinaneiro.api.usuario.models.Usuario;

public record UsuarioCadastradoEvent(Usuario usuario) {

}
