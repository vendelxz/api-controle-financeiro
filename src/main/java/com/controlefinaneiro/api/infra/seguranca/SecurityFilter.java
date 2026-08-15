package com.controlefinaneiro.api.infra.seguranca;

import java.io.IOException;
import java.util.Collections;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.controlefinaneiro.api.infra.seguranca.jwt.TokenService;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

 private final TokenService tokenService;
    private final UsuarioRepository repository;


    public SecurityFilter(TokenService tokenService, UsuarioRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    //Em alguns cenários tenta captura o token mesmo em endpoints públicos...
    //Refatorar para pegar apenas se o Bearer estiver presente no cabeçalho...
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //String token = this.recuperarToken(request);
        //TokenHeader para ver o Authorization correto
        var tokenHeader = request.getHeader("Authorization");
        boolean tokenPresente = tokenHeader != null && tokenHeader.startsWith("Bearer ");

        if (!tokenPresente) {
            filterChain.doFilter(request, response);
            logarRequisicao(request, tokenPresente);
            return;
        }

        try{
            var token = tokenHeader.replace("Bearer ", "");
            var email = tokenService.validarToken(token);//O subject que retorna é o email do usuário...

            if(email != null && !email.isBlank()){
                Usuario usuario = repository.findByEmail(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
            logarRequisicao(request, tokenPresente);

        }catch(JWTVerificationException e){
            log.warn("Token JWT rejeitado para path {}: {}", request.getRequestURI(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// 401 para dizer que é proibido;
            return;

        }

    }

    //Apenas de uso em casos necessários..
    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

    private void logarRequisicao(HttpServletRequest request, boolean tokenPresente) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        String classeExecutada = (handler instanceof HandlerMethod hm)
                ? hm.getBeanType().getSimpleName()
                : "Desconhecida";

        log.info("[SecurityFilter] path: {}, token: {}, class: {}",
                request.getRequestURI(), tokenPresente, classeExecutada);
    }

}

 
                