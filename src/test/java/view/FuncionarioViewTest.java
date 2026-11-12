package view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.FuncionarioRepository;
import service.FuncionarioService;

/**
 * Simula a digitação do usuário e verifica o que a visão imprime.
 */
class FuncionarioViewTest {

    // Sequências de entrada: opção do menu + dados pedidos, cada um em uma linha.
    private static final String CADASTRA_ANA = "1\n1\nAna Lima\n529.982.247-25\n3000\n500\n";
    private static final String CADASTRA_BRUNO = "2\n2\nBruno Reis\n111.444.777-35\n2500\n3\n";
    private static final String CADASTRA_CARLOS = "3\n3\nCarlos Mota\n390.533.447-05\n2000\n5\n10000\n";
    private static final String SAIR = "0\n";

    private FuncionarioService servico;

    @BeforeEach
    void preparar() {
        servico = new FuncionarioService(new FuncionarioRepository());
    }

    /** Executa a visão com a entrada informada e devolve tudo o que ela imprimiu. */
    private String executar(String entrada) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        FuncionarioView visao = new FuncionarioView(
                servico, new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), saida);
        visao.iniciar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Exige o serviço, a entrada e a saída")
    void exigeDependencias() {
        ByteArrayInputStream entrada = new ByteArrayInputStream(new byte[0]);
        PrintStream saida = new PrintStream(new ByteArrayOutputStream());
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioView(null, entrada, saida));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioView(servico, null, saida));
        assertThrows(DadosInvalidosException.class, () -> new FuncionarioView(servico, entrada, null));
    }

    @Test
    @DisplayName("Mostra o menu e encerra com a opção 0")
    void mostraMenuEEncerra() {
        String saida = executar(SAIR);
        assertTrue(saida.contains("MENU"));
        assertTrue(saida.contains("Cadastrar funcionário CLT"));
        assertTrue(saida.contains("Encerrando"));
    }

    @Test
    @DisplayName("Cadastra um CLT e o lista")
    void cadastraCltELista() {
        String saida = executar(CADASTRA_ANA + "4\n" + SAIR);
        assertTrue(saida.contains("cadastrado com sucesso"));
        assertTrue(saida.contains("[1] CLT - Ana Lima (529.982.247-25)"));
    }

    @Test
    @DisplayName("Cadastra um PJ e calcula a remuneração")
    void cadastraPjECalcula() {
        String saida = executar(CADASTRA_BRUNO + "9\n2\n" + SAIR);
        assertTrue(saida.contains("cadastrado com sucesso"));
        assertTrue(saida.contains("R$ 7.500,00"));
    }

    @Test
    @DisplayName("Cadastra um Comissionado e exibe os dados completos")
    void cadastraComissionadoEExibeDados() {
        String saida = executar(CADASTRA_CARLOS + "8\n3\n" + SAIR);
        assertTrue(saida.contains("Percentual de comissão: 5,00%"));
        assertTrue(saida.contains("Tributos: R$ 325,00"));
    }

    @Test
    @DisplayName("Aceita vírgula como separador decimal")
    void aceitaVirgulaDecimal() {
        String entrada = "1\n1\nAna Lima\n529.982.247-25\n3000,50\n500\n9\n1\n" + SAIR;
        assertTrue(executar(entrada).contains("R$ 3.500,50"));
    }

    @Test
    @DisplayName("CPF inválido mostra o erro e o menu continua funcionando")
    void cpfInvalidoMostraErro() {
        String saida = executar("1\n1\nAna Lima\n123.456.789-00\n3000\n500\n" + SAIR);
        assertTrue(saida.contains("Erro:"));
        assertTrue(saida.contains("CPF inválido"));
        assertTrue(saida.contains("Encerrando"));
        assertFalse(saida.contains("cadastrado com sucesso"));
    }

    @Test
    @DisplayName("Valor negativo mostra o erro")
    void valorNegativoMostraErro() {
        String saida = executar("1\n1\nAna Lima\n529.982.247-25\n-3000\n500\n" + SAIR);
        assertTrue(saida.contains("Erro:"));
        assertTrue(saida.contains("salário-base"));
    }

    @Test
    @DisplayName("Código duplicado mostra o erro")
    void codigoDuplicadoMostraErro() {
        String saida = executar(CADASTRA_ANA + CADASTRA_ANA + SAIR);
        assertTrue(saida.contains("Já existe um funcionário com o código 1"));
    }

    @Test
    @DisplayName("Número digitado errado mostra o erro")
    void numeroInvalidoMostraErro() {
        String saida = executar("1\nabc\n" + SAIR);
        assertTrue(saida.contains("Erro:"));
        assertTrue(saida.contains("abc"));
    }

    @Test
    @DisplayName("Buscar código inexistente informa que não foi encontrado")
    void buscaCodigoInexistente() {
        String saida = executar("5\n99\n" + SAIR);
        assertTrue(saida.contains("não encontrado"));
    }

    @Test
    @DisplayName("Busca por código, CPF e nome encontra o funcionário")
    void buscas() {
        String saida = executar(CADASTRA_ANA + "5\n1\n" + "6\n529.982.247-25\n" + "7\nana\nn\n" + "7\nana lima\ns\n" + SAIR);
        int ocorrencias = saida.split("\\[1\\] CLT - Ana Lima", -1).length - 1;
        assertTrue(ocorrencias >= 4, "esperava 4 resultados de busca, mas houve " + ocorrencias);
    }

    @Test
    @DisplayName("Remove um funcionário e a listagem fica vazia")
    void removeFuncionario() {
        String saida = executar(CADASTRA_ANA + "10\n1\n" + "4\n" + SAIR);
        assertTrue(saida.contains("removido com sucesso"));
        assertTrue(saida.contains("Nenhum funcionário cadastrado"));
    }

    @Test
    @DisplayName("Remover inexistente informa que não foi encontrado")
    void removeInexistente() {
        String saida = executar("10\n99\n" + SAIR);
        assertTrue(saida.contains("não encontrado"));
    }

    @Test
    @DisplayName("Relatório e total da folha")
    void relatorioETotal() {
        String saida = executar(CADASTRA_ANA + CADASTRA_BRUNO + "11\n" + "12\n" + SAIR);
        assertTrue(saida.contains("RELATÓRIO DE FUNCIONÁRIOS"));
        assertTrue(saida.contains("Bruno Reis"));
        assertTrue(saida.contains("Total da folha de pagamento: R$ 11.000,00"));
    }

    @Test
    @DisplayName("Listar sem cadastros informa que não há funcionários")
    void listarVazio() {
        assertTrue(executar("4\n" + SAIR).contains("Nenhum funcionário cadastrado"));
    }

    @Test
    @DisplayName("Opção inexistente ou não numérica mostra aviso")
    void opcaoInvalida() {
        String saida = executar("99\nabc\n" + SAIR);
        int avisos = saida.split("Opção inválida", -1).length - 1;
        assertTrue(avisos == 2, "esperava 2 avisos, mas houve " + avisos);
    }

    @Test
    @DisplayName("Entrada que termina sem a opção 0 não derruba o programa")
    void entradaEncerrada() {
        String saida = executar("1\n1\n");
        assertTrue(saida.contains("Entrada encerrada"));
    }
}
