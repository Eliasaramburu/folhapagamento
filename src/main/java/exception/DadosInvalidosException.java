package exception;

/**
 * Lançada quando um dado obrigatório não é informado ou é inválido: CPF
 * inválido, valores negativos, percentuais fora do intervalo, textos vazios etc.
 *
 * <p>É uma exceção não verificada (estende {@link IllegalArgumentException}),
 * pois indica argumento incorreto passado pelo chamador.</p>
 */
public class DadosInvalidosException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
