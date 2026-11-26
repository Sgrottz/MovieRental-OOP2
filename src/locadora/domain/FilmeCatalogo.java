package locadora.domain;

/** Filme de catálogo: diária barata, prazo maior e multa menor. */
public class FilmeCatalogo extends Filme {
    private static final long serialVersionUID = 1L;
    //1L seria 1 'long' o L é só uma convenção pelo jeito o java compila normal
    //Quando o RepositorioSerializado salva a Locadora,
    //o Java grava dentro do arquivo: “este Cliente é da versão 1”. Ao carregar, ele compara:
    //o número gravado no arquivo (1L) com o número que na classe agora q também é (1L) pra poder acessar

    public FilmeCatalogo(int id, String titulo, int ano, int copiasTotais, int copiasDisponiveis) {
        super(id, titulo, ano, copiasTotais, copiasDisponiveis);
    }

    @Override public TipoFilme tipo() { return TipoFilme.CATALOGO; }
    @Override public double valorDiaria() { return 4.00; }
    @Override public int prazoDias() { return 5; }
    @Override public double multaPorDiaAtraso() { return 2.00; }
}
