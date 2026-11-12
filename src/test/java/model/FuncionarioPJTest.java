package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuncionarioPJTest {

    private static final String CPF = "111.444.777-35";

    private FuncionarioPJ criar() {
        return new FuncionarioPJ(2, "Bruno Reis", CPF, 2500.0, 3);
    }

    @Test
    @DisplayName("Remuneração = valor por projeto x quantidade de projetos")
    void calculaRemuneracao() {
        assertEquals(7500.0, criar().calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Sem projetos realizados a remuneração é zero")
    void remuneracaoSemProjetos() {
        FuncionarioPJ f = new FuncionarioPJ(2, "Bruno Reis", CPF, 2500.0, 0);
        assertEquals(0.0, f.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("PJ não tem salário-base")
    void naoTemSalarioBase() {
        assertEquals(0.0, criar().getSalarioBase(), 0.001);
    }

    @Test
    @DisplayName("Tipo é PJ e não é Tributavel (o próprio PJ recolhe seus impostos)")
    void tipoEHierarquia() {
        FuncionarioPJ f = criar();
        assertEquals("PJ", f.getTipo());
        assertTrue(f instanceof Funcionario);
        assertFalse(Tributavel.class.isAssignableFrom(FuncionarioPJ.class));
    }

    @Test
    @DisplayName("Salário líquido é igual à remuneração, pois não há tributos")
    void liquidoIgualRemuneracao() {
        assertEquals(7500.0, criar().calcularSalarioLiquido(), 0.001);
    }

    @Test
    @DisplayName("Expõe valor por projeto e quantidade de projetos")
    void expoeAtributos() {
        FuncionarioPJ f = criar();
        assertEquals(2500.0, f.getValorPorProjeto(), 0.001);
        assertEquals(3, f.getQuantidadeProjetos());
    }

    @Test
    @DisplayName("Rejeita valor por projeto negativo, NaN ou infinito")
    void rejeitaValorInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioPJ(2, "Bruno", CPF, -1.0, 3));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioPJ(2, "Bruno", CPF, Double.NaN, 3));
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioPJ(2, "Bruno", CPF, Double.POSITIVE_INFINITY, 3));
    }

    @Test
    @DisplayName("Rejeita quantidade de projetos negativa")
    void rejeitaQuantidadeInvalida() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioPJ(2, "Bruno", CPF, 2500.0, -1));
    }

    @Test
    @DisplayName("Rejeita dados da classe base inválidos")
    void rejeitaDadosDaBaseInvalidos() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioPJ(2, "Bruno", "123.456.789-00", 2500.0, 3));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioPJ(0, "Bruno", CPF, 2500.0, 3));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioPJ(2, " ", CPF, 2500.0, 3));
    }

    @Test
    @DisplayName("Setters atualizam valores válidos e recalculam a remuneração")
    void settersAtualizam() {
        FuncionarioPJ f = criar();
        f.setValorPorProjeto(3000.0);
        f.setQuantidadeProjetos(4);
        assertEquals(12000.0, f.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Setters rejeitam valores inválidos e preservam os anteriores")
    void settersRejeitamInvalidos() {
        FuncionarioPJ f = criar();
        assertThrows(DadosInvalidosException.class, () -> f.setValorPorProjeto(-5.0));
        assertThrows(DadosInvalidosException.class, () -> f.setQuantidadeProjetos(-2));
        assertEquals(2500.0, f.getValorPorProjeto(), 0.001);
        assertEquals(3, f.getQuantidadeProjetos());
    }

    @Test
    @DisplayName("exibirDados mostra projetos e não mostra tributos")
    void exibeDados() {
        String dados = criar().exibirDados();
        assertTrue(dados.contains("Tipo: PJ"));
        assertTrue(dados.contains("Valor por projeto: R$ 2.500,00"));
        assertTrue(dados.contains("Projetos realizados: 3"));
        assertTrue(dados.contains("Remuneração: R$ 7.500,00"));
        assertFalse(dados.contains("Tributos"));
    }
}
