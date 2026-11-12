package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdentificavelTest {

    @Test
    @DisplayName("Identificavel é uma interface que retorna o código")
    void interfaceRetornaCodigo() {
        assertTrue(Identificavel.class.isInterface());
        Identificavel identificavel = () -> 42;
        assertEquals(42, identificavel.getCodigo());
    }

    @Test
    @DisplayName("Funcionario (e portanto as classes concretas) implementa Identificavel")
    void funcionarioImplementaIdentificavel() {
        Identificavel identificavel = new FuncionarioCLT(10, "Ana Lima", "52998224725", 3000.0, 500.0);
        assertEquals(10, identificavel.getCodigo());
    }
}
