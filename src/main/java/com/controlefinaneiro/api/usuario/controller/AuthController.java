package com.controlefinaneiro.api.usuario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.controlefinaneiro.api.usuario.dto.LoginDTO;
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

    @PostMapping("/login")
    public ResponseEntity <String> login(@Valid @RequestBody LoginDTO dto){
        String token = authService.autenticar(dto);
        return ResponseEntity.status(HttpStatus.OK).body("token: "+ token);
    }
}
