package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testa as regras da classe abstrata {@link Funcionario} por meio de uma
 * subclasse mínima, usada apenas nos testes.
 */
class FuncionarioTest {

    private static final String CPF_VALIDO = "529.982.247-25";

    /** Subclasse mínima só para instanciar a classe abstrata. */
    private static class FuncionarioFalso extends Funcionario {
        FuncionarioFalso(int codigo, String nome, String cpf, double salarioBase) {
            super(codigo, nome, cpf, salarioBase);
        }

        @Override
        public double calcularRemuneracao() {
            return getSalarioBase();
        }

        @Override
        public String getTipo() {
            return "FALSO";
        }

        @Override
        protected String detalhesEspecificos() {
            return "Detalhe: teste";
        }
    }

    private FuncionarioFalso criar() {
        return new FuncionarioFalso(1, "Maria Souza", CPF_VALIDO, 1000.0);
    }

    // ---------- construtor ----------

    @Test
    @DisplayName("Cria funcionário válido e expõe os dados pelos getters")
    void criaFuncionarioValido() {
        Funcionario f = criar();
        assertEquals(1, f.getCodigo());
        assertEquals("Maria Souza", f.getNome());
        assertEquals("52998224725", f.getCpf());
        assertEquals("529.982.247-25", f.getCpfFormatado());
        assertEquals(1000.0, f.getSalarioBase(), 0.001);
    }

    @Test
    @DisplayName("Remove espaços sobrando do nome")
    void removeEspacosDoNome() {
        Funcionario f = new FuncionarioFalso(1, "  Maria Souza  ", CPF_VALIDO, 1000.0);
        assertEquals("Maria Souza", f.getNome());
    }

    @Test
    @DisplayName("Aceita CPF sem formatação")
    void aceitaCpfSemFormatacao() {
        Funcionario f = new FuncionarioFalso(1, "Maria Souza", "52998224725", 1000.0);
        assertEquals("52998224725", f.getCpf());
    }

    @Test
    @DisplayName("Aceita salário-base zero")
    void aceitaSalarioZero() {
        Funcionario f = new FuncionarioFalso(1, "Maria Souza", CPF_VALIDO, 0.0);
        assertEquals(0.0, f.getSalarioBase(), 0.001);
    }

    @Test
    @DisplayName("Rejeita código zero ou negativo")
    void rejeitaCodigoInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(0, "Maria", CPF_VALIDO, 1000.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(-5, "Maria", CPF_VALIDO, 1000.0));
    }

    @Test
    @DisplayName("Rejeita nome nulo, vazio ou só com espaços")
    void rejeitaNomeInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, null, CPF_VALIDO, 1000.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "", CPF_VALIDO, 1000.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "   ", CPF_VALIDO, 1000.0));
    }

    @Test
    @DisplayName("Rejeita CPF nulo, vazio ou inválido")
    void rejeitaCpfInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "Maria", null, 1000.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "Maria", "", 1000.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "Maria", "123.456.789-00", 1000.0));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "Maria", "111.111.111-11", 1000.0));
    }

    @Test
    @DisplayName("Rejeita salário-base negativo, NaN ou infinito")
    void rejeitaSalarioInvalido() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "Maria", CPF_VALIDO, -0.01));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioFalso(1, "Maria", CPF_VALIDO, Double.NaN));
        assertThrows(DadosInvalidosException.class,
                () -> new FuncionarioFalso(1, "Maria", CPF_VALIDO, Double.POSITIVE_INFINITY));
    }

    // ---------- setters ----------

    @Test
    @DisplayName("Setters atualizam os valores válidos")
    void settersAtualizamValores() {
        Funcionario f = criar();
        f.setNome("Joana Dias");
        f.setCpf("111.444.777-35");
        f.setSalarioBase(2500.0);
        assertEquals("Joana Dias", f.getNome());
        assertEquals("11144477735", f.getCpf());
        assertEquals(2500.0, f.getSalarioBase(), 0.001);
    }

    @Test
    @DisplayName("Setters rejeitam valores inválidos e preservam o valor anterior")
    void settersRejeitamInvalidosEPreservam() {
        Funcionario f = criar();
        assertThrows(DadosInvalidosException.class, () -> f.setNome(" "));
        assertThrows(DadosInvalidosException.class, () -> f.setCpf("123"));
        assertThrows(DadosInvalidosException.class, () -> f.setSalarioBase(-1));
        assertEquals("Maria Souza", f.getNome());
        assertEquals("52998224725", f.getCpf());
        assertEquals(1000.0, f.getSalarioBase(), 0.001);
    }

    // ---------- comportamento comum ----------

    @Test
    @DisplayName("Funcionario implementa Identificavel")
    void implementaIdentificavel() {
        Identificavel identificavel = criar();
        assertEquals(1, identificavel.getCodigo());
    }

    @Test
    @DisplayName("Funcionário não Tributavel tem salário líquido igual à remuneração")
    void liquidoSemTributos() {
        Funcionario f = criar();
        assertEquals(1000.0, f.calcularSalarioLiquido(), 0.001);
    }

    @Test
    @DisplayName("Formata valores em reais no padrão brasileiro")
    void formataMoeda() {
        assertEquals("R$ 1.234,50", Funcionario.formatarMoeda(1234.5));
        assertEquals("R$ 0,00", Funcionario.formatarMoeda(0));
    }

    @Test
    @DisplayName("Arredonda para duas casas decimais")
    void arredonda() {
        assertEquals(385.0, Funcionario.arredondar(385.00000000000006), 0.0);
        assertEquals(10.57, Funcionario.arredondar(10.5678), 0.0);
    }

    @Test
    @DisplayName("exibirDados traz os dados completos do funcionário")
    void exibeDadosCompletos() {
        String dados = criar().exibirDados();
        assertTrue(dados.contains("Código: 1"));
        assertTrue(dados.contains("Tipo: FALSO"));
        assertTrue(dados.contains("Nome: Maria Souza"));
        assertTrue(dados.contains("CPF: 529.982.247-25"));
        assertTrue(dados.contains("Salário-base: R$ 1.000,00"));
        assertTrue(dados.contains("Detalhe: teste"));
        assertTrue(dados.contains("Remuneração: R$ 1.000,00"));
        assertTrue(dados.contains("Salário líquido: R$ 1.000,00"));
    }
}
