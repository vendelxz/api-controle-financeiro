package com.controlefinaneiro.api.infra.utils;

public class OriginValidator {

    //Vai receber o event.origem() que é uma string e validar para conseguir montar o link correto...
    public static String validadorDeOrigem(String origem){
        String urlReset;

        if(origem.contains("vercel.app")){
            urlReset = "/auth/redefinir-senha.html?token=";
            return urlReset;
        }
        if(origem.contains("github.io")){
            urlReset = "/finance.control/auth/redefinir-senha.html?token=";
            return urlReset;
         }

         //Se não for nenhum retornar nulo para tratar como exception...
         return null;

        }

    }


