package model;

/**
 * Contrato de quem tem tributos ou descontos incidentes sobre a remuneração.
 */
public interface Tributavel {

    /** @return o valor total, em reais, dos tributos/descontos aplicáveis. */
    double calcularTributos();
}
