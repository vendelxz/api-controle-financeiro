package com.controlefinaneiro.api.transacao.service;


import com.controlefinaneiro.api.transacao.dtos.TransacaoDTO;
import com.controlefinaneiro.api.transacao.enums.TipoTransacao;
import com.controlefinaneiro.api.transacao.mapper.TransacaoMapper;
import com.controlefinaneiro.api.transacao.models.Transacao;
import com.controlefinaneiro.api.transacao.repository.TransacaoRepository;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private TransacaoMapper transacaoMapper;


    @Transactional
    public TransacaoDTO criar(TransacaoDTO transacaoDTO) {
        Usuario usuarioLogado = authService.getUsuarioAutenticado();
        validarTransacaoCompleta(transacaoDTO);

        Transacao transacao = transacaoMapper.toEntity(transacaoDTO);
        transacao.setIdUsuario(usuarioLogado.getId());
        Transacao salva = transacaoRepository.save(transacao);

        return transacaoMapper.toDTO(salva);
    }

    @Transactional
    public TransacaoDTO atualizar(UUID id, TransacaoDTO transacaoDTO, UUID idUsuarioLogado) {
        Transacao transacao = buscarEValidarDono(id, idUsuarioLogado);

        validarTransacaoCompleta(transacaoDTO);

        transacao.setValor(transacaoDTO.valor());
        transacao.setTipo(transacaoDTO.tipo());
        transacao.setCategoria(transacaoDTO.categoria());
        transacao.setDataTransacao(transacaoDTO.dataTransacao());
        transacao.setDescricao(transacaoDTO.descricao());
        transacao.setMetodoPagamento(transacaoDTO.metodoPagamento());

        return transacaoMapper.toDTO(transacaoRepository.save(transacao));
    }

    @Transactional
    public void deletar(UUID id, UUID idUsuarioLogado) {
        buscarEValidarDono(id, idUsuarioLogado);
        transacaoRepository.deleteById(id);
    }

    public List<TransacaoDTO> filtrarPorPeriodo(UUID idUsuario, int mes, int ano){
        validarMesEAno(mes, ano);
        validarUsuario(idUsuario);

        LocalDate inicio = LocalDate.of(ano,mes,1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        return transacaoRepository.findByIdUsuarioAndDataTransacaoBetween(idUsuario,inicio,fim)
                .stream().map(transacaoMapper::toDTO).collect(Collectors.toList());
    }


    public BigDecimal calcularBalanco(UUID idUsuario){
        validarUsuario(idUsuario);

        BigDecimal receitas = transacaoRepository.somarValorPorTipo(idUsuario, TipoTransacao.RECEITA);
        BigDecimal despesas = transacaoRepository.somarValorPorTipo(idUsuario, TipoTransacao.DESPESA);

        receitas = (receitas != null ? receitas : BigDecimal.ZERO);
        despesas = (despesas != null ? despesas : BigDecimal.ZERO);

        return receitas.subtract(despesas);
    }

    //validações

    private void validarTransacaoCompleta(TransacaoDTO dto) {
        validarUsuario(dto.idUsuario());
        validarValor(dto.valor());
        validarData(dto.dataTransacao());

        if (dto.tipo() == null) throw new IllegalArgumentException("O tipo da transação é obrigatório.");
        if (dto.categoria() == null) throw new IllegalArgumentException("A categoria é obrigatória.");
        if (dto.metodoPagamento() == null) throw new IllegalArgumentException("O método de pagamento é obrigatório.");

        if (dto.descricao() != null && dto.descricao().length() > 255) {
            throw new IllegalArgumentException("A descrição não pode exceder 255 caracteres.");
        }
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser positivo e maior que zero.");
        }
    }

    private void validarData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data da transação é obrigatória.");
        }
        if (data.isAfter(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("Não é permitido agendar transações com mais de um ano de antecedência.");
        }
    }

    private void validarUsuario(UUID idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo.");
        }
    }

    private void validarMesEAno(int mes, int ano) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês inválido. Deve estar entre 1 e 12.");
        }
        if (ano < 2000 || ano > 2100) {
            throw new IllegalArgumentException("Ano fora do intervalo permitido.");
        }
    }
    private Transacao buscarEValidarDono(UUID idTransacao, UUID idUsuaioLogado){
        Transacao transacao = transacaoRepository.findById(idTransacao).orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        if(!transacao.getIdUsuario().equals(idUsuaioLogado)){
            throw new RuntimeException("Acesso negado: Esta transação pertence a outro usuário.");
        }

        return transacao;
    }
}
