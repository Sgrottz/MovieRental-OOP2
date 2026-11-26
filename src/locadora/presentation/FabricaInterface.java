package locadora.presentation;

import locadora.application.Configuracao;
import locadora.application.ServicoLocadora;
import locadora.presentation.gui.FabricaGui;
import locadora.presentation.textual.FabricaTextual;

/**
 * Fábrica Abstrata: cria a família completa de telas de um mesmo estilo
 * (todas textuais ou todas gráficas). O Main só conhece esta interface.
 */
public interface FabricaInterface {
    Tela criarMenuPrincipal(ServicoLocadora servico);

    Tela criarTelaCliente(ServicoLocadora servico);

    Tela criarTelaFilme(ServicoLocadora servico);

    Tela criarTelaLocacao(ServicoLocadora servico);

    /** Escolhe a fábrica concreta conforme a configuração (interface=textual|gui). */
    static FabricaInterface daConfiguracao(Configuracao configuracao) {
        return switch (configuracao.tipoInterface()) {
            case "textual" -> new FabricaTextual();
            case "gui" -> new FabricaGui();
            default -> throw new IllegalArgumentException(
                    "Interface desconhecida: " + configuracao.tipoInterface() + " (use textual ou gui)");
        };
    }
}
