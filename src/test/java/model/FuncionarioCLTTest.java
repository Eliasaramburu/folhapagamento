package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuncionarioCLTTest {

    private static final String CPF = "529.982.247-25";

    private FuncionarioCLT criar() {
        return new FuncionarioCLT(1, "Ana Lima", CPF, 3000.0, 500.0);
    }

    @Test
    @DisplayName("Remuneração = salário-base + adicional de benefícios")
    void calculaRemuneracao() {
        assertEquals(3500.0, criar().calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Remuneração sem benefícios é igual ao salário-base")
    void remuneracaoSemBeneficios() {
        FuncionarioCLT f = new FuncionarioCLT(1, "Ana Lima", CPF, 3000.0, 0.0);
        assertEquals(3000.0, f.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Tributos = 11% da remuneração")
    void calculaTributos() {
        assertEquals(385.0, criar().calcularTributos(), 0.001);
    }

    @Test
    @DisplayName("Salário líquido = remuneração - tributos")
    void calculaLiquido() {
        assertEquals(3115.0, criar().calcularSalarioLiquido(), 0.001);
    }

    @Test
    @DisplayName("Tipo é CLT e é Funcionario e Tributavel")
    void tipoEHierarquia() {
        FuncionarioCLT f = criar();
        assertEquals("CLT", f.getTipo());
        assertTrue(f instanceof Funcionario);
        assertTrue(f instanceof Tributavel);
    }

    @Test
    @DisplayName("Expõe o adicional de benefícios")
    void expoeAdicional() {
        assertEquals(500.0, criar().getAdicionalBeneficios(), 0.001);
    }

    @Test
    @DisplayName("Rejeita adicional de benefícios negativo, NaN ou infinito")
    void rejeitaAdicionalInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioCLT(1, "Ana", CPF, 3000.0, -1.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioCLT(1, "Ana", CPF, 3000.0, Double.NaN));
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioCLT(1, "Ana", CPF, 3000.0, Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Rejeita dados da classe base inválidos (salário negativo, CPF inválido)")
    void rejeitaDadosDaBaseInvalidos() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioCLT(1, "Ana", CPF, -3000.0, 0.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioCLT(1, "Ana", "000.000.000-00", 3000.0, 0.0));
    }

    @Test
    @DisplayName("Setter do adicional atualiza o valor e recalcula a remuneração")
    void setterAdicional() {
        FuncionarioCLT f = criar();
        f.setAdicionalBeneficios(1000.0);
        assertEquals(1000.0, f.getAdicionalBeneficios(), 0.001);
        assertEquals(4000.0, f.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Setter do adicional rejeita negativo e preserva o valor anterior")
    void setterAdicionalInvalido() {
        FuncionarioCLT f = criar();
        assertThrows(DadosInvalidosException.class, () -> f.setAdicionalBeneficios(-10.0));
        assertEquals(500.0, f.getAdicionalBeneficios(), 0.001);
    }

    @Test
    @DisplayName("exibirDados inclui adicional, tributos e líquido")
    void exibeDados() {
        String dados = criar().exibirDados();
        assertTrue(dados.contains("Tipo: CLT"));
        assertTrue(dados.contains("Adicional de benefícios: R$ 500,00"));
        assertTrue(dados.contains("Remuneração: R$ 3.500,00"));
        assertTrue(dados.contains("Tributos: R$ 385,00"));
        assertTrue(dados.contains("Salário líquido: R$ 3.115,00"));
    }
}
