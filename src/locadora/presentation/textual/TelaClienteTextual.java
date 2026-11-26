package locadora.presentation.textual;

import locadora.application.ServicoLocadora;
import locadora.domain.Cliente;
import locadora.presentation.Tela;

class TelaClienteTextual implements Tela {
    private final ServicoLocadora servico;

    TelaClienteTextual(ServicoLocadora servico) {
        this.servico = servico;
    }

    @Override
    public void exibir() {
        int opcao;
        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1 - Listar\n2 - Cadastrar\n3 - Editar\n4 - Excluir\n0 - Voltar");
            opcao = Console.lerInteiro("Opção");
            switch (opcao) {
                case 1 -> listar();
                case 2 -> Console.executar(this::cadastrar);
                case 3 -> Console.executar(this::editar);
                case 4 -> Console.executar(this::excluir);
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void listar() {
        if (servico.listarClientes().isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.printf("%-4s %-25s %-12s %-15s %s%n", "ID", "Nome", "CPF", "Telefone", "Em aberto");
        for (Cliente c : servico.listarClientes()) {
            System.out.printf("%-4d %-25s %-12s %-15s %d%n", c.getId(), c.getNome(), c.getCpf(),
                    c.getTelefone(), c.locacoesEmAberto().size());
        }
    }

    private void cadastrar() {
        Cliente c = servico.cadastrarCliente(Console.lerTexto("Nome"), Console.lerTexto("CPF"),
                Console.lerTexto("Telefone"));
        System.out.println("Cliente cadastrado com ID " + c.getId() + ".");
    }

    private void editar() {
        int id = Console.lerInteiro("ID do cliente");
        Cliente c = servico.buscarCliente(id);
        servico.atualizarCliente(id,
                Console.lerTextoOuAtual("Nome", c.getNome()),
                Console.lerTextoOuAtual("CPF", c.getCpf()),
                Console.lerTextoOuAtual("Telefone", c.getTelefone()));
        System.out.println("Cliente atualizado.");
    }

    private void excluir() {
        servico.excluirCliente(Console.lerInteiro("ID do cliente"));
        System.out.println("Cliente excluído.");
    }
}
