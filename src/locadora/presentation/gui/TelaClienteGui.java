package locadora.presentation.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import locadora.application.ServicoLocadora;
import locadora.domain.Cliente;
import locadora.presentation.Tela;

class TelaClienteGui implements Tela {
    private static final String[] ROTULOS = {"Nome", "CPF", "Telefone"};

    private final ServicoLocadora servico;
    private final DefaultTableModel modelo = Gui.modelo("ID", "Nome", "CPF", "Telefone", "Em aberto");
    private final JTable tabela = new JTable(modelo);
    private JDialog janela;

    TelaClienteGui(ServicoLocadora servico) {
        this.servico = servico;
    }

    @Override
    public void exibir() {
        janela = new JDialog((Frame) null, "Clientes", false);
        janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        janela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        botoes.add(Gui.botao("Novo", this::novo));
        botoes.add(Gui.botao("Editar", this::editar));
        botoes.add(Gui.botao("Excluir", this::excluir));
        botoes.add(Gui.botao("Fechar", janela::dispose));
        janela.add(botoes, BorderLayout.SOUTH);

        // atualiza a tabela sempre que a janela volta a ficar ativa (ex.: após "Carregar dados")
        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                atualizar();
            }
        });
        janela.setSize(640, 360);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    private void atualizar() {
        modelo.setRowCount(0);
        for (Cliente c : servico.listarClientes()) {
            modelo.addRow(new Object[]{c.getId(), c.getNome(), c.getCpf(), c.getTelefone(),
                    c.locacoesEmAberto().size()});
        }
    }

    private void novo() {
        String[] v = Gui.formulario(janela, "Novo cliente", ROTULOS, new String[]{"", "", ""});
        if (v != null) {
            servico.cadastrarCliente(v[0], v[1], v[2]);
            atualizar();
        }
    }

    private void editar() {
        int id = Gui.inteiroSelecionado(tabela, 0);
        Cliente c = servico.buscarCliente(id);
        String[] v = Gui.formulario(janela, "Editar cliente", ROTULOS,
                new String[]{c.getNome(), c.getCpf(), c.getTelefone()});
        if (v != null) {
            servico.atualizarCliente(id, v[0], v[1], v[2]);
            atualizar();
        }
    }

    private void excluir() {
        servico.excluirCliente(Gui.inteiroSelecionado(tabela, 0));
        atualizar();
    }
}
