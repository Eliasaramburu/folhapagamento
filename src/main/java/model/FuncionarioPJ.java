package model;

import exception.DadosInvalidosException;

/**
 * Prestador de serviço (pessoa jurídica): recebe um valor por projeto e a
 * quantidade de projetos realizados. Não possui salário-base (fica em zero)
 * e não é {@link Tributavel}, pois recolhe seus próprios impostos.
 */
public class FuncionarioPJ extends Funcionario {

    private double valorPorProjeto;
    private int quantidadeProjetos;

    public FuncionarioPJ(int codigo, String nome, String cpf, double valorPorProjeto, int quantidadeProjetos) {
        super(codigo, nome, cpf, 0.0);
        this.valorPorProjeto = validarValor(valorPorProjeto);
        this.quantidadeProjetos = validarQuantidade(quantidadeProjetos);
    }

    /** Remuneração = valor por projeto x quantidade de projetos. */
    @Override
    public double calcularRemuneracao() {
        return arredondar(valorPorProjeto * quantidadeProjetos);
    }

    @Override
    public String getTipo() {
        return "PJ";
    }

    @Override
    protected String detalhesEspecificos() {
        return "Valor por projeto: " + formatarMoeda(valorPorProjeto) + "\n"
                + "Projetos realizados: " + quantidadeProjetos;
    }

    public double getValorPorProjeto() {
        return valorPorProjeto;
    }

    public void setValorPorProjeto(double valorPorProjeto) {
        this.valorPorProjeto = validarValor(valorPorProjeto);
    }

    public int getQuantidadeProjetos() {
        return quantidadeProjetos;
    }

    public void setQuantidadeProjetos(int quantidadeProjetos) {
        this.quantidadeProjetos = validarQuantidade(quantidadeProjetos);
    }

    private static double validarValor(double valorPorProjeto) {
        return exigirNaoNegativo(valorPorProjeto, "O valor por projeto não pode ser negativo");
    }

    private static int validarQuantidade(int quantidadeProjetos) {
        if (quantidadeProjetos < 0) {
            throw new DadosInvalidosException("A quantidade de projetos não pode ser negativa");
        }
        return quantidadeProjetos;
    }
}
