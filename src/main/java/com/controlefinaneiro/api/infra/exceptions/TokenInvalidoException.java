package com.controlefinaneiro.api.infra.exceptions;

public class TokenInvalidoException extends RuntimeException{
    public TokenInvalidoException(String mensagem){
        super(mensagem);
    }
}
