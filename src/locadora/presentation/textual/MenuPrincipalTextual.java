package locadora.presentation.textual;

import locadora.application.ServicoLocadora;
import locadora.presentation.FabricaInterface;
import locadora.presentation.Tela;

class MenuPrincipalTextual implements Tela {
    private final ServicoLocadora servico;
    private final FabricaInterface fabrica;

    MenuPrincipalTextual(ServicoLocadora servico, FabricaInterface fabrica) {
        this.servico = servico;
        this.fabrica = fabrica;
    }

    @Override
    public void exibir() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n=== LOCADORA DE FILMES ===");
            System.out.println("1 - Clientes");
            System.out.println("2 - Filmes");
            System.out.println("3 - Locações");
            System.out.println("4 - Salvar dados em arquivo");
            System.out.println("5 - Carregar dados do arquivo");
            System.out.println("0 - Sair");
            switch (Console.lerInteiro("Opção")) {
                case 1 -> fabrica.criarTelaCliente(servico).exibir();
                case 2 -> fabrica.criarTelaFilme(servico).exibir();
                case 3 -> fabrica.criarTelaLocacao(servico).exibir();
                case 4 -> Console.executar(() -> {
                    servico.salvar();
                    System.out.println("Dados salvos.");
                });
                case 5 -> Console.executar(() -> {
                    servico.carregar();
                    System.out.println("Dados carregados.");
                });
                case 0 -> sair = true;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Até logo!");
    }
}
