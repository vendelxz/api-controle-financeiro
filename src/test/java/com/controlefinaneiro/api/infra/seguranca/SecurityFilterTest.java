package com.controlefinaneiro.api.infra.seguranca;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.controlefinaneiro.api.infra.seguranca.jwt.TokenService;
import com.controlefinaneiro.api.usuario.models.Usuario;
import com.controlefinaneiro.api.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;
    @Mock
    private UsuarioRepository repository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir o filterChain sem autenticar quando não há header Authorization")
    void deveSeguirFilterChainQuandoNaoHaHeaderAuthorization() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(tokenService, repository);
    }

    @Test
    @DisplayName("Deve seguir o filterChain sem autenticar quando o header não começa com Bearer")
    void deveSeguirFilterChainQuandoHeaderSemPrefixoBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic algumacoisa");

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(tokenService, repository);
    }

    @Test
    @DisplayName("Deve autenticar o usuário no SecurityContext quando o token for válido")
    void deveAutenticarUsuarioQuandoTokenValido() throws Exception {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "hash_senha");
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(tokenService.validarToken("token-valido")).thenReturn("teste@email.com");
        when(repository.findByEmail("teste@email.com")).thenReturn(usuario);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(usuario);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Deve retornar 401 e interromper a cadeia quando o token for inválido")
    void deveRetornar401QuandoTokenServiceLancaJWTVerificationException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(tokenService.validarToken("token-invalido"))
                .thenThrow(new JWTVerificationException("Token inválido para fins de teste"));

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Não deve autenticar, mas deve seguir o filterChain normalmente quando o email retornado for vazio")
    void deveSeguirFilterChainSemAutenticarQuandoEmailRetornadoEmBranco() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-estranho");
        when(tokenService.validarToken("token-estranho")).thenReturn("");

        securityFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(repository, never()).findByEmail(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }
}
