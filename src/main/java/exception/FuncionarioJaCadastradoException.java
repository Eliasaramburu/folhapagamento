package exception;

/**
 * Lançada ao cadastrar um funcionário cujo código (ou CPF) já está em uso.
 *
 * <p>É uma exceção verificada: quem chama é obrigado a tratá-la ou declará-la.</p>
 */
public class FuncionarioJaCadastradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public FuncionarioJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
