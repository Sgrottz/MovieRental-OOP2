package locadora.presentation.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import locadora.application.ServicoLocadora;
import locadora.domain.Filme;
import locadora.domain.TipoFilme;
import locadora.presentation.Formatacao;
import locadora.presentation.Tela;

class TelaFilmeGui implements Tela {
    private static final String[] ROTULOS = {"Título", "Ano", "Total de cópias"};

    private final ServicoLocadora servico;
    private final DefaultTableModel modelo =
            Gui.modelo("ID", "Tipo", "Título", "Ano", "Disponíveis", "Total", "Diária");
    private final JTable tabela = new JTable(modelo);
    private JDialog janela;

    TelaFilmeGui(ServicoLocadora servico) {
        this.servico = servico;
    }

    @Override
    public void exibir() {
        janela = new JDialog((Frame) null, "Filmes", false);
        janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        janela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        botoes.add(Gui.botao("Novo", this::novo));
        botoes.add(Gui.botao("Editar", this::editar));
        botoes.add(Gui.botao("Excluir", this::excluir));
        botoes.add(Gui.botao("Fechar", janela::dispose));
        janela.add(botoes, BorderLayout.SOUTH);

        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                atualizar();
            }
        });
        janela.setSize(720, 360);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    private void atualizar() {
        modelo.setRowCount(0);
        for (Filme f : servico.listarFilmes()) {
            modelo.addRow(new Object[]{f.getId(), f.tipo().getRotulo(), f.getTitulo(), f.getAno(),
                    f.getCopiasDisponiveis(), f.getCopiasTotais(), Formatacao.moeda(f.valorDiaria())});
        }
    }

    private void novo() {
        Object tipo = JOptionPane.showInputDialog(janela, "Tipo do filme:", "Novo filme",
                JOptionPane.PLAIN_MESSAGE, null, TipoFilme.values(), TipoFilme.CATALOGO);
        if (tipo == null) return;
        String[] v = Gui.formulario(janela, "Novo filme", ROTULOS, new String[]{"", "", "1"});
        if (v != null) {
            servico.cadastrarFilme((TipoFilme) tipo, v[0], Gui.inteiro(v[1], "Ano"),
                    Gui.inteiro(v[2], "Cópias"));
            atualizar();
        }
    }

    private void editar() {
        int id = Gui.inteiroSelecionado(tabela, 0);
        Filme f = servico.buscarFilme(id);
        String[] v = Gui.formulario(janela, "Editar filme", ROTULOS, new String[]{
                f.getTitulo(), String.valueOf(f.getAno()), String.valueOf(f.getCopiasTotais())});
        if (v != null) {
            servico.atualizarFilme(id, v[0], Gui.inteiro(v[1], "Ano"), Gui.inteiro(v[2], "Cópias"));
            atualizar();
        }
    }

    private void excluir() {
        servico.excluirFilme(Gui.inteiroSelecionado(tabela, 0));
        atualizar();
    }
}
