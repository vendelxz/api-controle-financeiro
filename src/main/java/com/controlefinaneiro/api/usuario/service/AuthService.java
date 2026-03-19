package com.controlefinaneiro.api.usuario.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.controlefinaneiro.api.infra.seguranca.TokenService;
import com.controlefinaneiro.api.usuario.dto.LoginDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioResponseDTO;
import com.controlefinaneiro.api.usuario.mapper.UsuarioMapper;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO registrar(UsuarioDTO dto){
        if(usuarioRepository.existsByEmail(dto.email())){
            throw new RuntimeException("Email em uso, tente outro por favor.");
        }

        String senhaHash = passwordEncoder.encode(dto.senha());

        Usuario usuarioAsalvar = UsuarioMapper.toModel(dto, senhaHash);
        Usuario usuarioSalvo = usuarioRepository.save(usuarioAsalvar);

        return UsuarioMapper.toResponse(usuarioSalvo);

    }

    public String autenticar(LoginDTO login){
        Usuario usuario = usuarioRepository.findByEmail(login.email());
        if(usuario == null){
            throw new RuntimeException("Erro de autenticação.");
        }

        if(!passwordEncoder.matches(login.senha(), usuario.getSenha())){
            throw new RuntimeException("E-mail ou senha incorretos.");
        }

        String token = tokenService.gerarToken(usuario);
        return token;
    }

    public Usuario getUsuarioAutenticado(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("Usuário não autenticado");
        }

        return (Usuario) authentication.getPrincipal();
    }


}
