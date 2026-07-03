package com.controlefinaneiro.api.usuario;


import com.controlefinaneiro.api.infra.utils.OriginValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

    class OriginValidatorTest {

        @Test
        void deveRetornarUrlSemHtmlParaVercel() {
            String resultado = OriginValidator.validadorDeOrigem("https://financecontrol-react.vercel.app");
            assertEquals("/auth/redefinir-senha?token=", resultado);
        }

        @Test
        void deveRetornarUrlComHtmlParaGithubIo() {
            String resultado = OriginValidator.validadorDeOrigem("https://vendelxz.github.io");
            assertEquals("/finance.control/auth/redefinir-senha.html?token=", resultado);
        }

        @Test
        void deveRetornarUrlSemHtmlParaLocalhostReact() {
            String resultado = OriginValidator.validadorDeOrigem("http://localhost:5173");
            assertEquals("/auth/redefinir-senha?token=", resultado);
        }

        @Test
        void deveRetornarUrlComHtmlParaLocalhostVanilla() {
            String resultado = OriginValidator.validadorDeOrigem("http://localhost:5500");
            assertEquals("/auth/redefinir-senha.html?token=", resultado);
        }

        @Test
        void deveRetornarNuloParaOrigemDesconhecida() {
            String resultado = OriginValidator.validadorDeOrigem("https://origem-estranha.com");
            assertNull(resultado);
        }

        @Test
        void deveRetornarNuloParaOrigemVazia() {
            String resultado = OriginValidator.validadorDeOrigem("");
            assertNull(resultado);
        }
    }

