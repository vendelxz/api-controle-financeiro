package com.controlefinaneiro.api.transacao.service;


import com.controlefinaneiro.api.transacao.dtos.TransacaoDTO;
import com.controlefinaneiro.api.transacao.dtos.TransacaoResponse;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;


    @Autowired
    private AuthService authService;

    @Transactional
    public TransacaoResponse criar(TransacaoDTO transacaoDTO) {
        validarTransacaoCompleta(transacaoDTO);

        UUID idDoUsuario = pegarIDUsuario();

        Transacao transacaoASalvar = TransacaoMapper.toEntity(transacaoDTO, idDoUsuario);
        Transacao salva = transacaoRepository.save(transacaoASalvar);

        TransacaoResponse reposta = TransacaoMapper.toResponse(salva);

        return reposta;
    }

    @Transactional
    public TransacaoResponse atualizar(UUID id, TransacaoDTO transacaoDTO) {
        
        Transacao transacao = buscarEValidarDono(id);

        validarTransacaoCompleta(transacaoDTO);

        transacao.setValor(transacaoDTO.valor());
        transacao.setTipo(transacaoDTO.tipo());
        transacao.setCategoria(transacaoDTO.categoria());
        transacao.setDataTransacao(transacaoDTO.dataTransacao());
        transacao.setDescricao(transacaoDTO.descricao());
        transacao.setMetodoPagamento(transacaoDTO.metodoPagamento());

        return TransacaoMapper.toResponse(transacaoRepository.save(transacao));
    }

    @Transactional
    public void deletar(UUID id) {
        buscarEValidarDono(id);
        transacaoRepository.deleteById(id);
    }

    public List<TransacaoResponse> filtrarPorPeriodo( int mes, int ano){
        UUID idUsuario = pegarIDUsuario();

        validarMesEAno(mes, ano);
        validarUsuario(idUsuario);

        LocalDate inicio = LocalDate.of(ano,mes,1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        return transacaoRepository.findByIdUsuarioAndDataTransacaoBetween(idUsuario,inicio,fim)
                .stream().map(TransacaoMapper::toResponse).collect(Collectors.toList());
    }

    //Assim como nos outros métodos, todos são verificados internamente
    //O token funciona como a autenticação usuário
    //O sistema vai calcular o balanço de quem está logado de acordo com o token do login, logo não precisa passar ID para os métodos, beleza?
    public BigDecimal calcularBalanco(){
        UUID idUsuario = pegarIDUsuario();

        validarUsuario(idUsuario);

        BigDecimal receitas = transacaoRepository.somarValorPorTipo(idUsuario, TipoTransacao.RECEITA);
        BigDecimal despesas = transacaoRepository.somarValorPorTipo(idUsuario, TipoTransacao.DESPESA);

        receitas = (receitas != null ? receitas : BigDecimal.ZERO);
        despesas = (despesas != null ? despesas : BigDecimal.ZERO);

        return receitas.subtract(despesas);
    }

    //validações

    private void validarTransacaoCompleta(TransacaoDTO dto) {
        pegarIDUsuario();
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
    private Transacao buscarEValidarDono(UUID idTransacao){
        Transacao transacao = transacaoRepository.findById(idTransacao).orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        //Todos os métodos estou fazendo validação interna, isso é mais seguro
        //Passar o id no body do controller é arriscado, o token já tem todas as informações do usuário ;)
        UUID usuarioLogado = pegarIDUsuario();

        if(!transacao.getIdUsuario().equals(usuarioLogado)){
            throw new RuntimeException("Acesso negado: Esta transação pertence a outro usuário.");
        }

        return transacao;
    }

    private UUID pegarIDUsuario(){
        //Esse método que fiz é apenas auxiliar pra não ter que instanciar o AuthService toda hora
        //Centraliza tudo aqui e só devolve o que me importa pra verificar todas as transações (O id)
        Usuario usuario = authService.getUsuarioAutenticado();

        if(usuario == null){
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario.getId();
    }
}
