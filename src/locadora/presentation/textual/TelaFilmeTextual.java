package locadora.presentation.textual;

import locadora.application.ServicoLocadora;
import locadora.domain.Filme;
import locadora.domain.RegraNegocioException;
import locadora.domain.TipoFilme;
import locadora.presentation.Formatacao;
import locadora.presentation.Tela;

class TelaFilmeTextual implements Tela {
    private final ServicoLocadora servico;

    TelaFilmeTextual(ServicoLocadora servico) {
        this.servico = servico;
    }

    @Override
    public void exibir() {
        int opcao;
        do {
            System.out.println("\n--- FILMES ---");
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
        if (servico.listarFilmes().isEmpty()) {
            System.out.println("Nenhum filme cadastrado.");
            return;
        }
        System.out.printf("%-4s %-12s %-28s %-5s %-10s %s%n", "ID", "Tipo", "Título", "Ano", "Disp/Total", "Diária");
        for (Filme f : servico.listarFilmes()) {
            System.out.printf("%-4d %-12s %-28s %-5d %-10s %s%n", f.getId(), f.tipo().getRotulo(),
                    f.getTitulo(), f.getAno(), f.getCopiasDisponiveis() + "/" + f.getCopiasTotais(),
                    Formatacao.moeda(f.valorDiaria()));
        }
    }

    private void cadastrar() {
        TipoFilme[] tipos = TipoFilme.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + " - " + tipos[i].getRotulo());
        }
        int escolha = Console.lerInteiro("Tipo do filme");
        if (escolha < 1 || escolha > tipos.length) {
            throw new RegraNegocioException("Tipo inválido.");
        }
        Filme f = servico.cadastrarFilme(tipos[escolha - 1], Console.lerTexto("Título"),
                Console.lerInteiro("Ano"), Console.lerInteiro("Número de cópias"));
        System.out.println("Filme cadastrado com ID " + f.getId() + ".");
    }

    private void editar() {
        int id = Console.lerInteiro("ID do filme");
        Filme f = servico.buscarFilme(id);
        servico.atualizarFilme(id,
                Console.lerTextoOuAtual("Título", f.getTitulo()),
                Console.lerInteiroOuAtual("Ano", f.getAno()),
                Console.lerInteiroOuAtual("Total de cópias", f.getCopiasTotais()));
        System.out.println("Filme atualizado.");
    }

    private void excluir() {
        servico.excluirFilme(Console.lerInteiro("ID do filme"));
        System.out.println("Filme excluído.");
    }
}
