package service;

import exception.DadosInvalidosException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import model.Funcionario;
import repository.FuncionarioRepository;
import util.ValidadorCpf;

/**
 * Regras de negócio do sistema: coordena o repositório e os cálculos da folha.
 */
public class FuncionarioService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final FuncionarioRepository repositorio;

    public FuncionarioService(FuncionarioRepository repositorio) {
        if (repositorio == null) {
            throw new DadosInvalidosException("O repositório é obrigatório");
        }
        this.repositorio = repositorio;
    }

    // ---------- cadastro, listagem e remoção ----------

    /**
     * @throws FuncionarioJaCadastradoException se o código ou o CPF já existirem
     * @throws DadosInvalidosException          se o funcionário for nulo
     */
    public void cadastrar(Funcionario funcionario) throws FuncionarioJaCadastradoException {
        repositorio.salvar(funcionario);
    }

    public List<Funcionario> listar() {
        return repositorio.listarTodos();
    }

    /** @throws FuncionarioNaoEncontradoException se não existir funcionário com o código */
    public void remover(int codigo) throws FuncionarioNaoEncontradoException {
        if (!repositorio.remover(codigo)) {
            throw naoEncontradoPorCodigo(codigo);
        }
    }

    // ---------- buscas (polimorfismo por sobrecarga) ----------

    /** Busca pelo código identificador. */
    public Funcionario buscarFuncionario(int codigo) throws FuncionarioNaoEncontradoException {
        return repositorio.buscarPorCodigo(codigo).orElseThrow(() -> naoEncontradoPorCodigo(codigo));
    }

    /** Busca pelo CPF, com ou sem formatação. */
    public Funcionario buscarFuncionario(String cpf) throws FuncionarioNaoEncontradoException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new DadosInvalidosException("O CPF é obrigatório");
        }
        return repositorio.buscarPorCpf(cpf).orElseThrow(() -> new FuncionarioNaoEncontradoException(
                "Funcionário com CPF " + formatarParaMensagem(cpf) + " não encontrado"));
    }

    /**
     * Busca pelo nome, ignorando maiúsculas e minúsculas.
     *
     * @param buscaExata {@code true} para nome idêntico; {@code false} para nomes que contêm o texto
     * @throws FuncionarioNaoEncontradoException se ninguém corresponder
     */
    public List<Funcionario> buscarFuncionario(String nome, boolean buscaExata)
            throws FuncionarioNaoEncontradoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DadosInvalidosException("O nome é obrigatório");
        }
        String procurado = nome.trim().toLowerCase(PT_BR);
        List<Funcionario> encontrados = repositorio.listarTodos().stream()
                .filter(funcionario -> {
                    String atual = funcionario.getNome().toLowerCase(PT_BR);
                    return buscaExata ? atual.equals(procurado) : atual.contains(procurado);
                })
                .collect(Collectors.toList());
        if (encontrados.isEmpty()) {
            throw new FuncionarioNaoEncontradoException("Nenhum funcionário encontrado com o nome \"" + nome.trim() + "\"");
        }
        return encontrados;
    }

    // ---------- remuneração, dados, relatório e folha ----------

    public double calcularRemuneracao(int codigo) throws FuncionarioNaoEncontradoException {
        return buscarFuncionario(codigo).calcularRemuneracao();
    }

    public String exibirDados(int codigo) throws FuncionarioNaoEncontradoException {
        return buscarFuncionario(codigo).exibirDados();
    }

    /** Soma da remuneração bruta de todos os funcionários. */
    public double calcularTotalFolha() {
        double total = 0.0;
        for (Funcionario funcionario : repositorio.listarTodos()) {
            total += funcionario.calcularRemuneracao();
        }
        return Funcionario.arredondar(total);
    }

    /** Relatório com todos os funcionários, suas remunerações e o total da folha. */
    public String gerarRelatorio() {
        StringBuilder texto = new StringBuilder();
        texto.append("========== RELATÓRIO DE FUNCIONÁRIOS ==========\n");
        List<Funcionario> funcionarios = repositorio.listarTodos();
        if (funcionarios.isEmpty()) {
            texto.append("Nenhum funcionário cadastrado.\n");
        } else {
            texto.append(String.format(PT_BR, "%-5s %-13s %-24s %-15s %16s%n",
                    "Cód.", "Tipo", "Nome", "CPF", "Remuneração"));
            for (Funcionario funcionario : funcionarios) {
                texto.append(String.format(PT_BR, "%-5d %-13s %-24s %-15s %16s%n",
                        funcionario.getCodigo(),
                        funcionario.getTipo(),
                        funcionario.getNome(),
                        funcionario.getCpfFormatado(),
                        Funcionario.formatarMoeda(funcionario.calcularRemuneracao())));
            }
        }
        texto.append("Total da folha de pagamento: ").append(Funcionario.formatarMoeda(calcularTotalFolha()));
        return texto.toString();
    }

    // ---------- auxiliares ----------

    private static FuncionarioNaoEncontradoException naoEncontradoPorCodigo(int codigo) {
        return new FuncionarioNaoEncontradoException("Funcionário com código " + codigo + " não encontrado");
    }

    private static String formatarParaMensagem(String cpf) {
        String digitos = ValidadorCpf.normalizar(cpf);
        return digitos.length() == 11 ? ValidadorCpf.formatar(digitos) : cpf.trim();
    }
}
