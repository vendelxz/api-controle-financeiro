package com.controlefinaneiro.api.usuario.service;


import com.controlefinaneiro.api.infra.exceptions.TokenInvalidoException;
import com.controlefinaneiro.api.infra.notificacoes.eventos.RecuperarSenhaEvent;
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
            throw new IllegalArgumentException("Email em uso, tente outro por favor.");
        }

        String senhaHash = passwordEncoder.encode(dto.senha());

        Usuario usuarioAsalvar = UsuarioMapper.toModel(dto, senhaHash);
        Usuario usuarioSalvo = usuarioRepository.save(usuarioAsalvar);

        //Utilzação geral pra cadastrar um evento e disparar o e-mail
        publisher.publishEvent(new UsuarioCadastradoEvent(usuarioSalvo));

        return UsuarioMapper.toResponse(usuarioSalvo);

    }

    public String autenticar(LoginDTO login){
        Usuario usuario = usuarioRepository.findByEmail(login.email());
        if(usuario == null){
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        if(!passwordEncoder.matches(login.senha(), usuario.getSenha())){
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        String token = tokenService.gerarToken(usuario);
        return token;
    }

    public Usuario getUsuarioAutenticado(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("Usuário não autenticado");
        }

        return (Usuario) authentication.getPrincipal();
    }

    @Transactional
    public void solicitarRecuperacao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if(usuario == null){
            throw new RuntimeException("Usuário não encontrado");
        }

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

        publisher.publishEvent(new RecuperarSenhaEvent(usuario,valorToken));
    }

    @Transactional
    public void redefinirSenha(String valorToken, String novaSenha){
        Optional<TokenRecuperacao> recuperacao = tokenRepository.findByToken(valorToken);
        if(recuperacao == null){
            throw new TokenInvalidoException("Token inexistente");
        }
        if(!recuperacao.get().ehValido()){
            throw new TokenInvalidoException("Token expirado ou já utilizado");
        }

        //Marca o token como usado
        recuperacao.get().setUsado(true);
        tokenRepository.save(recuperacao.get());

        //Atualiza e salva
        Usuario usuario = recuperacao.get().getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

    }
}
