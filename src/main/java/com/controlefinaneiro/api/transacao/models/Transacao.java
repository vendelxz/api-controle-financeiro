package com.controlefinaneiro.api.transacao.models;

import com.controlefinaneiro.api.transacao.enums.Categoria;
import com.controlefinaneiro.api.transacao.enums.MetodoPagamento;
import com.controlefinaneiro.api.transacao.enums.TipoTransacao;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "transacoes")
public class Transacao {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID idUsuario;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private LocalDate dataTransacao;


    //Um construtor já é suficiente, achei que precisaria de dois mas esse faz todo o serviço sem precisar de outro pra acumular lógica desnecessária.
    public Transacao(@Nullable UUID idUsuario, @Nullable BigDecimal valor, @Nullable TipoTransacao tipo, @Nullable Categoria categoria, @Nullable MetodoPagamento metodoPagamento, String descricao, @Nullable LocalDate dataTransacao) {
        this.categoria = categoria;
        this.dataTransacao = dataTransacao;
        this.descricao = descricao;
        this.idUsuario = idUsuario;
        this.metodoPagamento = metodoPagamento;
        this.tipo = tipo;
        this.valor = valor;
    }

    public Transacao() {
    }

   
    public Categoria getCategoria() {return categoria;}
    public void setCategoria(Categoria categoria) {this.categoria = categoria;}

    public LocalDate getDataTransacao() {return dataTransacao;}
    public void setDataTransacao(LocalDate dataTransacao) {this.dataTransacao = dataTransacao;}

    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public UUID getIdUsuario() {return idUsuario;}
    public void setIdUsuario(UUID idUsuario) {this.idUsuario = idUsuario;}

    public MetodoPagamento getMetodoPagamento() {return metodoPagamento;}
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {this.metodoPagamento = metodoPagamento;}

    public TipoTransacao getTipo() {return tipo;}
    public void setTipo(TipoTransacao tipo) {this.tipo = tipo;}

    public BigDecimal getValor() {return valor;}
    public void setValor(BigDecimal valor) {this.valor = valor;}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(id, transacao.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }

}
