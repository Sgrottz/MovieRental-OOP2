package locadora.domain;

/** Tipos de filme existentes. Também funciona como fábrica simples de {@link Filme}. */
public enum TipoFilme {
    LANCAMENTO("Lançamento"),
    CATALOGO("Catálogo");

    private final String rotulo;

    TipoFilme(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    /** Cria a subclasse concreta correspondente ao tipo. */
    public Filme criar(int id, String titulo, int ano, int copiasTotais, int copiasDisponiveis) {
        return switch (this) {
            case LANCAMENTO -> new FilmeLancamento(id, titulo, ano, copiasTotais, copiasDisponiveis);
            case CATALOGO -> new FilmeCatalogo(id, titulo, ano, copiasTotais, copiasDisponiveis);
        };
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
