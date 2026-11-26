package locadora.application;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Singleton com a configuração da aplicação, lida de "locadora.properties" (na pasta
 * de execução). Se o arquivo não existir, usa os valores padrão.
 * Chaves: interface = textual | gui ; formato = serializado | csv ; arquivo = caminho.
 */
public final class Configuracao {
    private static final Configuracao INSTANCIA = new Configuracao();

    private final Properties propriedades = new Properties();

    private Configuracao() {
        Path arquivo = Path.of("locadora.properties");
        if (Files.exists(arquivo)) {
            try (Reader leitor = Files.newBufferedReader(arquivo)) {
                propriedades.load(leitor);
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível ler locadora.properties", e);
            }
        }
    }

    public static Configuracao getInstancia() {
        return INSTANCIA;
    }

    public String tipoInterface() {
        return propriedades.getProperty("interface", "textual").trim().toLowerCase();
    }

    public String formatoArquivo() {
        return propriedades.getProperty("formato", "serializado").trim().toLowerCase();
    }

    public String caminhoArquivo() {
        String padrao = formatoArquivo().equals("csv") ? "locadora.csv" : "locadora.dat";
        return propriedades.getProperty("arquivo", padrao).trim();
    }
}
