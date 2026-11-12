package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        FuncionarioJaCadastradoException e = new FuncionarioJaCadastradoException("x");
        assertTrue(e instanceof Exception);
        assertTrue(!(e instanceof RuntimeException));
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
