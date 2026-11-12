package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.DadosInvalidosException;
import exception.FuncionarioJaCadastradoException;
import java.util.List;
import java.util.Optional;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FuncionarioRepositoryTest {

    private FuncionarioRepository repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new FuncionarioRepository();
    }

    private Funcionario ana() {
        return new FuncionarioCLT(1, "Ana Lima", "529.982.247-25", 3000.0, 500.0);
    }

    private Funcionario bruno() {
        return new FuncionarioPJ(2, "Bruno Reis", "111.444.777-35", 2500.0, 3);
    }

    @Test
    @DisplayName("Começa vazio")
    void comecaVazio() {
        assertTrue(repositorio.listarTodos().isEmpty());
        assertEquals(0, repositorio.quantidade());
    }

    @Test
    @DisplayName("Salva e recupera o funcionário pelo código")
    void salvaERecuperaPorCodigo() throws Exception {
        Funcionario ana = ana();
        repositorio.salvar(ana);

        Optional<Funcionario> encontrado = repositorio.buscarPorCodigo(1);
        assertTrue(encontrado.isPresent());
        assertSame(ana, encontrado.get());
        assertEquals(1, repositorio.quantidade());
    }

    @Test
    @DisplayName("Busca por código inexistente devolve vazio")
    void buscaPorCodigoInexistente() {
        assertFalse(repositorio.buscarPorCodigo(99).isPresent());
    }

    @Test
    @DisplayName("Recupera pelo CPF, com ou sem formatação")
    void recuperaPorCpf() throws Exception {
        Funcionario ana = ana();
        repositorio.salvar(ana);

        assertSame(ana, repositorio.buscarPorCpf("529.982.247-25").get());
        assertSame(ana, repositorio.buscarPorCpf("52998224725").get());
    }

    @Test
    @DisplayName("Busca por CPF inexistente ou nulo devolve vazio")
    void buscaPorCpfInexistente() throws Exception {
        repositorio.salvar(ana());
        assertFalse(repositorio.buscarPorCpf("11144477735").isPresent());
        assertFalse(repositorio.buscarPorCpf(null).isPresent());
    }

    @Test
    @DisplayName("Rejeita código já cadastrado e não altera o armazenamento")
    void rejeitaCodigoDuplicado() throws Exception {
        repositorio.salvar(ana());
        Funcionario outro = new FuncionarioCLT(1, "Outra Pessoa", "111.444.777-35", 2000.0, 0.0);

        assertThrows(FuncionarioJaCadastradoException.class, () -> repositorio.salvar(outro));
        assertEquals(1, repositorio.quantidade());
        assertEquals("Ana Lima", repositorio.buscarPorCodigo(1).get().getNome());
    }

    @Test
    @DisplayName("Rejeita CPF já cadastrado em outro código")
    void rejeitaCpfDuplicado() throws Exception {
        repositorio.salvar(ana());
        Funcionario mesmoCpf = new FuncionarioCLT(2, "Outra Pessoa", "52998224725", 2000.0, 0.0);

        assertThrows(FuncionarioJaCadastradoException.class, () -> repositorio.salvar(mesmoCpf));
        assertEquals(1, repositorio.quantidade());
    }

    @Test
    @DisplayName("Rejeita funcionário nulo")
    void rejeitaNulo() {
        assertThrows(DadosInvalidosException.class, () -> repositorio.salvar(null));
    }

    @Test
    @DisplayName("Lista os funcionários na ordem de cadastro")
    void listaNaOrdemDeCadastro() throws Exception {
        Funcionario bruno = bruno();
        Funcionario ana = ana();
        repositorio.salvar(bruno);
        repositorio.salvar(ana);

        List<Funcionario> lista = repositorio.listarTodos();
        assertEquals(2, lista.size());
        assertSame(bruno, lista.get(0));
        assertSame(ana, lista.get(1));
    }

    @Test
    @DisplayName("A lista devolvida é uma cópia imutável: não expõe o armazenamento interno")
    void listaEhImutavelECopia() throws Exception {
        repositorio.salvar(ana());
        List<Funcionario> lista = repositorio.listarTodos();

        assertThrows(UnsupportedOperationException.class, () -> lista.add(bruno()));
        repositorio.salvar(bruno());
        assertEquals(1, lista.size());
        assertEquals(2, repositorio.listarTodos().size());
    }

    @Test
    @DisplayName("Remove funcionário existente")
    void removeExistente() throws Exception {
        repositorio.salvar(ana());
        assertTrue(repositorio.remover(1));
        assertFalse(repositorio.buscarPorCodigo(1).isPresent());
        assertEquals(0, repositorio.quantidade());
    }

    @Test
    @DisplayName("Remover inexistente devolve false")
    void removeInexistente() {
        assertFalse(repositorio.remover(99));
    }

    @Test
    @DisplayName("Após remover, o código e o CPF podem ser reutilizados")
    void reutilizaCodigoAposRemover() throws Exception {
        repositorio.salvar(ana());
        repositorio.remover(1);
        repositorio.salvar(ana());
        assertEquals(1, repositorio.quantidade());
    }

    @Test
    @DisplayName("existePorCodigo informa se o código está cadastrado")
    void existePorCodigo() throws Exception {
        assertFalse(repositorio.existePorCodigo(1));
        repositorio.salvar(ana());
        assertTrue(repositorio.existePorCodigo(1));
    }
}
