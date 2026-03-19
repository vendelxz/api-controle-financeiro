package com.controlefinaneiro.api.infra.seguranca;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.controlefinaneiro.api.usuario.models.Usuario;

@Service
public class TokenService {

    @Value("$token.secret")
    private String secret;

    public String gerarToken(Usuario usuario){
        try{
            Algorithm algoritmo = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("API Controle Financeiro") // Identifica quem emitiu o token
                    .withSubject(usuario.getEmail()) // Identifica o usuário logado pelo e-mail
                    .withExpiresAt(gerarDataExpiracao()) // Define a validade do token
                    .sign(algoritmo); // Assina e finaliza a criação

        }catch(JWTCreationException e){
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar o token JWT: "+ e.getMessage());
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String validarToken(String token){
        try{
            Algorithm algoritmo = Algorithm.HMAC256(secret);

            return JWT.require(algoritmo)
                    .withIssuer("API Controle Financeiro") // Verifica se fomos nós que emitimos
                    .build()
                    .verify(token) // Valida a assinatura e a expiração
                    .getSubject(); // Retorna o e-mail do usuário (o Subject)

        }catch(JWTCreationException e){
            return "";
        }
    }

}
