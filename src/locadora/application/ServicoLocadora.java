package locadora.application;

import java.time.LocalDate;
import java.util.List;

import locadora.domain.Cliente;
import locadora.domain.Filme;
import locadora.domain.Locacao;
import locadora.domain.Locadora;
import locadora.domain.TipoFilme;

/**
 * Casos de uso da locadora. É a única porta de entrada da apresentação:
 * orquestra o domínio e a persistência (via interface, sem conhecer o formato).
 */
public class ServicoLocadora {
    private final RepositorioLocadora repositorio;
    private Locadora locadora = new Locadora();

    public ServicoLocadora(RepositorioLocadora repositorio) {
        this.repositorio = repositorio;
    }

    public LocalDate hoje() {
        return LocalDate.now();
    }

    // ---- Clientes ----
    public Cliente cadastrarCliente(String nome, String cpf, String telefone) {
        return locadora.cadastrarCliente(nome, cpf, telefone);
    }

    public void atualizarCliente(int id, String nome, String cpf, String telefone) {
        locadora.atualizarCliente(id, nome, cpf, telefone);
    }

    public void excluirCliente(int id) {
        locadora.excluirCliente(id);
    }

    public Cliente buscarCliente(int id) {
        return locadora.buscarCliente(id);
    }

    public List<Cliente> listarClientes() {
        return locadora.listarClientes();
    }

    // ---- Filmes ----
    public Filme cadastrarFilme(TipoFilme tipo, String titulo, int ano, int copias) {
        return locadora.cadastrarFilme(tipo, titulo, ano, copias);
    }

    public void atualizarFilme(int id, String titulo, int ano, int copias) {
        locadora.atualizarFilme(id, titulo, ano, copias);
    }

    public void excluirFilme(int id) {
        locadora.excluirFilme(id);
    }

    public Filme buscarFilme(int id) {
        return locadora.buscarFilme(id);
    }

    public List<Filme> listarFilmes() {
        return locadora.listarFilmes();
    }

    // ---- Locações (data nula = hoje) ----
    public Locacao alugar(int clienteId, int filmeId, LocalDate data) {
        return locadora.alugar(clienteId, filmeId, data != null ? data : hoje());
    }

    public Locacao devolver(int clienteId, int filmeId, LocalDate data) {
        return locadora.devolver(clienteId, filmeId, data != null ? data : hoje());
    }

    // ---- Persistência ----
    public void salvar() {
        repositorio.salvar(locadora);
    }

    public void carregar() {
        locadora = repositorio.carregar();
    }
}
