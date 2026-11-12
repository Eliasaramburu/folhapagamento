package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuncionarioNaoEncontradoExceptionTest {

    @Test
    @DisplayName("Guarda a mensagem informada")
    void guardaMensagem() {
        FuncionarioNaoEncontradoException e = new FuncionarioNaoEncontradoException("Funcionário 7 não encontrado");
        assertEquals("Funcionário 7 não encontrado", e.getMessage());
    }

    @Test
    @DisplayName("É uma exceção verificada (checked), obrigando o tratamento")
    void eExcecaoVerificada() {
        FuncionarioNaoEncontradoException e = new FuncionarioNaoEncontradoException("x");
        assertTrue(e instanceof Exception);
        assertTrue(!(e instanceof RuntimeException));
    }

    @Test
    @DisplayName("Pode ser lançada e capturada")
    void podeSerLancada() {
        FuncionarioNaoEncontradoException e = assertThrows(FuncionarioNaoEncontradoException.class, () -> {
            throw new FuncionarioNaoEncontradoException("não existe");
        });
        assertEquals("não existe", e.getMessage());
    }
}
