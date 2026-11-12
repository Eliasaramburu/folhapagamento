package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuncionarioComissionadoTest {

    private static final String CPF = "390.533.447-05";

    private FuncionarioComissionado criar() {
        return new FuncionarioComissionado(3, "Carlos Mota", CPF, 2000.0, 5.0, 10000.0);
    }

    @Test
    @DisplayName("Comissão = vendas x percentual / 100")
    void calculaComissao() {
        assertEquals(500.0, criar().calcularComissao(), 0.001);
    }

    @Test
    @DisplayName("Remuneração = salário-base + comissão")
    void calculaRemuneracao() {
        assertEquals(2500.0, criar().calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Sem vendas a remuneração é só o salário-base")
    void semVendas() {
        FuncionarioComissionado f = new FuncionarioComissionado(3, "Carlos", CPF, 2000.0, 5.0, 0.0);
        assertEquals(0.0, f.calcularComissao(), 0.001);
        assertEquals(2000.0, f.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Aceita percentuais nos limites 0% e 100%")
    void aceitaLimitesDoPercentual() {
        FuncionarioComissionado zero = new FuncionarioComissionado(3, "Carlos", CPF, 2000.0, 0.0, 10000.0);
        FuncionarioComissionado cem = new FuncionarioComissionado(4, "Carla", "529.982.247-25", 2000.0, 100.0, 10000.0);
        assertEquals(0.0, zero.calcularComissao(), 0.001);
        assertEquals(10000.0, cem.calcularComissao(), 0.001);
    }

    @Test
    @DisplayName("Tributos = 13% da remuneração e líquido = remuneração - tributos")
    void calculaTributosELiquido() {
        FuncionarioComissionado f = criar();
        assertEquals(325.0, f.calcularTributos(), 0.001);
        assertEquals(2175.0, f.calcularSalarioLiquido(), 0.001);
    }

    @Test
    @DisplayName("Tipo é Comissionado e é Funcionario e Tributavel")
    void tipoEHierarquia() {
        FuncionarioComissionado f = criar();
        assertEquals("Comissionado", f.getTipo());
        assertTrue(f instanceof Funcionario);
        assertTrue(f instanceof Tributavel);
    }

    @Test
    @DisplayName("Rejeita percentual negativo, acima de 100 ou NaN")
    void rejeitaPercentualInvalido() {
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioComissionado(3, "Carlos", CPF, 2000.0, -0.1, 1000.0));
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioComissionado(3, "Carlos", CPF, 2000.0, 100.01, 1000.0));
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioComissionado(3, "Carlos", CPF, 2000.0, Double.NaN, 1000.0));
    }

    @Test
    @DisplayName("Rejeita total de vendas negativo")
    void rejeitaVendasNegativas() {
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioComissionado(3, "Carlos", CPF, 2000.0, 5.0, -1.0));
    }

    @Test
    @DisplayName("Rejeita salário-base negativo e CPF inválido (classe base)")
    void rejeitaDadosDaBaseInvalidos() {
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioComissionado(3, "Carlos", CPF, -2000.0, 5.0, 1000.0));
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioComissionado(3, "Carlos", "12345678900", 2000.0, 5.0, 1000.0));
    }

    @Test
    @DisplayName("Setters atualizam valores válidos e recalculam a remuneração")
    void settersAtualizam() {
        FuncionarioComissionado f = criar();
        f.setPercentualComissao(10.0);
        f.setTotalVendas(20000.0);
        assertEquals(10.0, f.getPercentualComissao(), 0.001);
        assertEquals(20000.0, f.getTotalVendas(), 0.001);
        assertEquals(4000.0, f.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Setters rejeitam valores inválidos e preservam os anteriores")
    void settersRejeitamInvalidos() {
        FuncionarioComissionado f = criar();
        assertThrows(DadosInvalidosException.class, () -> f.setPercentualComissao(150.0));
        assertThrows(DadosInvalidosException.class, () -> f.setTotalVendas(-1.0));
        assertEquals(5.0, f.getPercentualComissao(), 0.001);
        assertEquals(10000.0, f.getTotalVendas(), 0.001);
    }

    @Test
    @DisplayName("exibirDados inclui percentual, vendas, comissão e tributos")
    void exibeDados() {
        String dados = criar().exibirDados();
        assertTrue(dados.contains("Tipo: Comissionado"));
        assertTrue(dados.contains("Percentual de comissão: 5,00%"));
        assertTrue(dados.contains("Total de vendas: R$ 10.000,00"));
        assertTrue(dados.contains("Comissão: R$ 500,00"));
        assertTrue(dados.contains("Remuneração: R$ 2.500,00"));
        assertTrue(dados.contains("Tributos: R$ 325,00"));
    }
}
