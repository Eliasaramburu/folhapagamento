package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comportamento polimórfico por sobreposição: referências do tipo
 * {@link Funcionario} executam a implementação do objeto real.
 */
class PolimorfismoTest {

    private Funcionario clt;
    private Funcionario pj;
    private Funcionario comissionado;
    private List<Funcionario> todos;

    @BeforeEach
    void preparar() {
        clt = new FuncionarioCLT(1, "Ana Lima", "529.982.247-25", 3000.0, 500.0);
        pj = new FuncionarioPJ(2, "Bruno Reis", "111.444.777-35", 2500.0, 3);
        comissionado = new FuncionarioComissionado(3, "Carlos Mota", "390.533.447-05", 2000.0, 5.0, 10000.0);
        todos = List.of(clt, pj, comissionado);
    }

    @Test
    @DisplayName("calcularRemuneracao executa a regra de cada categoria")
    void remuneracaoPorCategoria() {
        assertEquals(3500.0, clt.calcularRemuneracao(), 0.001);
        assertEquals(7500.0, pj.calcularRemuneracao(), 0.001);
        assertEquals(2500.0, comissionado.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Mesma chamada, comportamentos diferentes: remunerações distintas")
    void comportamentosDiferentes() {
        Set<Double> remuneracoes = new HashSet<>();
        for (Funcionario f : todos) {
            remuneracoes.add(f.calcularRemuneracao());
        }
        assertEquals(3, remuneracoes.size());
    }

    @Test
    @DisplayName("Soma das remunerações percorrendo uma lista de Funcionario")
    void somaDasRemuneracoes() {
        double total = 0;
        for (Funcionario f : todos) {
            total += f.calcularRemuneracao();
        }
        assertEquals(13500.0, total, 0.001);
    }

    @Test
    @DisplayName("Salário líquido usa os tributos da subclasse, quando existirem")
    void liquidoPolimorfico() {
        assertEquals(3115.0, clt.calcularSalarioLiquido(), 0.001);
        assertEquals(7500.0, pj.calcularSalarioLiquido(), 0.001);
        assertEquals(2175.0, comissionado.calcularSalarioLiquido(), 0.001);
    }

    @Test
    @DisplayName("Somente CLT e Comissionado são Tributavel; a soma dos tributos confere")
    void tributavelSomenteOndeFazSentido() {
        double tributos = 0;
        int tributaveis = 0;
        for (Funcionario f : todos) {
            if (f instanceof Tributavel t) {
                tributos += t.calcularTributos();
                tributaveis++;
            }
        }
        assertEquals(2, tributaveis);
        assertEquals(710.0, tributos, 0.001);
    }

    @Test
    @DisplayName("Cada categoria informa o seu tipo")
    void tiposDistintos() {
        assertEquals("CLT", clt.getTipo());
        assertEquals("PJ", pj.getTipo());
        assertEquals("Comissionado", comissionado.getTipo());
    }

    @Test
    @DisplayName("exibirDados mostra os detalhes específicos de cada categoria")
    void exibirDadosPolimorfico() {
        assertTrue(clt.exibirDados().contains("Adicional de benefícios"));
        assertTrue(pj.exibirDados().contains("Projetos realizados"));
        assertTrue(comissionado.exibirDados().contains("Percentual de comissão"));
    }

    @Test
    @DisplayName("Todas as classes concretas são Identificavel")
    void todosSaoIdentificaveis() {
        int esperado = 1;
        for (Funcionario f : todos) {
            Identificavel identificavel = f;
            assertNotNull(identificavel);
            assertEquals(esperado++, identificavel.getCodigo());
        }
    }
}
