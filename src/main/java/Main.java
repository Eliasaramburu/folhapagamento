import repository.FuncionarioRepository;
import service.FuncionarioService;
import view.FuncionarioView;

/**
 * Ponto de entrada: monta as camadas (repositório, serviço e visão) e inicia o menu.
 */
public class Main {

    public static void main(String[] args) {
        FuncionarioRepository repositorio = new FuncionarioRepository();
        FuncionarioService servico = new FuncionarioService(repositorio);
        FuncionarioView visao = new FuncionarioView(servico, System.in, System.out);
        visao.iniciar();
    }
}
