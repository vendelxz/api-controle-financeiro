package com.controlefinaneiro.api.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetSenhaRequest(
        @NotBlank String token,

        @Size(min = 8)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "A senha deve conter ao menos uma letra maiúscula, um número e um caractere especial.")
        @NotBlank String novaSenha
) {
}
