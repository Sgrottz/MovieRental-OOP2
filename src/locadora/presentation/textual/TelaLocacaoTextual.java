package locadora.presentation.textual;

import java.time.LocalDate;

import locadora.application.ServicoLocadora;
import locadora.domain.Cliente;
import locadora.domain.Locacao;
import locadora.presentation.Formatacao;
import locadora.presentation.Tela;

class TelaLocacaoTextual implements Tela {
    private static final String PEDIDO_DATA = "Data (dd/MM/aaaa, Enter = hoje)";

    private final ServicoLocadora servico;

    TelaLocacaoTextual(ServicoLocadora servico) {
        this.servico = servico;
    }

    @Override
    public void exibir() {
        int opcao;
        do {
            System.out.println("\n--- LOCAÇÕES ---");
            System.out.println("1 - Alugar filme\n2 - Devolver filme\n3 - Listar locações de um cliente\n0 - Voltar");
            opcao = Console.lerInteiro("Opção");
            switch (opcao) {
                case 1 -> Console.executar(this::alugar);
                case 2 -> Console.executar(this::devolver);
                case 3 -> Console.executar(this::listarDoCliente);
                case 0 -> { }
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void alugar() {
        int clienteId = Console.lerInteiro("ID do cliente");
        int filmeId = Console.lerInteiro("ID do filme");
        LocalDate data = Formatacao.interpretarData(Console.lerTexto(PEDIDO_DATA));
        Locacao l = servico.alugar(clienteId, filmeId, data);
        System.out.println("Locação registrada. Devolver até " + Formatacao.data(l.getPrevista())
                + " (diária " + Formatacao.moeda(l.getFilme().valorDiaria()) + ").");
    }

    private void devolver() {
        int clienteId = Console.lerInteiro("ID do cliente");
        int filmeId = Console.lerInteiro("ID do filme");
        LocalDate data = Formatacao.interpretarData(Console.lerTexto(PEDIDO_DATA));
        Locacao l = servico.devolver(clienteId, filmeId, data);
        LocalDate ref = l.getDevolucao();
        System.out.println("Devolução registrada. Dias de atraso: " + l.diasAtraso(ref)
                + " | Multa: " + Formatacao.moeda(l.calcularMulta(ref))
                + " | Total: " + Formatacao.moeda(l.calcularTotal(ref)));
    }

    private void listarDoCliente() {
        Cliente c = servico.buscarCliente(Console.lerInteiro("ID do cliente"));
        LocalDate hoje = servico.hoje();
        if (c.getLocacoes().isEmpty()) {
            System.out.println(c.getNome() + " não possui locações.");
            return;
        }
        System.out.println("Locações de " + c.getNome() + ":");
        System.out.printf("%-26s %-11s %-11s %-11s %-10s %-10s %s%n",
                "Filme", "Retirada", "Prevista", "Devolução", "Situação", "Multa", "Total");
        for (Locacao l : c.getLocacoes()) {
            String situacao = !l.emAberto() ? "Devolvida" : l.estaAtrasada(hoje) ? "ATRASADA" : "Em dia";
            System.out.printf("%-26s %-11s %-11s %-11s %-10s %-10s %s%n", l.getFilme().getTitulo(),
                    Formatacao.data(l.getRetirada()), Formatacao.data(l.getPrevista()),
                    Formatacao.data(l.getDevolucao()), situacao,
                    Formatacao.moeda(l.calcularMulta(hoje)), Formatacao.moeda(l.calcularTotal(hoje)));
        }
        System.out.println("Multas pendentes: " + Formatacao.moeda(c.totalMultasPendentes(hoje)));
    }
}
