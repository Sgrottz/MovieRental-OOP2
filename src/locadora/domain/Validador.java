package locadora.domain;

/** Classe estática (utilitária) com validações reutilizadas pelas entidades. */
public final class Validador {

    private Validador() {
        // classe estática: não pode ser instanciada
    }

    /** Exige texto não vazio e devolve o texto sem espaços nas pontas. */
    public static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    /** Exige que o inteiro esteja no intervalo [min, max]. */
    public static int inteiroEntre(int valor, int min, int max, String campo) {
        if (valor < min || valor > max) {
            throw new RegraNegocioException(campo + " deve estar entre " + min + " e " + max + ".");
        }
        return valor;
    }

    /** Aceita CPF com ou sem pontuação; devolve só os 11 dígitos. */
    public static String cpf(String valor) {
        String digitos = textoObrigatorio(valor, "CPF").replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new RegraNegocioException("CPF deve ter 11 dígitos.");
        }
        return digitos;
    }
}
