package com.controlefinaneiro.api.infra.exceptions;

public class AcessoNegadoNegocioException extends RuntimeException{
    public AcessoNegadoNegocioException(String mensagem){
        super(mensagem);
    }
}
