package locadora.domain;

import java.io.Serializable;
//serve pro java converter o objeto em uma sequência de bytes
//depois ele desserializa transformando a sequência de bytes no objeto de novo
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Cliente da locadora. Compõe suas próprias locações (lista). */
public final class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    //1L seria 1 'long' o L é só uma convenção pelo jeito o java compila normal
    //Quando o RepositorioSerializado salva a Locadora,
    //o Java grava dentro do arquivo: “este Cliente é da versão 1”. Ao carregar, ele compara:
    //o número gravado no arquivo (1L) com o número que na classe agora q também é (1L) pra poder acessar

    public static final int MAX_LOCACOES_ABERTAS = 3;

    private final int id;
    private String nome;
    private String cpf;
    private String telefone;
    private final ArrayList<Locacao> locacoes = new ArrayList<>();

    public Cliente(int id, String nome, String cpf, String telefone) {
        this.id = id;
        this.nome = Validador.textoObrigatorio(nome, "Nome");
        this.cpf = Validador.cpf(cpf);
        this.telefone = Validador.textoObrigatorio(telefone, "Telefone");
    }

    public void atualizar(String novoNome, String novoCpf, String novoTelefone) {
        // valida tudo antes de alterar para não deixar o objeto pela metade
        String n = Validador.textoObrigatorio(novoNome, "Nome");
        String c = Validador.cpf(novoCpf);
        String t = Validador.textoObrigatorio(novoTelefone, "Telefone");
        this.nome = n;
        this.cpf = c;
        this.telefone = t;
    }

    // Visibilidade de pacote: o fluxo público passa por Locadora.
    Locacao alugar(Filme filme, LocalDate data) {
        if (temLocacaoAtrasada(data)) {
            throw new RegraNegocioException("Cliente possui locação atrasada.");
        }
        if (locacoesEmAberto().size() >= MAX_LOCACOES_ABERTAS) {
            throw new RegraNegocioException("Limite de " + MAX_LOCACOES_ABERTAS + " locações em aberto atingido.");
        }
        if (localizarEmAberto(filme) != null) {
            throw new RegraNegocioException("Cliente já está com este filme.");
        }
        filme.retirarCopia(); // falha se não houver cópia, antes de alterar a lista
        Locacao locacao = new Locacao(filme, data);
        locacoes.add(locacao);
        return locacao;
    }

    Locacao devolver(Filme filme, LocalDate data) {
        Locacao locacao = localizarEmAberto(filme);
        if (locacao == null) {
            throw new RegraNegocioException("Não há locação em aberto deste filme para o cliente.");
        }
        locacao.registrarDevolucao(data); // valida a data antes de mexer no estoque
        filme.devolverCopia();
        return locacao;
    }

    public List<Locacao> locacoesEmAberto() {
        List<Locacao> abertas = new ArrayList<>();
        for (Locacao l : locacoes) {
            if (l.emAberto()) abertas.add(l);
        }
        return abertas;
    }

    public boolean temLocacaoAtrasada(LocalDate hoje) {
        return locacoes.stream().anyMatch(l -> l.estaAtrasada(hoje));
    }

    /** Soma das multas das locações ainda em aberto. */
    public double totalMultasPendentes(LocalDate referencia) {
        return locacoesEmAberto().stream().mapToDouble(l -> l.calcularMulta(referencia)).sum();
    }

    private Locacao localizarEmAberto(Filme filme) {
        for (Locacao l : locacoes) {
            if (l.emAberto() && l.getFilme() == filme) return l;
        }
        return null;
    }

    /** Usado apenas por repositórios ao reconstruir dados salvos (não aplica regras). */
    public void adicionarLocacaoRestaurada(Locacao locacao) {
        locacoes.add(locacao);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public List<Locacao> getLocacoes() { return Collections.unmodifiableList(locacoes); }
}
