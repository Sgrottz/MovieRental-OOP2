package locadora.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Raiz do agregado: guarda clientes e filmes, aplica as regras do CRUD e
 * coordena aluguel/devolução. Não faz nenhuma entrada/saída com o usuário.
 * Serializar a Locadora salva todo o grafo (clientes, locações e filmes) de uma vez,
 * mantendo as referências compartilhadas entre locações e filmes.
 */
public final class Locadora implements Serializable {
    private static final long serialVersionUID = 1L;
    //1L seria 1 'long' o L é só uma convenção pelo jeito o java compila normal
    //Quando o RepositorioSerializado salva a Locadora,
    //o Java grava dentro do arquivo: “este Cliente é da versão 1”. Ao carregar, ele compara:
    //o número gravado no arquivo (1L) com o número que na classe agora q também é (1L) pra poder acessar

    private final ArrayList<Cliente> clientes = new ArrayList<>();
    private final ArrayList<Filme> filmes = new ArrayList<>();
    private int proximoIdCliente = 1;
    private int proximoIdFilme = 1;

    // ===================== Clientes =====================

    public Cliente cadastrarCliente(String nome, String cpf, String telefone) {
        garantirCpfUnico(Validador.cpf(cpf), null);
        Cliente cliente = new Cliente(proximoIdCliente, nome, cpf, telefone);
        clientes.add(cliente);
        proximoIdCliente++;
        return cliente;
    }

    public void atualizarCliente(int id, String nome, String cpf, String telefone) {
        Cliente cliente = buscarCliente(id);
        garantirCpfUnico(Validador.cpf(cpf), cliente);
        cliente.atualizar(nome, cpf, telefone);
    }

    public void excluirCliente(int id) {
        Cliente cliente = buscarCliente(id);
        if (!cliente.locacoesEmAberto().isEmpty()) {
            throw new RegraNegocioException("Cliente possui locações em aberto e não pode ser excluído.");
        }
        clientes.remove(cliente);
    }

    public Cliente buscarCliente(int id) {
        return clientes.stream().filter(c -> c.getId() == id).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Cliente " + id + " não encontrado."));
    }

    public List<Cliente> listarClientes() {
        return Collections.unmodifiableList(clientes);
    }

    private void garantirCpfUnico(String cpf, Cliente ignorar) {
        for (Cliente c : clientes) {
            if (c != ignorar && c.getCpf().equals(cpf)) {
                throw new RegraNegocioException("Já existe cliente com este CPF.");
            }
        }
    }

    // ===================== Filmes =====================

    public Filme cadastrarFilme(TipoFilme tipo, String titulo, int ano, int copias) {
        Filme filme = tipo.criar(proximoIdFilme, titulo, ano, copias, copias);
        filmes.add(filme);
        proximoIdFilme++;
        return filme;
    }

    public void atualizarFilme(int id, String titulo, int ano, int copias) {
        buscarFilme(id).atualizar(titulo, ano, copias);
    }

    public void excluirFilme(int id) {
        Filme filme = buscarFilme(id);
        boolean temHistorico = clientes.stream()
                .flatMap(c -> c.getLocacoes().stream())
                //flatMap junta várias coleções em uma só sequência
                // resultado vai para temHistorico: true se algum cliente já teve esse filme, aberto ou devolvido.
                .anyMatch(l -> l.getFilme() == filme);
        if (temHistorico) {
            throw new RegraNegocioException("Filme possui histórico de locações e não pode ser excluído.");
        }
        filmes.remove(filme);
    }

    public Filme buscarFilme(int id) {
        return filmes.stream().filter(f -> f.getId() == id).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Filme " + id + " não encontrado."));
    }

    public List<Filme> listarFilmes() {
        return Collections.unmodifiableList(filmes);
    }

    // ===================== Locações =====================

    public Locacao alugar(int clienteId, int filmeId, LocalDate data) {
        return buscarCliente(clienteId).alugar(buscarFilme(filmeId), data);
    }

    public Locacao devolver(int clienteId, int filmeId, LocalDate data) {
        return buscarCliente(clienteId).devolver(buscarFilme(filmeId), data);
    }

    // ===================== Restauração (repositórios) =====================
    /** Reinsere um cliente lido de arquivo, mantendo o contador de IDs coerente. */
    public void restaurarCliente(Cliente cliente) {
        clientes.add(cliente);
        proximoIdCliente = Math.max(proximoIdCliente, cliente.getId() + 1);
        //devolve o maior dos dois e aqui ele só deixa o contador subir (por conta do max)
    }


    public void restaurarFilme(Filme filme) {
        filmes.add(filme);
        proximoIdFilme = Math.max(proximoIdFilme, filme.getId() + 1);
        //devolve o maior dos dois e aqui ele só deixa o contador subir (por conta do max)
    }
}
