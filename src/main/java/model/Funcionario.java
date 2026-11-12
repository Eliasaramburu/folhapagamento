package model;

import exception.DadosInvalidosException;
import java.util.Locale;
import util.ValidadorCpf;

/**
 * Classe abstrata base de todos os funcionários.
 *
 * <p>Todos os atributos são privados; as alterações passam por setters que
 * validam os dados. Cada subclasse define a sua regra de remuneração em
 * {@link #calcularRemuneracao()} (polimorfismo por sobreposição).</p>
 */
public abstract class Funcionario implements Identificavel {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final int codigo;
    private String nome;
    private String cpf; // guardado somente com os 11 dígitos
    private double salarioBase;

    /**
     * @throws DadosInvalidosException se algum dado for obrigatório e não
     *                                 informado, ou inválido
     */
    protected Funcionario(int codigo, String nome, String cpf, double salarioBase) {
        if (codigo <= 0) {
            throw new DadosInvalidosException("O código deve ser um número inteiro positivo");
        }
        this.codigo = codigo;
        this.nome = validarNome(nome);
        this.cpf = validarCpf(cpf);
        this.salarioBase = validarSalarioBase(salarioBase);
    }

    // ---------- regra de negócio específica de cada categoria ----------

    /** Calcula a remuneração bruta, conforme a categoria do funcionário. */
    public abstract double calcularRemuneracao();

    /** @return o nome da categoria (ex.: "CLT"). */
    public abstract String getTipo();

    /** @return linhas com os dados específicos da categoria, para {@link #exibirDados()}. */
    protected abstract String detalhesEspecificos();

    // ---------- comportamento comum ----------

    /**
     * Remuneração menos os tributos, quando o funcionário for {@link Tributavel}.
     */
    public double calcularSalarioLiquido() {
        double tributos = (this instanceof Tributavel tributavel) ? tributavel.calcularTributos() : 0.0;
        return arredondar(calcularRemuneracao() - tributos);
    }

    /** Monta um texto com os dados completos do funcionário. */
    public String exibirDados() {
        StringBuilder texto = new StringBuilder();
        texto.append("Código: ").append(codigo).append('\n');
        texto.append("Tipo: ").append(getTipo()).append('\n');
        texto.append("Nome: ").append(nome).append('\n');
        texto.append("CPF: ").append(getCpfFormatado()).append('\n');
        texto.append("Salário-base: ").append(formatarMoeda(salarioBase)).append('\n');
        String detalhes = detalhesEspecificos();
        if (detalhes != null && !detalhes.isEmpty()) {
            texto.append(detalhes).append('\n');
        }
        texto.append("Remuneração: ").append(formatarMoeda(calcularRemuneracao())).append('\n');
        if (this instanceof Tributavel tributavel) {
            texto.append("Tributos: ").append(formatarMoeda(tributavel.calcularTributos())).append('\n');
        }
        texto.append("Salário líquido: ").append(formatarMoeda(calcularSalarioLiquido()));
        return texto.toString();
    }

    /** Arredonda para duas casas decimais. */
    public static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Formata em reais no padrão brasileiro, ex.: {@code R$ 1.234,50}. */
    public static String formatarMoeda(double valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }

    /** Formata um percentual com duas casas, ex.: {@code 5,00%}. */
    protected static String formatarPercentual(double percentual) {
        return String.format(PT_BR, "%.2f%%", percentual);
    }

    // ---------- getters e setters ----------

    @Override
    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = validarNome(nome);
    }

    /** @return o CPF somente com dígitos. */
    public String getCpf() {
        return cpf;
    }

    /** @return o CPF no padrão 000.000.000-00. */
    public String getCpfFormatado() {
        return ValidadorCpf.formatar(cpf);
    }

    public void setCpf(String cpf) {
        this.cpf = validarCpf(cpf);
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = validarSalarioBase(salarioBase);
    }

    // ---------- validações ----------

    private static String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DadosInvalidosException("O nome é obrigatório");
        }
        return nome.trim();
    }

    private static String validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new DadosInvalidosException("O CPF é obrigatório");
        }
        if (!ValidadorCpf.isValido(cpf)) {
            throw new DadosInvalidosException("CPF inválido: " + cpf);
        }
        return ValidadorCpf.normalizar(cpf);
    }

    private static double validarSalarioBase(double salarioBase) {
        return exigirNaoNegativo(salarioBase, "O salário-base não pode ser negativo");
    }

    /**
     * Garante que o número é finito e não negativo; caso contrário lança
     * {@link DadosInvalidosException} com a mensagem informada.
     */
    protected static double exigirNaoNegativo(double valor, String mensagem) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new DadosInvalidosException(mensagem);
        }
        return valor;
    }

    /** Garante que o percentual está entre 0 e 100 (inclusive). */
    protected static double exigirPercentual(double valor, String mensagem) {
        if (Double.isNaN(valor) || valor < 0 || valor > 100) {
            throw new DadosInvalidosException(mensagem);
        }
        return valor;
    }
}
