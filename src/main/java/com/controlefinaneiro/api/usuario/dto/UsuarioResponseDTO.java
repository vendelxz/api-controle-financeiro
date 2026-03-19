package com.controlefinaneiro.api.usuario.dto;

import java.time.LocalDateTime;


public record UsuarioResponseDTO( String nome, String email, LocalDateTime dataCricao) {}
