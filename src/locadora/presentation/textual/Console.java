package locadora.presentation.textual;

import java.util.Scanner;

import locadora.application.PersistenciaException;
import locadora.domain.RegraNegocioException;

/** Utilitários de leitura do console (classe estática, visível apenas neste pacote). */
final class Console {
    private static final Scanner ENTRADA = new Scanner(System.in);

    private Console() {
    }

    static String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        return ENTRADA.nextLine().trim();
    }

    /** Ao editar: Enter vazio mantém o valor atual. */
    static String lerTextoOuAtual(String rotulo, String atual) {
        String lido = lerTexto(rotulo + " [" + atual + "]");
        return lido.isEmpty() ? atual : lido;
    }

    static int lerInteiro(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    static int lerInteiroOuAtual(String rotulo, int atual) {
        while (true) {
            String lido = lerTexto(rotulo + " [" + atual + "]");
            if (lido.isEmpty()) return atual;
            try {
                return Integer.parseInt(lido);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    /** Executa uma ação mostrando a mensagem de erro (em vez de derrubar o programa) se ela falhar. */
    static void executar(Runnable acao) {
        try {
            acao.run();
        } catch (RegraNegocioException | PersistenciaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
