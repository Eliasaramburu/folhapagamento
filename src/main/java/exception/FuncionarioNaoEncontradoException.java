package exception;

/**
 * Lançada ao buscar, consultar ou remover um funcionário que não existe.
 *
 * <p>É uma exceção verificada: quem chama é obrigado a tratá-la ou declará-la.
 * A camada de visão é a responsável por informar o usuário.</p>
 */
public class FuncionarioNaoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
