package locadora.presentation.gui;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import locadora.application.ServicoLocadora;
import locadora.presentation.FabricaInterface;
import locadora.presentation.Tela;

class MenuPrincipalGui implements Tela {
    private final ServicoLocadora servico;
    private final FabricaInterface fabrica;

    MenuPrincipalGui(ServicoLocadora servico, FabricaInterface fabrica) {
        this.servico = servico;
        this.fabrica = fabrica;
    }

    @Override
    public void exibir() {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Locadora de Filmes");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel painel = new JPanel(new GridLayout(0, 1, 8, 8));
            painel.setBorder(new EmptyBorder(16, 24, 16, 24));
            painel.add(Gui.botao("Clientes", () -> fabrica.criarTelaCliente(servico).exibir()));
            painel.add(Gui.botao("Filmes", () -> fabrica.criarTelaFilme(servico).exibir()));
            painel.add(Gui.botao("Locações", () -> fabrica.criarTelaLocacao(servico).exibir()));
            painel.add(Gui.botao("Salvar dados", () -> {
                servico.salvar();
                Gui.info("Dados salvos.");
            }));
            painel.add(Gui.botao("Carregar dados", () -> {
                servico.carregar();
                Gui.info("Dados carregados.");
            }));
            painel.add(Gui.botao("Sair", () -> System.exit(0)));

            janela.setContentPane(painel);
            janela.pack();
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
