package com.controlefinaneiro.api.usuario.controller;

import com.controlefinaneiro.api.infra.seguranca.jwt.TokenResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.controlefinaneiro.api.usuario.dto.EmailRequest;
import com.controlefinaneiro.api.usuario.dto.LoginDTO;
import com.controlefinaneiro.api.usuario.dto.ResetSenhaRequest;
import com.controlefinaneiro.api.usuario.dto.UsuarioDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioResponseDTO;
import com.controlefinaneiro.api.usuario.service.AuthService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    
    @PostMapping("/registrar")
    public ResponseEntity <UsuarioResponseDTO > registrar(@Valid @RequestBody UsuarioDTO dto){

        UsuarioResponseDTO usuarioCriado = authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }
    //Precisei fazer um TokenResponse para ser retornado como JSON
    //Pois estava chegando como PlainText no Javascript, e impedindo a extração do mesmo
    @PostMapping("/login")
    public ResponseEntity <TokenResponse> login(@Valid @RequestBody LoginDTO dto){
        String token = authService.autenticar(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> esqueciSenha(@Valid @RequestBody EmailRequest request){
        authService.solicitarRecuperacao(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid ResetSenhaRequest request){
        authService.redefinirSenha(request.token(), request.novaSenha());
        return ResponseEntity.ok().build();
    }
}
