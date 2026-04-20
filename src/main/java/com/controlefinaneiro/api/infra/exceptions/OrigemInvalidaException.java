package com.controlefinaneiro.api.infra.exceptions;

public class OrigemInvalidaException extends RuntimeException{
    public OrigemInvalidaException(String mensagem){
        super(mensagem);
    }

}
