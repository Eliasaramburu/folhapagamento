package model;

/**
 * Funcionário comissionado: recebe salário-base mais um percentual de
 * comissão sobre o total de suas vendas. Sofre desconto de tributos
 * (modelo didático: 13% da remuneração).
 */
public class FuncionarioComissionado extends Funcionario implements Tributavel {

    /** Alíquota de tributos aplicada à remuneração. */
    public static final double ALIQUOTA_TRIBUTOS = 0.13;

    private double percentualComissao; // de 0 a 100
    private double totalVendas;

    public FuncionarioComissionado(int codigo, String nome, String cpf, double salarioBase,
                                   double percentualComissao, double totalVendas) {
        super(codigo, nome, cpf, salarioBase);
        this.percentualComissao = validarPercentual(percentualComissao);
        this.totalVendas = validarVendas(totalVendas);
    }

    /** Comissão = total de vendas x percentual / 100. */
    public double calcularComissao() {
        return arredondar(totalVendas * percentualComissao / 100.0);
    }

    /** Remuneração = salário-base + comissão. */
    @Override
    public double calcularRemuneracao() {
        return arredondar(getSalarioBase() + calcularComissao());
    }

    /** Tributos = 13% da remuneração. */
    @Override
    public double calcularTributos() {
        return arredondar(calcularRemuneracao() * ALIQUOTA_TRIBUTOS);
    }

    @Override
    public String getTipo() {
        return "Comissionado";
    }

    @Override
    protected String detalhesEspecificos() {
        return "Percentual de comissão: " + formatarPercentual(percentualComissao) + "\n"
                + "Total de vendas: " + formatarMoeda(totalVendas) + "\n"
                + "Comissão: " + formatarMoeda(calcularComissao());
    }

    public double getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(double percentualComissao) {
        this.percentualComissao = validarPercentual(percentualComissao);
    }

    public double getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(double totalVendas) {
        this.totalVendas = validarVendas(totalVendas);
    }

    private static double validarPercentual(double percentualComissao) {
        return exigirPercentual(percentualComissao, "O percentual de comissão deve estar entre 0 e 100");
    }

    private static double validarVendas(double totalVendas) {
        return exigirNaoNegativo(totalVendas, "O total de vendas não pode ser negativo");
    }
}
