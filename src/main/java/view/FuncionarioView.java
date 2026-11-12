package view;

import exception.DadosInvalidosException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import service.FuncionarioService;

/**
 * Camada de visão: menu de console que interage com o usuário e apresenta os
 * resultados. As exceções do sistema são tratadas aqui, virando mensagens.
 *
 * <p>A entrada e a saída são recebidas pelo construtor, o que permite testar a
 * visão sem teclado nem tela.</p>
 */
public class FuncionarioView {

    private final FuncionarioService servico;
    private final Scanner entrada;
    private final PrintStream saida;

    public FuncionarioView(FuncionarioService servico, InputStream entrada, PrintStream saida) {
        if (servico == null || entrada == null || saida == null) {
            throw new DadosInvalidosException("Serviço, entrada e saída são obrigatórios");
        }
        this.servico = servico;
        this.entrada = new Scanner(entrada);
        this.saida = saida;
    }

    /** Executa o menu até o usuário escolher sair (ou a entrada acabar). */
    public void iniciar() {
        saida.println("=== Sistema de Funcionários e Folha de Pagamento ===");
        boolean continuar = true;
        while (continuar) {
            try {
                exibirMenu();
                continuar = executarOpcao(lerTexto("Escolha uma opção").trim());
            } catch (FuncionarioNaoEncontradoException | FuncionarioJaCadastradoException
                     | DadosInvalidosException e) {
                saida.println("Erro: " + e.getMessage());
            } catch (NoSuchElementException e) {
                saida.println();
                saida.println("Entrada encerrada.");
                return;
            }
        }
        saida.println("Encerrando o sistema. Até logo!");
    }

    private void exibirMenu() {
        saida.println();
        saida.println("----------------- MENU -----------------");
        saida.println(" 1 - Cadastrar funcionário CLT");
        saida.println(" 2 - Cadastrar funcionário PJ");
        saida.println(" 3 - Cadastrar funcionário comissionado");
        saida.println(" 4 - Listar funcionários");
        saida.println(" 5 - Buscar por código");
        saida.println(" 6 - Buscar por CPF");
        saida.println(" 7 - Buscar por nome");
        saida.println(" 8 - Exibir dados completos de um funcionário");
        saida.println(" 9 - Calcular remuneração de um funcionário");
        saida.println("10 - Remover funcionário");
        saida.println("11 - Gerar relatório de funcionários");
        saida.println("12 - Calcular total da folha de pagamento");
        saida.println(" 0 - Sair");
    }

    /** @return {@code false} quando o usuário pediu para sair. */
    private boolean executarOpcao(String opcao)
            throws FuncionarioNaoEncontradoException, FuncionarioJaCadastradoException {
        switch (opcao) {
            case "1" -> cadastrarClt();
            case "2" -> cadastrarPj();
            case "3" -> cadastrarComissionado();
            case "4" -> listar();
            case "5" -> buscarPorCodigo();
            case "6" -> buscarPorCpf();
            case "7" -> buscarPorNome();
            case "8" -> saida.println(servico.exibirDados(lerInteiro("Código")));
            case "9" -> calcularRemuneracao();
            case "10" -> remover();
            case "11" -> saida.println(servico.gerarRelatorio());
            case "12" -> saida.println("Total da folha de pagamento: "
                    + Funcionario.formatarMoeda(servico.calcularTotalFolha()));
            case "0" -> {
                return false;
            }
            default -> saida.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    // ---------- ações do menu ----------

    private void cadastrarClt() throws FuncionarioJaCadastradoException {
        int codigo = lerInteiro("Código");
        String nome = lerTexto("Nome");
        String cpf = lerTexto("CPF");
        double salarioBase = lerDecimal("Salário-base");
        double adicional = lerDecimal("Adicional de benefícios");
        servico.cadastrar(new FuncionarioCLT(codigo, nome, cpf, salarioBase, adicional));
        saida.println("Funcionário cadastrado com sucesso.");
    }

    private void cadastrarPj() throws FuncionarioJaCadastradoException {
        int codigo = lerInteiro("Código");
        String nome = lerTexto("Nome");
        String cpf = lerTexto("CPF");
        double valorPorProjeto = lerDecimal("Valor por projeto");
        int quantidade = lerInteiro("Quantidade de projetos realizados");
        servico.cadastrar(new FuncionarioPJ(codigo, nome, cpf, valorPorProjeto, quantidade));
        saida.println("Funcionário cadastrado com sucesso.");
    }

    private void cadastrarComissionado() throws FuncionarioJaCadastradoException {
        int codigo = lerInteiro("Código");
        String nome = lerTexto("Nome");
        String cpf = lerTexto("CPF");
        double salarioBase = lerDecimal("Salário-base");
        double percentual = lerDecimal("Percentual de comissão (0 a 100)");
        double vendas = lerDecimal("Total de vendas");
        servico.cadastrar(new FuncionarioComissionado(codigo, nome, cpf, salarioBase, percentual, vendas));
        saida.println("Funcionário cadastrado com sucesso.");
    }

    private void listar() {
        List<Funcionario> funcionarios = servico.listar();
        if (funcionarios.isEmpty()) {
            saida.println("Nenhum funcionário cadastrado.");
            return;
        }
        funcionarios.forEach(funcionario -> saida.println(resumo(funcionario)));
    }

    private void buscarPorCodigo() throws FuncionarioNaoEncontradoException {
        saida.println(resumo(servico.buscarFuncionario(lerInteiro("Código"))));
    }

    private void buscarPorCpf() throws FuncionarioNaoEncontradoException {
        saida.println(resumo(servico.buscarFuncionario(lerTexto("CPF"))));
    }

    private void buscarPorNome() throws FuncionarioNaoEncontradoException {
        String nome = lerTexto("Nome");
        String resposta = lerTexto("Busca exata? (s/n)").trim();
        boolean exata = resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("sim");
        servico.buscarFuncionario(nome, exata).forEach(funcionario -> saida.println(resumo(funcionario)));
    }

    private void calcularRemuneracao() throws FuncionarioNaoEncontradoException {
        int codigo = lerInteiro("Código");
        Funcionario funcionario = servico.buscarFuncionario(codigo);
        saida.println("Remuneração de " + funcionario.getNome() + ": "
                + Funcionario.formatarMoeda(servico.calcularRemuneracao(codigo)));
    }

    private void remover() throws FuncionarioNaoEncontradoException {
        servico.remover(lerInteiro("Código"));
        saida.println("Funcionário removido com sucesso.");
    }

    // ---------- leitura de dados ----------

    private String lerTexto(String rotulo) {
        saida.print(rotulo + ": ");
        return entrada.nextLine();
    }

    private int lerInteiro(String rotulo) {
        String texto = lerTexto(rotulo).trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Número inteiro inválido: \"" + texto + "\"");
        }
    }

    /** Aceita ponto ou vírgula como separador decimal. */
    private double lerDecimal(String rotulo) {
        String texto = lerTexto(rotulo).trim();
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Número inválido: \"" + texto + "\"");
        }
    }

    private static String resumo(Funcionario funcionario) {
        return "[" + funcionario.getCodigo() + "] " + funcionario.getTipo() + " - "
                + funcionario.getNome() + " (" + funcionario.getCpfFormatado() + ")";
    }
}
