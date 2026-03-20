package com.controlefinaneiro.api.infra.seguranca;

import java.io.IOException;
import java.util.Collections;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

 private final TokenService tokenService;
    private final UsuarioRepository repository;


    public SecurityFilter(TokenService tokenService, UsuarioRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = this.recuperarToken(request);

        if (token != null) {
            var email = tokenService.validarToken(token); 
            
            if (!email.isEmpty()) {
                Usuario usuario = repository.findByEmail(email);
                if(usuario.getEmail().isBlank()){
                    throw new IllegalArgumentException();
                } 
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

}
