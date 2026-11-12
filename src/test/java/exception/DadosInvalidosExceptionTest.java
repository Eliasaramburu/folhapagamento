package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DadosInvalidosExceptionTest {

    @Test
    @DisplayName("Guarda a mensagem informada")
    void guardaMensagem() {
        DadosInvalidosException e = new DadosInvalidosException("CPF inválido");
        assertEquals("CPF inválido", e.getMessage());
    }

    @Test
    @DisplayName("É uma exceção não verificada (unchecked), como IllegalArgumentException")
    void eExcecaoNaoVerificada() {
        DadosInvalidosException e = new DadosInvalidosException("x");
        assertTrue(e instanceof IllegalArgumentException);
        assertTrue(e instanceof RuntimeException);
    }

    @Test
    @DisplayName("Pode ser lançada e capturada")
    void podeSerLancada() {
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            throw new DadosInvalidosException("salário negativo");
        });
        assertEquals("salário negativo", e.getMessage());
    }
}
