package locadora.presentation.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import locadora.application.ServicoLocadora;
import locadora.domain.Cliente;
import locadora.domain.Locacao;
import locadora.presentation.Formatacao;
import locadora.presentation.Tela;

class TelaLocacaoGui implements Tela {
    private static final String[] ROTULOS_ALUGUEL = {"ID do cliente", "ID do filme", "Data (dd/MM/aaaa, vazio = hoje)"};

    private final ServicoLocadora servico;
    private final DefaultTableModel modelo = Gui.modelo("ID Cliente", "ID Filme", "Cliente", "Filme",
            "Retirada", "Prevista", "Devolução", "Situação", "Multa", "Total");
    private final JTable tabela = new JTable(modelo);
    private JDialog janela;

    TelaLocacaoGui(ServicoLocadora servico) {
        this.servico = servico;
    }

    @Override
    public void exibir() {
        janela = new JDialog((Frame) null, "Locações", false);
        janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        janela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        botoes.add(Gui.botao("Alugar", this::alugar));
        botoes.add(Gui.botao("Devolver selecionada", this::devolver));
        botoes.add(Gui.botao("Fechar", janela::dispose));
        janela.add(botoes, BorderLayout.SOUTH);

        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                atualizar();
            }
        });
        janela.setSize(980, 380);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    private void atualizar() {
        modelo.setRowCount(0);
        LocalDate hoje = servico.hoje();
        for (Cliente c : servico.listarClientes()) {
            for (Locacao l : c.getLocacoes()) {
                String situacao = !l.emAberto() ? "Devolvida" : l.estaAtrasada(hoje) ? "ATRASADA" : "Em dia";
                modelo.addRow(new Object[]{c.getId(), l.getFilme().getId(), c.getNome(),
                        l.getFilme().getTitulo(), Formatacao.data(l.getRetirada()),
                        Formatacao.data(l.getPrevista()), Formatacao.data(l.getDevolucao()), situacao,
                        Formatacao.moeda(l.calcularMulta(hoje)), Formatacao.moeda(l.calcularTotal(hoje))});
            }
        }
    }

    private void alugar() {
        String[] v = Gui.formulario(janela, "Alugar filme", ROTULOS_ALUGUEL, new String[]{"", "", ""});
        if (v != null) {
            Locacao l = servico.alugar(Gui.inteiro(v[0], "ID do cliente"), Gui.inteiro(v[1], "ID do filme"),
                    Formatacao.interpretarData(v[2]));
            atualizar();
            Gui.info("Locação registrada. Devolver até " + Formatacao.data(l.getPrevista()) + ".");
        }
    }

    private void devolver() {
        int clienteId = Gui.inteiroSelecionado(tabela, 0);
        int filmeId = Gui.inteiroSelecionado(tabela, 1);
        String[] v = Gui.formulario(janela, "Devolver filme", new String[]{"Data (dd/MM/aaaa, vazio = hoje)"},
                new String[]{""});
        if (v != null) {
            Locacao l = servico.devolver(clienteId, filmeId, Formatacao.interpretarData(v[0]));
            atualizar();
            LocalDate ref = l.getDevolucao();
            Gui.info("Devolução registrada.\nDias de atraso: " + l.diasAtraso(ref)
                    + "\nMulta: " + Formatacao.moeda(l.calcularMulta(ref))
                    + "\nTotal: " + Formatacao.moeda(l.calcularTotal(ref)));
        }
    }
}
