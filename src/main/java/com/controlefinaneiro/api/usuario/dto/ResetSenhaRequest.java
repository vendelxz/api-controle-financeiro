package com.controlefinaneiro.api.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetSenhaRequest(
        @NotBlank String token,
        @NotBlank String novaSenha
) {
}
