package locadora.infrastructure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import locadora.application.PersistenciaException;
import locadora.application.RepositorioLocadora;
import locadora.domain.Locadora;

/** Persistência por serialização de objetos Java (grafo inteiro em um único arquivo). */
class RepositorioSerializado implements RepositorioLocadora {
    private final Path arquivo;

    RepositorioSerializado(String caminho) {
        this.arquivo = Path.of(caminho);
    }

    @Override
    public void salvar(Locadora locadora) {
        try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(arquivo))) {
            saida.writeObject(locadora);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar em " + arquivo + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Locadora carregar() {
        if (!Files.exists(arquivo)) {
            throw new PersistenciaException("Arquivo não encontrado: " + arquivo);
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            return (Locadora) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("Erro ao carregar " + arquivo + ": " + e.getMessage(), e);
        }
    }
}
