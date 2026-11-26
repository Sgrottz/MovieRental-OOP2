package locadora.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import locadora.application.PersistenciaException;
import locadora.application.RepositorioLocadora;
import locadora.domain.Cliente;
import locadora.domain.Filme;
import locadora.domain.Locacao;
import locadora.domain.Locadora;
import locadora.domain.RegraNegocioException;
import locadora.domain.TipoFilme;

/**
 * Persistência em texto (CSV com ";"). Cada linha começa com o tipo do registro:
 *   FILME;id;tipo;titulo;ano;copiasTotais;copiasDisponiveis
 *   CLIENTE;id;nome;cpf;telefone
 *   LOCACAO;clienteId;filmeId;retirada;prevista;devolucao   (devolucao vazia = em aberto)
 * As locações guardam apenas os IDs; os vínculos são refeitos ao carregar.
 */
class RepositorioCsv implements RepositorioLocadora {
    private static final String SEP = ";";
    private final Path arquivo;

    RepositorioCsv(String caminho) {
        this.arquivo = Path.of(caminho);
    }

    @Override
    public void salvar(Locadora locadora) {

        //O repositório percorre a Locadora e escreve uma linha de texto por registro,
        // com campos separados por ; A primeira coluna diz o tipo da linha

        List<String> linhas = new ArrayList<>();
        for (Filme f : locadora.listarFilmes()) {
            linhas.add(String.join(SEP, "FILME", String.valueOf(f.getId()), f.tipo().name(),
                    limpar(f.getTitulo()), String.valueOf(f.getAno()),
                    String.valueOf(f.getCopiasTotais()), String.valueOf(f.getCopiasDisponiveis())));
        }
        for (Cliente c : locadora.listarClientes()) {
            linhas.add(String.join(SEP, "CLIENTE", String.valueOf(c.getId()),
                    limpar(c.getNome()), c.getCpf(), limpar(c.getTelefone())));
        }
        for (Cliente c : locadora.listarClientes()) {
            for (Locacao l : c.getLocacoes()) {
                linhas.add(String.join(SEP, "LOCACAO", String.valueOf(c.getId()),
                        String.valueOf(l.getFilme().getId()), l.getRetirada().toString(),
                        l.getPrevista().toString(),
                        l.getDevolucao() == null ? "" : l.getDevolucao().toString()));
            }
        }
        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar em " + arquivo + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Locadora carregar() {
        //Lê todas as linhas e cria uma Locadora vazia
        //Para cada linha, separa pelo ; e olha a primeira coluna (FILME, CLIENTE ou LOCACAO)
        //Reconstrói o objeto correspondente e o insere na Locadora
        //Nas linhas LOCACAO, usa os IDs para achar o cliente e o filme já carregados e refaz a conexao
        if (!Files.exists(arquivo)) {
            throw new PersistenciaException("Arquivo não encontrado: " + arquivo);
        }
        List<String> linhas;
        try {
            linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler " + arquivo + ": " + e.getMessage(), e);
        }
        Locadora locadora = new Locadora();
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).isBlank()) continue;
            try {
                interpretar(locadora, linhas.get(i).split(SEP, -1));
            } catch (NumberFormatException | DateTimeParseException
                     | ArrayIndexOutOfBoundsException | RegraNegocioException e) {
                throw new PersistenciaException("Arquivo inválido na linha " + (i + 1) + ".", e);
            }
        }
        return locadora;
    }

    private void interpretar(Locadora locadora, String[] p) {
        switch (p[0]) {
            case "FILME" -> locadora.restaurarFilme(TipoFilme.valueOf(p[2]).criar(
                    Integer.parseInt(p[1]), p[3], Integer.parseInt(p[4]),
                    Integer.parseInt(p[5]), Integer.parseInt(p[6])));
            case "CLIENTE" -> locadora.restaurarCliente(
                    new Cliente(Integer.parseInt(p[1]), p[2], p[3], p[4]));
            case "LOCACAO" -> {
                Cliente cliente = locadora.buscarCliente(Integer.parseInt(p[1]));
                Filme filme = locadora.buscarFilme(Integer.parseInt(p[2]));
                LocalDate devolucao = p[5].isEmpty() ? null : LocalDate.parse(p[5]);
                cliente.adicionarLocacaoRestaurada(
                        new Locacao(filme, LocalDate.parse(p[3]), LocalDate.parse(p[4]), devolucao));
            }
            default -> throw new RegraNegocioException("Registro desconhecido: " + p[0]);
        }
    }

    /** Evita que ";" ou quebras de linha digitados pelo usuário corrompam o arquivo. */
    private String limpar(String texto) {
        return texto.replace(SEP, ",").replace("\n", " ").replace("\r", " ");
    }
}
