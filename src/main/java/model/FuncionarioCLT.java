package model;

/**
 * Funcionário contratado pela CLT: recebe salário-base e adicional de benefícios.
 * Sofre desconto de tributos (modelo didático: 11% da remuneração).
 */
public class FuncionarioCLT extends Funcionario implements Tributavel {

    /** Alíquota de tributos aplicada à remuneração. */
    public static final double ALIQUOTA_TRIBUTOS = 0.11;

    private double adicionalBeneficios;

    public FuncionarioCLT(int codigo, String nome, String cpf, double salarioBase, double adicionalBeneficios) {
        super(codigo, nome, cpf, salarioBase);
        this.adicionalBeneficios = validarAdicional(adicionalBeneficios);
    }

    /** Remuneração = salário-base + adicional de benefícios. */
    @Override
    public double calcularRemuneracao() {
        return arredondar(getSalarioBase() + adicionalBeneficios);
    }

    /** Tributos = 11% da remuneração. */
    @Override
    public double calcularTributos() {
        return arredondar(calcularRemuneracao() * ALIQUOTA_TRIBUTOS);
    }

    @Override
    public String getTipo() {
        return "CLT";
    }

    @Override
    protected String detalhesEspecificos() {
        return "Adicional de benefícios: " + formatarMoeda(adicionalBeneficios);
    }

    public double getAdicionalBeneficios() {
        return adicionalBeneficios;
    }

    public void setAdicionalBeneficios(double adicionalBeneficios) {
        this.adicionalBeneficios = validarAdicional(adicionalBeneficios);
    }

    private static double validarAdicional(double adicionalBeneficios) {
        return exigirNaoNegativo(adicionalBeneficios, "O adicional de benefícios não pode ser negativo");
    }
}
