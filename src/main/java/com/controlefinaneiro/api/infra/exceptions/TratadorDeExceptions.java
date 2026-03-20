package com.controlefinaneiro.api.infra.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.el.MethodNotFoundException;

@RestControllerAdvice
public class TratadorDeExceptions {

    //Passar um json completinho nos campos que tem @Valid
    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
    }
}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        List<FieldError> erros = ex.getFieldErrors();
        
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarIllegal(IllegalArgumentException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> tratarRuntime(RuntimeException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", ex.getMessage());


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
      public ResponseEntity<Map<String, String>> tratarForbidden(AccessDeniedException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", "ACESSO NEGADO");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
      }

      @ExceptionHandler(MethodNotFoundException.class)
      public ResponseEntity<Map<String, String>> tratar404(MethodNotFoundException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
      }

}

