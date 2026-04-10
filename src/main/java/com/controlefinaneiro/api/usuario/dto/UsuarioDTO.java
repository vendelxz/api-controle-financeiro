package com.controlefinaneiro.api.usuario.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
     
     @NotBlank(message = "Nome é obrigatório.")
     String nome,

    @Email(message = "Formato de e-mail inválido.")
    @NotBlank
     String email,

    @Size(min = 8)
    @NotBlank(message = "Senha é obrigatória.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
             message = "A senha deve conter ao menos uma letra maiúscula, um número e um caractere especial.")
    String senha,

    @Size(min = 8)
    @NotBlank(message = "Campo de confirmação obrigatório.")
     @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
             message = "A senha deve conter ao menos uma letra maiúscula, um número e um caractere especial.")
    String confirmarSenha

) {

}
