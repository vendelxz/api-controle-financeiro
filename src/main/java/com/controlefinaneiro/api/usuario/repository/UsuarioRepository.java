package com.controlefinaneiro.api.usuario.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.controlefinaneiro.api.usuario.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    boolean existsByEmail(String email);

    Usuario findByEmail(String email);
}
