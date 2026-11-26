package locadora.domain;

import java.io.Serializable;

/**
 * Generalização de filme. As regras de preço, prazo e multa variam conforme
 * o tipo e ficam nas subclasses (polimorfismo).
 */
public abstract class Filme implements Serializable {
    private static final long serialVersionUID = 1L;
    //1L seria 1 'long' o L é só uma convenção pelo jeito o java compila normal
    //Quando o RepositorioSerializado salva a Locadora,
    //o Java grava dentro do arquivo: “este Cliente é da versão 1”. Ao carregar, ele compara:
    //o número gravado no arquivo (1L) com o número que na classe agora q também é (1L) pra poder acessar

    private final int id;
    private String titulo;
    private int ano;
    private int copiasTotais;
    private int copiasDisponiveis;

    //o constructor
    protected Filme(int id, String titulo, int ano, int copiasTotais, int copiasDisponiveis) {
        this.id = id;
        this.titulo = Validador.textoObrigatorio(titulo, "Título");
        this.ano = Validador.inteiroEntre(ano, 1888, 2100, "Ano");
        this.copiasTotais = Validador.inteiroEntre(copiasTotais, 1, 1000, "Cópias");
        this.copiasDisponiveis = Validador.inteiroEntre(copiasDisponiveis, 0, copiasTotais, "Cópias disponíveis");
    }

    // ---- Comportamento que cada tipo de filme define ----
    public abstract TipoFilme tipo();
    public abstract double valorDiaria();
    public abstract int prazoDias();
    public abstract double multaPorDiaAtraso();

    // ---- Regras comuns ----
    public boolean estaDisponivel() {
        return copiasDisponiveis > 0;
    }

    public int copiasAlugadas() {
        return copiasTotais - copiasDisponiveis;
    }

    /** Edita os dados, preservando as cópias que estão alugadas neste momento. */
    public void atualizar(String novoTitulo, int novoAno, int novasCopiasTotais) {
        String t = Validador.textoObrigatorio(novoTitulo, "Título");
        int a = Validador.inteiroEntre(novoAno, 1888, 2100, "Ano");
        int c = Validador.inteiroEntre(novasCopiasTotais, 1, 1000, "Cópias");
        if (c < copiasAlugadas()) {
            throw new RegraNegocioException("Há " + copiasAlugadas() + " cópias alugadas; o total não pode ser menor.");
        }
        int alugadas = copiasAlugadas();
        this.titulo = t;
        this.ano = a;
        this.copiasTotais = c;
        this.copiasDisponiveis = c - alugadas;
    }

    // Visibilidade de pacote: só Cliente (mesmo pacote) movimenta o estoque.
    void retirarCopia() {
        if (!estaDisponivel()) {
            throw new RegraNegocioException("Não há cópias disponíveis de \"" + titulo + "\".");
        }
        copiasDisponiveis--;
    }

    void devolverCopia() {
        copiasDisponiveis++;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getAno() { return ano; }
    public int getCopiasTotais() { return copiasTotais; }
    public int getCopiasDisponiveis() { return copiasDisponiveis; }
}
