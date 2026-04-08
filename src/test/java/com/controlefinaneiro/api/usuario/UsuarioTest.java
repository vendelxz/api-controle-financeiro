package com.controlefinaneiro.api.usuario;

import com.controlefinaneiro.api.infra.notificacoes.eventos.UsuarioCadastradoEvent;
import com.controlefinaneiro.api.infra.seguranca.jwt.TokenService;
import com.controlefinaneiro.api.usuario.dto.LoginDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioDTO;
import com.controlefinaneiro.api.usuario.dto.UsuarioResponseDTO;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.repository.UsuarioRepository;
import com.controlefinaneiro.api.usuario.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UsuarioTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AuthServiceTest {

        @Mock
        private UsuarioRepository usuarioRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private TokenService tokenService;

        @Mock
        private ApplicationEventPublisher publisher;

        @InjectMocks
        private AuthService authService;

        private Usuario usuarioExemplo;
        private UsuarioDTO registroDTO;

        @BeforeEach
        void setup() {
            usuarioExemplo = new Usuario("Teste", "teste@email.com", "Senha@123");
            usuarioExemplo.setId(UUID.randomUUID());

            registroDTO = new UsuarioDTO("Teste", "teste@email.com", "Senha@123", "Senha@123");
        }

        @Test
        @DisplayName("Deve registrar um usuário com sucesso")
        void registrarSucesso() {

            when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hash_senha");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExemplo);

            // Act
            UsuarioResponseDTO resultado = authService.registrar(registroDTO);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.email()).isEqualTo(registroDTO.email());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
            verify(publisher, times(1)).publishEvent(any(UsuarioCadastradoEvent.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando o e-mail já estiver em uso")
        void registrarErroEmailEmUso() {
            // Arrange
            when(usuarioRepository.existsByEmail(registroDTO.email())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> authService.registrar(registroDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email em uso, tente outro por favor.");

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando as senhas forem diferentes")
        void registrarErroSenhasDiferentes() {
            // Arrange
            UsuarioDTO dtoSenhaIncorreta = new UsuarioDTO("Teste", "teste@email.com", "Senha123", "OutraSenha");

            // Act & Assert
            assertThatThrownBy(() -> authService.registrar(dtoSenhaIncorreta))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("As senhas digitadas são diferentes.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando a senha de login for diferente da cadastrada")
        void autenticarSenhaIncorreta() {

            Usuario usuarioCadastrado = new Usuario("Teste", "teste@gmail.com", "hash_no_banco");

            LoginDTO loginComSenhaErrada = new LoginDTO("teste@gmail.com", "SenhaErrada123");

            when(usuarioRepository.findByEmail(loginComSenhaErrada.email())).thenReturn(usuarioCadastrado);

            when(passwordEncoder.matches(eq(loginComSenhaErrada.senha()), eq(usuarioCadastrado.getSenha())))
                    .thenReturn(false);

            assertThatThrownBy(() -> authService.autenticar(loginComSenhaErrada))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("E-mail ou senha incorretos.");


            verify(tokenService, never()).gerarToken(any());
    }


}}
