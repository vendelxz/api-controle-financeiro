package com.controlefinaneiro.api.usuario.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.controlefinaneiro.api.usuario.dto.UsuarioDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioResponseDTO;
import com.controlefinaneiro.api.usuario.models.Usuario;

@Component
public class UsuarioMapper {

    public static Usuario toModel(UsuarioDTO dto, String senhaHash){
        Usuario usuario = new Usuario(
            dto.nome(),
            dto.email(),
            senhaHash
        );
        return usuario;
    }

    public static UsuarioResponseDTO toResponse(Usuario usuario){
        UsuarioResponseDTO response = new UsuarioResponseDTO(
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getDataCricao()
        );
        return response;
    }

    public static List<UsuarioResponseDTO> toResponseList(List<Usuario> usuarios){
        if(usuarios.isEmpty()){
            return Collections.emptyList();
        }
        
       return usuarios.stream()
        .map(u -> toResponse(u))
        .toList();
    }
}
