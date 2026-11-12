package repository;

import exception.DadosInvalidosException;
import exception.FuncionarioJaCadastradoException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Funcionario;
import util.ValidadorCpf;

/**
 * Armazena e recupera funcionários em memória.
 *
 * <p>Usa um {@link LinkedHashMap} indexado pelo código, o que dá busca direta
 * por código e preserva a ordem de cadastro na listagem.</p>
 */
public class FuncionarioRepository {

    private final Map<Integer, Funcionario> funcionarios = new LinkedHashMap<>();

    /**
     * Guarda o funcionário.
     *
     * @throws DadosInvalidosException           se o funcionário for nulo
     * @throws FuncionarioJaCadastradoException  se o código ou o CPF já estiverem em uso
     */
    public void salvar(Funcionario funcionario) throws FuncionarioJaCadastradoException {
        if (funcionario == null) {
            throw new DadosInvalidosException("O funcionário é obrigatório");
        }
        if (existePorCodigo(funcionario.getCodigo())) {
            throw new FuncionarioJaCadastradoException(
                    "Já existe um funcionário com o código " + funcionario.getCodigo());
        }
        if (buscarPorCpf(funcionario.getCpf()).isPresent()) {
            throw new FuncionarioJaCadastradoException(
                    "Já existe um funcionário com o CPF " + funcionario.getCpfFormatado());
        }
        funcionarios.put(funcionario.getCodigo(), funcionario);
    }

    public Optional<Funcionario> buscarPorCodigo(int codigo) {
        return Optional.ofNullable(funcionarios.get(codigo));
    }

    /** Busca pelo CPF, com ou sem formatação. */
    public Optional<Funcionario> buscarPorCpf(String cpf) {
        String digitos = ValidadorCpf.normalizar(cpf);
        if (digitos == null || digitos.isEmpty()) {
            return Optional.empty();
        }
        return funcionarios.values().stream()
                .filter(funcionario -> funcionario.getCpf().equals(digitos))
                .findFirst();
    }

    /** @return cópia imutável dos funcionários, na ordem de cadastro. */
    public List<Funcionario> listarTodos() {
        return List.copyOf(funcionarios.values());
    }

    /** @return {@code true} se havia um funcionário com esse código e ele foi removido. */
    public boolean remover(int codigo) {
        return funcionarios.remove(codigo) != null;
    }

    public boolean existePorCodigo(int codigo) {
        return funcionarios.containsKey(codigo);
    }

    public int quantidade() {
        return funcionarios.size();
    }
}
