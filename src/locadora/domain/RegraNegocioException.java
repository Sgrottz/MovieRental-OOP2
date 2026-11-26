package locadora.domain;

/**
 * Exceção lançada quando uma regra de negócio é violada
 * (ex.: cliente com locação atrasada tentando alugar de novo).
 * É não verificada para não poluir as assinaturas; a camada de
 * apresentação a captura e mostra a mensagem ao usuário.
 */
public class RegraNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    //1L seria 1 'long' o L é só uma convenção pelo jeito o java compila normal
    //Quando o RepositorioSerializado salva a Locadora,
    //o Java grava dentro do arquivo: “este Cliente é da versão 1”. Ao carregar, ele compara:
    //o número gravado no arquivo (1L) com o número que na classe agora q também é (1L) pra poder acessar

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
