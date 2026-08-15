package com.controlefinaneiro.api.usuario.service;


import com.controlefinaneiro.api.infra.exceptions.OrigemInvalidaException;
import com.controlefinaneiro.api.infra.exceptions.TokenInvalidoException;
import com.controlefinaneiro.api.infra.exceptions.UsuarioNaoAutenticadoException;
import com.controlefinaneiro.api.infra.notificacoes.eventos.RecuperarSenhaEvent;
import com.controlefinaneiro.api.infra.utils.OriginValidator;
import com.controlefinaneiro.api.usuario.models.TokenRecuperacao;
import com.controlefinaneiro.api.usuario.repository.TokenRecuperacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.controlefinaneiro.api.infra.notificacoes.eventos.UsuarioCadastradoEvent;
import com.controlefinaneiro.api.infra.seguranca.jwt.TokenService;
import com.controlefinaneiro.api.usuario.dto.LoginDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioResponseDTO;
import com.controlefinaneiro.api.usuario.mapper.UsuarioMapper;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRecuperacaoRepository tokenRepository;

    //Classe que cuida dos eventos
    @Autowired
    private ApplicationEventPublisher publisher;

    public UsuarioResponseDTO registrar(UsuarioDTO dto){
        if(usuarioRepository.existsByEmail(dto.email())){
            log.warn("Tentativa de cadastro com e-mail já em uso: {}", dto.email());
            throw new IllegalArgumentException("Email em uso, tente outro por favor.");
        }

        if(!dto.senha().equals(dto.confirmarSenha())){
            throw new IllegalArgumentException("As senhas digitadas são diferentes.");
        }

        String senhaHash = passwordEncoder.encode(dto.senha());

        Usuario usuarioAsalvar = UsuarioMapper.toModel(dto, senhaHash);
        Usuario usuarioSalvo = usuarioRepository.save(usuarioAsalvar);

        log.info("Cadastro realizado com sucesso para {}", usuarioSalvo.getEmail());

        //Utilzação geral pra cadastrar um evento e disparar o e-mail
        publisher.publishEvent(new UsuarioCadastradoEvent(usuarioSalvo));

        return UsuarioMapper.toResponse(usuarioSalvo);

    }

    public String autenticar(LoginDTO login){
        Usuario usuario = usuarioRepository.findByEmail(login.email());
        if(usuario == null){
            log.warn("Tentativa de login com e-mail não cadastrado: {}", login.email());
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        if(!passwordEncoder.matches(login.senha(), usuario.getSenha())){
            log.warn("Tentativa de login com senha incorreta para {}", login.email());
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        String token = tokenService.gerarToken(usuario);
        log.info("Login bem-sucedido para {}", usuario.getEmail());
        return token;
    }

    public Usuario getUsuarioAutenticado(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new UsuarioNaoAutenticadoException("Usuário não autenticado");
        }

        return (Usuario) authentication.getPrincipal();
    }

    @Transactional
    public void solicitarRecuperacao(String email, String origem){
        //Valida a origem de forma síncrona (fail-fast), antes de publicar o evento assíncrono:
        //se disparasse só dentro do listener @Async, a exceção nunca chegaria ao cliente,
        //que já teria recebido 200 (ver RecuperaSenhaListener).
        String urlReset = OriginValidator.validadorDeOrigem(origem);
        if(urlReset == null){
            throw new OrigemInvalidaException("Origem não identificada para montar o link.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email);
         if(usuario == null){ return ;} //Para garantir o 200 no build();

        //Invalia qualquer token que nao foi usado antes
        tokenRepository.deletarPorUsuarioId(usuario.getId());

        //Vou dar um flush pra obrigar ele a rodar e conseguir apagar o token
        tokenRepository.flush();
        //Gera token aleatorio
        String valorToken = UUID.randomUUID().toString();

        TokenRecuperacao novoToken = new TokenRecuperacao();
        novoToken.setToken(valorToken);
        novoToken.setUsuario(usuario);
        novoToken.setDataExpiracao(LocalDateTime.now().plusMinutes(10));

        tokenRepository.save(novoToken);

        String urlCompleta = origem + urlReset + valorToken;

        publisher.publishEvent(new RecuperarSenhaEvent(usuario, urlCompleta));

        log.info("Solicitação de recuperação de senha processada para {}", email);
    }

    @Transactional
    public void redefinirSenha(String valorToken, String novaSenha){
        Optional<TokenRecuperacao> recuperacao = tokenRepository.findByToken(valorToken);
        if(recuperacao == null){
            throw new TokenInvalidoException("Token inexistente");
        }
        if(!recuperacao.get().ehValido()){
            log.warn("Tentativa de redefinição de senha com token inválido ou expirado para o usuário {}",
                    recuperacao.get().getUsuario().getEmail());
            throw new TokenInvalidoException("Token expirado ou já utilizado");
        }

        //Marca o token como usado
        recuperacao.get().setUsado(true);
        tokenRepository.save(recuperacao.get());

        //Atualiza e salva
        Usuario usuario = recuperacao.get().getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        log.info("Senha redefinida com sucesso para {}", usuario.getEmail());
    }
}