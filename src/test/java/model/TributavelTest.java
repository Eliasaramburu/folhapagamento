package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TributavelTest {

    @Test
    @DisplayName("Tributavel é uma interface que calcula os tributos")
    void interfaceCalculaTributos() {
        assertTrue(Tributavel.class.isInterface());
        Tributavel tributavel = () -> 12.5;
        assertEquals(12.5, tributavel.calcularTributos(), 0.001);
    }

    @Test
    @DisplayName("FuncionarioCLT é Tributavel e aplica seus tributos")
    void cltEhTributavel() {
        Tributavel tributavel = new FuncionarioCLT(1, "Ana Lima", "52998224725", 3000.0, 500.0);
        assertEquals(385.0, tributavel.calcularTributos(), 0.001);
    }
}
