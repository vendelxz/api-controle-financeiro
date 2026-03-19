package com.controlefinaneiro.api.usuario.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório.")
    private String nome;

    @Email(message = "Formato de e-mail inválido.")
    @NotBlank
    @Column(nullable = false, unique =  true)
    private String email;

    @Size(min = 8)
    @NotBlank(message = "Senha é obrigatória.")
    private String senha;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCricao ;

    //Construtor padrão completo. (UsuarioMapper)
    public Usuario( UUID id, @NotNull @NotBlank String nome, @Email @NotBlank @NotBlank String email,
            @Size(min = 8) @NotBlank @NotNull String senha, LocalDateTime dataCricao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCricao = dataCricao;
    }

    //Construtor vazio para o banco também
    public Usuario(){}

    public Usuario(@NotBlank(message = "Nome é obrigatório.") String nome,
            @Email(message = "Formato de e-mail inválido.") @NotBlank String email,
            @Size(min = 8) @NotBlank(message = "Senha é obrigatória.") String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCricao = LocalDateTime.now();
    }  

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCricao() {
        return dataCricao;
    }

    public void setDataCricao(LocalDateTime dataCricao) {
        this.dataCricao = dataCricao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((senha == null) ? 0 : senha.hashCode());
        result = prime * result + ((dataCricao == null) ? 0 : dataCricao.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Usuario other = (Usuario) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (senha == null) {
            if (other.senha != null)
                return false;
        } else if (!senha.equals(other.senha))
            return false;
        if (dataCricao == null) {
            if (other.dataCricao != null)
                return false;
        } else if (!dataCricao.equals(other.dataCricao))
            return false;
        return true;
    }

}
