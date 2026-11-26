package locadora.application;

import locadora.domain.Locadora;

/**
 * Porta de persistência (definida na aplicação, implementada na infraestrutura).
 * Persiste a Locadora inteira para manter os vínculos Cliente -> Locação -> Filme.
 */
public interface RepositorioLocadora {
    void salvar(Locadora locadora);

    Locadora carregar();
}
