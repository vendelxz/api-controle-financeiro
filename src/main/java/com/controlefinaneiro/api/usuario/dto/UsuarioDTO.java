package com.controlefinaneiro.api.usuario.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
     
     @NotBlank(message = "Nome é obrigatório.")
     String nome,

    @Email(message = "Formato de e-mail inválido.")
    @NotBlank
     String email,

    @Size(min = 8)
    @NotBlank(message = "Senha é obrigatória.")
    String senha

) {

}
