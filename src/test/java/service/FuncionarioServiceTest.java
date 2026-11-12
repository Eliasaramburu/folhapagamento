package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import java.lang.reflect.Method;
import java.util.List;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.FuncionarioRepository;

class FuncionarioServiceTest {

    private FuncionarioService servico;
    private Funcionario ana;
    private Funcionario bruno;
    private Funcionario carlos;

    @BeforeEach
    void preparar() {
        servico = new FuncionarioService(new FuncionarioRepository());
        ana = new FuncionarioCLT(1, "Ana Lima", "529.982.247-25", 3000.0, 500.0);
        bruno = new FuncionarioPJ(2, "Bruno Reis", "111.444.777-35", 2500.0, 3);
        carlos = new FuncionarioComissionado(3, "Carlos Mota", "390.533.447-05", 2000.0, 5.0, 10000.0);
    }

    private void cadastrarTodos() throws Exception {
        servico.cadastrar(ana);
        servico.cadastrar(bruno);
        servico.cadastrar(carlos);
    }

    // ---------- construtor ----------

    @Test
    @DisplayName("Exige um repositório")
    void exigeRepositorio() {
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioService(null));
    }

    // ---------- cadastrar / listar ----------

    @Test
    @DisplayName("Cadastra e lista funcionários na ordem de cadastro")
    void cadastraELista() throws Exception {
        cadastrarTodos();
        List<Funcionario> lista = servico.listar();
        assertEquals(3, lista.size());
        assertSame(ana, lista.get(0));
        assertSame(bruno, lista.get(1));
        assertSame(carlos, lista.get(2));
    }

    @Test
    @DisplayName("Lista vazia quando ninguém foi cadastrado")
    void listaVazia() {
        assertTrue(servico.listar().isEmpty());
    }

    @Test
    @DisplayName("Não permite cadastrar código já existente")
    void rejeitaCodigoDuplicado() throws Exception {
        servico.cadastrar(ana);
        Funcionario mesmoCodigo = new FuncionarioCLT(1, "Outra Pessoa", "168.995.350-09", 2000.0, 0.0);

        FuncionarioJaCadastradoException e = assertThrows(FuncionarioJaCadastradoException.class,
                () -> servico.cadastrar(mesmoCodigo));
        assertTrue(e.getMessage().contains("1"));
        assertEquals(1, servico.listar().size());
    }

    @Test
    @DisplayName("Não permite cadastrar CPF já existente")
    void rejeitaCpfDuplicado() throws Exception {
        servico.cadastrar(ana);
        Funcionario mesmoCpf = new FuncionarioCLT(9, "Outra Pessoa", "52998224725", 2000.0, 0.0);
        assertThrows(FuncionarioJaCadastradoException.class, () -> servico.cadastrar(mesmoCpf));
    }

    @Test
    @DisplayName("Não permite cadastrar funcionário nulo")
    void rejeitaNulo() {
        assertThrows(DadosInvalidosException.class, () -> servico.cadastrar(null));
    }

    // ---------- buscas (sobrecarga) ----------

    @Test
    @DisplayName("Busca por código")
    void buscaPorCodigo() throws Exception {
        cadastrarTodos();
        assertSame(bruno, servico.buscarFuncionario(2));
    }

    @Test
    @DisplayName("Busca por código inexistente lança FuncionarioNaoEncontradoException")
    void buscaPorCodigoInexistente() throws Exception {
        cadastrarTodos();
        FuncionarioNaoEncontradoException e = assertThrows(FuncionarioNaoEncontradoException.class,
                () -> servico.buscarFuncionario(99));
        assertTrue(e.getMessage().contains("99"));
    }

    @Test
    @DisplayName("Busca por CPF, formatado ou não")
    void buscaPorCpf() throws Exception {
        cadastrarTodos();
        assertSame(carlos, servico.buscarFuncionario("390.533.447-05"));
        assertSame(carlos, servico.buscarFuncionario("39053344705"));
    }

    @Test
    @DisplayName("Busca por CPF inexistente lança FuncionarioNaoEncontradoException")
    void buscaPorCpfInexistente() throws Exception {
        cadastrarTodos();
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.buscarFuncionario("168.995.350-09"));
    }

    @Test
    @DisplayName("Busca por CPF nulo ou vazio lança DadosInvalidosException")
    void buscaPorCpfInvalido() {
        assertThrows(DadosInvalidosException.class, () -> servico.buscarFuncionario((String) null));
        assertThrows(DadosInvalidosException.class, () -> servico.buscarFuncionario("  "));
    }

    @Test
    @DisplayName("Busca exata por nome ignora maiúsculas/minúsculas")
    void buscaExataPorNome() throws Exception {
        cadastrarTodos();
        List<Funcionario> resultado = servico.buscarFuncionario("ana lima", true);
        assertEquals(1, resultado.size());
        assertSame(ana, resultado.get(0));
    }

    @Test
    @DisplayName("Busca exata não encontra nome parcial")
    void buscaExataNaoEncontraParcial() throws Exception {
        cadastrarTodos();
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.buscarFuncionario("Ana", true));
    }

    @Test
    @DisplayName("Busca parcial por nome encontra todos que contêm o texto")
    void buscaParcialPorNome() throws Exception {
        cadastrarTodos();
        servico.cadastrar(new FuncionarioPJ(4, "Mariana Souza", "168.995.350-09", 1000.0, 1));

        List<Funcionario> resultado = servico.buscarFuncionario("ana", false);
        assertEquals(2, resultado.size());
        assertSame(ana, resultado.get(0));
        assertEquals("Mariana Souza", resultado.get(1).getNome());
    }

    @Test
    @DisplayName("Busca por nome sem resultado lança FuncionarioNaoEncontradoException")
    void buscaPorNomeSemResultado() throws Exception {
        cadastrarTodos();
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.buscarFuncionario("Zzz", false));
    }

    @Test
    @DisplayName("Busca por nome nulo ou vazio lança DadosInvalidosException")
    void buscaPorNomeInvalido() {
        assertThrows(DadosInvalidosException.class, () -> servico.buscarFuncionario(null, true));
        assertThrows(DadosInvalidosException.class, () -> servico.buscarFuncionario("", false));
    }

    @Test
    @DisplayName("Sobrecarga: existem três métodos buscarFuncionario com parâmetros diferentes")
    void sobrecargaDeBuscarFuncionario() {
        int quantidade = 0;
        for (Method metodo : FuncionarioService.class.getDeclaredMethods()) {
            if (metodo.getName().equals("buscarFuncionario")) {
                quantidade++;
            }
        }
        assertEquals(3, quantidade);
    }

    // ---------- remover ----------

    @Test
    @DisplayName("Remove funcionário existente")
    void removeExistente() throws Exception {
        cadastrarTodos();
        servico.remover(2);
        assertEquals(2, servico.listar().size());
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.buscarFuncionario(2));
    }

    @Test
    @DisplayName("Remover inexistente lança FuncionarioNaoEncontradoException")
    void removeInexistente() throws Exception {
        cadastrarTodos();
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.remover(99));
        assertEquals(3, servico.listar().size());
    }

    // ---------- remuneração e dados ----------

    @Test
    @DisplayName("Calcula a remuneração de cada funcionário pelo código")
    void calculaRemuneracaoPorCodigo() throws Exception {
        cadastrarTodos();
        assertEquals(3500.0, servico.calcularRemuneracao(1), 0.001);
        assertEquals(7500.0, servico.calcularRemuneracao(2), 0.001);
        assertEquals(2500.0, servico.calcularRemuneracao(3), 0.001);
    }

    @Test
    @DisplayName("Calcular remuneração de código inexistente lança FuncionarioNaoEncontradoException")
    void calculaRemuneracaoInexistente() {
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.calcularRemuneracao(99));
    }

    @Test
    @DisplayName("Exibe os dados completos de um funcionário")
    void exibeDados() throws Exception {
        cadastrarTodos();
        String dados = servico.exibirDados(1);
        assertTrue(dados.contains("Ana Lima"));
        assertTrue(dados.contains("529.982.247-25"));
        assertTrue(dados.contains("Remuneração: R$ 3.500,00"));
    }

    @Test
    @DisplayName("Exibir dados de código inexistente lança FuncionarioNaoEncontradoException")
    void exibeDadosInexistente() {
        assertThrows(FuncionarioNaoEncontradoException.class, () -> servico.exibirDados(99));
    }

    // ---------- relatório e folha ----------

    @Test
    @DisplayName("Total da folha é a soma das remunerações de todos")
    void totalDaFolha() throws Exception {
        cadastrarTodos();
        assertEquals(13500.0, servico.calcularTotalFolha(), 0.001);
    }

    @Test
    @DisplayName("Total da folha é zero sem funcionários e acompanha as remoções")
    void totalDaFolhaVazioEAposRemocao() throws Exception {
        assertEquals(0.0, servico.calcularTotalFolha(), 0.001);
        cadastrarTodos();
        servico.remover(2);
        assertEquals(6000.0, servico.calcularTotalFolha(), 0.001);
    }

    @Test
    @DisplayName("Relatório lista todos os funcionários, suas remunerações e o total")
    void geraRelatorio() throws Exception {
        cadastrarTodos();
        String relatorio = servico.gerarRelatorio();
        assertTrue(relatorio.contains("Ana Lima"));
        assertTrue(relatorio.contains("Bruno Reis"));
        assertTrue(relatorio.contains("Carlos Mota"));
        assertTrue(relatorio.contains("CLT"));
        assertTrue(relatorio.contains("PJ"));
        assertTrue(relatorio.contains("Comissionado"));
        assertTrue(relatorio.contains("R$ 3.500,00"));
        assertTrue(relatorio.contains("R$ 7.500,00"));
        assertTrue(relatorio.contains("R$ 2.500,00"));
        assertTrue(relatorio.contains("Total da folha de pagamento: R$ 13.500,00"));
    }

    @Test
    @DisplayName("Relatório sem funcionários informa que não há cadastros")
    void relatorioVazio() {
        String relatorio = servico.gerarRelatorio();
        assertTrue(relatorio.contains("Nenhum funcionário cadastrado"));
        assertTrue(relatorio.contains("Total da folha de pagamento: R$ 0,00"));
    }
}
