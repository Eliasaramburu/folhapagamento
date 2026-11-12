package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidadorCpfTest {

    @Test
    @DisplayName("Aceita CPFs válidos só com dígitos")
    void aceitaCpfSoDigitos() {
        assertTrue(ValidadorCpf.isValido("52998224725"));
        assertTrue(ValidadorCpf.isValido("11144477735"));
        assertTrue(ValidadorCpf.isValido("39053344705"));
    }

    @Test
    @DisplayName("Aceita CPFs válidos formatados")
    void aceitaCpfFormatado() {
        assertTrue(ValidadorCpf.isValido("529.982.247-25"));
        assertTrue(ValidadorCpf.isValido("111.444.777-35"));
    }

    @Test
    @DisplayName("Rejeita CPF com dígito verificador errado")
    void rejeitaDigitoVerificadorErrado() {
        assertFalse(ValidadorCpf.isValido("52998224726"));
        assertFalse(ValidadorCpf.isValido("52998224715"));
    }

    @Test
    @DisplayName("Rejeita CPF com todos os dígitos iguais")
    void rejeitaDigitosRepetidos() {
        assertFalse(ValidadorCpf.isValido("00000000000"));
        assertFalse(ValidadorCpf.isValido("111.111.111-11"));
        assertFalse(ValidadorCpf.isValido("99999999999"));
    }

    @Test
    @DisplayName("Rejeita CPF nulo, vazio, curto, longo ou com letras")
    void rejeitaFormatosInvalidos() {
        assertFalse(ValidadorCpf.isValido(null));
        assertFalse(ValidadorCpf.isValido(""));
        assertFalse(ValidadorCpf.isValido("   "));
        assertFalse(ValidadorCpf.isValido("123"));
        assertFalse(ValidadorCpf.isValido("529982247251"));
        assertFalse(ValidadorCpf.isValido("5299822472a"));
        assertFalse(ValidadorCpf.isValido("529-982-247.25"));
    }

    @Test
    @DisplayName("Normaliza removendo tudo que não é dígito")
    void normaliza() {
        assertEquals("52998224725", ValidadorCpf.normalizar("529.982.247-25"));
        assertEquals("52998224725", ValidadorCpf.normalizar(" 52998224725 "));
        assertNull(ValidadorCpf.normalizar(null));
    }

    @Test
    @DisplayName("Formata 11 dígitos no padrão 000.000.000-00")
    void formata() {
        assertEquals("529.982.247-25", ValidadorCpf.formatar("52998224725"));
    }

    @Test
    @DisplayName("Formatar exige exatamente 11 dígitos")
    void formatarExige11Digitos() {
        assertThrows(IllegalArgumentException.class, () -> ValidadorCpf.formatar("123"));
        assertThrows(IllegalArgumentException.class, () -> ValidadorCpf.formatar(null));
    }
}
