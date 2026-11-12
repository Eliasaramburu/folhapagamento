package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuncionarioJaCadastradoExceptionTest {

    @Test
    @DisplayName("Guarda a mensagem informada")
    void guardaMensagem() {
        FuncionarioJaCadastradoException e = new FuncionarioJaCadastradoException("Código 1 já existe");
        assertEquals("Código 1 já existe", e.getMessage());
    }

    @Test
    @DisplayName("É uma exceção verificada (checked), obrigando o tratamento")
    void eExcecaoVerificada() {
        Class<?> tipo = FuncionarioJaCadastradoException.class;
        assertTrue(Exception.class.isAssignableFrom(tipo));
        assertFalse(RuntimeException.class.isAssignableFrom(tipo));
    }

    @Test
    @DisplayName("Pode ser lançada e capturada")
    void podeSerLancada() {
        FuncionarioJaCadastradoException e = assertThrows(FuncionarioJaCadastradoException.class, () -> {
            throw new FuncionarioJaCadastradoException("duplicado");
        });
        assertEquals("duplicado", e.getMessage());
    }
}
