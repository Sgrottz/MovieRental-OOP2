package locadora.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Registro do aluguel de um filme por um cliente. Pertence ao {@link Cliente}. */
public final class Locacao implements Serializable {
    private static final long serialVersionUID = 1L;
    //1L seria 1 'long' o L é só uma convenção pelo jeito o java compila normal
    //Quando o RepositorioSerializado salva a Locadora,
    //o Java grava dentro do arquivo: “este Cliente é da versão 1”. Ao carregar, ele compara:
    //o número gravado no arquivo (1L) com o número que na classe agora q também é (1L) pra poder acessar

    private final Filme filme;
    private final LocalDate retirada;
    private final LocalDate prevista;
    private LocalDate devolucao; // null enquanto estiver em aberto

    /** Nova locação: a data prevista vem do prazo do tipo de filme. */
    Locacao(Filme filme, LocalDate retirada) {
        this(filme, retirada, retirada.plusDays(filme.prazoDias()), null);
    }

    /** Construtor completo, usado também para reconstruir locações lidas de arquivo. */
    public Locacao(Filme filme, LocalDate retirada, LocalDate prevista, LocalDate devolucao) {
        this.filme = filme;
        this.retirada = retirada;
        this.prevista = prevista;
        this.devolucao = devolucao;
    }

    public boolean emAberto() {
        return devolucao == null;
    }

    public boolean estaAtrasada(LocalDate hoje) {
        return emAberto() && hoje.isAfter(prevista);
    }

    /** Dias além do prazo. Se já devolvida, usa a data de devolução; senão, a data de referência. */
    public int diasAtraso(LocalDate referencia) {
        LocalDate fim = emAberto() ? referencia : devolucao;
        return (int) Math.max(0, ChronoUnit.DAYS.between(prevista, fim));
    }

    public double calcularMulta(LocalDate referencia) {
        return diasAtraso(referencia) * filme.multaPorDiaAtraso(); // polimorfismo
    }

    /** Diárias (mínimo 1) + multa. Para locação em aberto, é o valor acumulado até a referência. */
    public double calcularTotal(LocalDate referencia) {
        LocalDate fim = emAberto() ? referencia : devolucao;
        long dias = Math.max(1, ChronoUnit.DAYS.between(retirada, fim));
        return dias * filme.valorDiaria() + calcularMulta(referencia);
    }

    void registrarDevolucao(LocalDate data) {
        if (!emAberto()) {
            throw new RegraNegocioException("Esta locação já foi devolvida.");
        }
        if (data.isBefore(retirada)) {
            throw new RegraNegocioException("A devolução não pode ser anterior à retirada.");
        }
        this.devolucao = data;
    }

    public Filme getFilme() { return filme; }
    public LocalDate getRetirada() { return retirada; }
    public LocalDate getPrevista() { return prevista; }
    public LocalDate getDevolucao() { return devolucao; }
}
