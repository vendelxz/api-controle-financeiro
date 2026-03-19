package com.controlefinaneiro.api.usuario.mapper;

import java.util.List;


import com.controlefinaneiro.api.usuario.dto.UsuarioDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioResponseDTO;
import com.controlefinaneiro.api.usuario.models.Usuario;

public class UsuarioMapper {

    

    public Usuario toModel(UsuarioDTO dto, String senhaHash){
        Usuario usuario = new Usuario(
            dto.nome(),
            dto.email(),
            senhaHash
        );
        return usuario;
    }

    public UsuarioResponseDTO toResponse(Usuario usuario){
        UsuarioResponseDTO response = new UsuarioResponseDTO(
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getDataCricao()
        );
        return response;
    }

    public List<UsuarioResponseDTO> toResponseList(List<Usuario> usuarios){
       return usuarios.stream()
        .map(u -> toResponse(u))
        .toList();
    }
}
