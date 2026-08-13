package com.controlefinaneiro.api.infra.exceptions;

public class UsuarioNaoAutenticadoException extends RuntimeException{
    public UsuarioNaoAutenticadoException(String mensagem){
        super(mensagem);
    }
}
