import locadora.application.Configuracao;
import locadora.application.ServicoLocadora;
import locadora.infrastructure.FabricaRepositorio;
import locadora.presentation.FabricaInterface;

/**
 * Ponto de entrada. Apenas monta as peças (repositório, serviço, fábrica de interface)
 * e abre o menu. Trocar entre interface textual e gráfica é feito em locadora.properties,
 * sem alterar este código.
 */
public class Main {
    public static void main(String[] args) {
        Configuracao configuracao = Configuracao.getInstancia();
        ServicoLocadora servico = new ServicoLocadora(FabricaRepositorio.criar(configuracao));
        FabricaInterface fabrica = FabricaInterface.daConfiguracao(configuracao);
        fabrica.criarMenuPrincipal(servico).exibir();
    }
}
