package com.controlefinaneiro.api.infra.seguranca.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.controlefinaneiro.api.usuario.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private static final String SECRET = "chave-secreta-de-teste-nao-usar-em-producao";
    private static final String ISSUER = "API Controle Financeiro";

    private TokenService tokenService;
    private Usuario usuarioExemplo;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        usuarioExemplo = new Usuario("Teste", "teste@email.com", "Senha@123");
    }

    private String criarTokenComClaims(String secret, String issuer, String subject, Instant expiresAt) {
        Algorithm algoritmo = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .sign(algoritmo);
    }

    @Test
    @DisplayName("Deve validar um token válido e retornar o e-mail do usuário")
    void deveValidarTokenValidoERetornarEmail() {
        String token = tokenService.gerarToken(usuarioExemplo);

        String email = tokenService.validarToken(token);

        assertThat(email).isEqualTo("teste@email.com");
    }

    @Test
    @DisplayName("Deve lançar TokenExpiredException quando o token estiver expirado")
    void deveLancarTokenExpiredExceptionQuandoTokenExpirado() {
        String tokenExpirado = criarTokenComClaims(SECRET, ISSUER, usuarioExemplo.getEmail(),
                Instant.now().minusSeconds(3600));

        assertThatThrownBy(() -> tokenService.validarToken(tokenExpirado))
                .isInstanceOf(TokenExpiredException.class)
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Deve lançar SignatureVerificationException quando a assinatura for inválida")
    void deveLancarSignatureVerificationExceptionQuandoAssinaturaInvalida() {
        String tokenAssinaturaInvalida = criarTokenComClaims("outra-chave-completamente-diferente", ISSUER,
                usuarioExemplo.getEmail(), Instant.now().plusSeconds(3600));

        assertThatThrownBy(() -> tokenService.validarToken(tokenAssinaturaInvalida))
                .isInstanceOf(SignatureVerificationException.class)
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Deve lançar JWTDecodeException quando o token estiver malformado")
    void deveLancarJWTDecodeExceptionQuandoTokenMalformado() {
        String tokenMalformado = "isso-nao-e-um-jwt-valido";

        assertThatThrownBy(() -> tokenService.validarToken(tokenMalformado))
                .isInstanceOf(JWTDecodeException.class)
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Deve lançar InvalidClaimException quando o issuer for diferente do esperado")
    void deveLancarInvalidClaimExceptionQuandoIssuerIncorreto() {
        String tokenIssuerErrado = criarTokenComClaims(SECRET, "Outro Emissor Qualquer",
                usuarioExemplo.getEmail(), Instant.now().plusSeconds(3600));

        assertThatThrownBy(() -> tokenService.validarToken(tokenIssuerErrado))
                .isInstanceOf(InvalidClaimException.class)
                .isInstanceOf(JWTVerificationException.class);
    }
}
