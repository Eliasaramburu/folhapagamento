package util;

/**
 * Utilitário para validação, normalização e formatação de CPF.
 */
public final class ValidadorCpf {

    private ValidadorCpf() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Remove tudo que não for dígito.
     *
     * @return somente os dígitos, ou {@code null} se o argumento for nulo
     */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("\\D", "");
    }

    /**
     * Informa se o CPF é válido. Aceita "00000000000" ou "000.000.000-00";
     * rejeita sequências repetidas e dígitos verificadores incorretos.
     */
    public static boolean isValido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String texto = cpf.trim();
        if (!texto.matches("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            return false;
        }
        String digitos = normalizar(texto);
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        int primeiro = calcularDigito(digitos, 9);
        int segundo = calcularDigito(digitos, 10);
        return primeiro == Character.getNumericValue(digitos.charAt(9))
                && segundo == Character.getNumericValue(digitos.charAt(10));
    }

    /**
     * Formata 11 dígitos no padrão 000.000.000-00.
     *
     * @throws IllegalArgumentException se não houver exatamente 11 dígitos
     */
    public static String formatar(String digitos) {
        if (digitos == null || !digitos.matches("\\d{11}")) {
            throw new IllegalArgumentException("O CPF deve ter exatamente 11 dígitos");
        }
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    /** Calcula um dígito verificador usando os primeiros {@code quantidade} dígitos. */
    private static int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
