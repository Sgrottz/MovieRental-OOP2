package locadora.infrastructure;

import locadora.application.Configuracao;
import locadora.application.RepositorioLocadora;

/** Classe estática que escolhe a implementação de persistência conforme a configuração. */
public final class FabricaRepositorio {

    private FabricaRepositorio() {
    }

    public static RepositorioLocadora criar(Configuracao configuracao) {
        return switch (configuracao.formatoArquivo()) {
            case "serializado" -> new RepositorioSerializado(configuracao.caminhoArquivo());
            case "csv" -> new RepositorioCsv(configuracao.caminhoArquivo());
            default -> throw new IllegalArgumentException(
                    "Formato desconhecido: " + configuracao.formatoArquivo() + " (use serializado ou csv)");
        };
    }
}
