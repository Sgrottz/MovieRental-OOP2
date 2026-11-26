package locadora.presentation.gui;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import locadora.application.PersistenciaException;
import locadora.domain.RegraNegocioException;

/** Utilitários de Swing compartilhados pelas telas (classe estática, visível apenas neste pacote). */
final class Gui {
    private Gui() {
    }

    static void info(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Locadora", JOptionPane.INFORMATION_MESSAGE);
    }

    static void erro(Component pai, Exception e) {
        JOptionPane.showMessageDialog(pai, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /** Botão que executa a ação e mostra erros de negócio/persistência em uma caixa de diálogo. */
    static JButton botao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(evento -> {
            try {
                acao.run();
            } catch (RegraNegocioException | PersistenciaException e) {
                erro(botao, e);
            }
        });
        return botao;
    }

    /** Tabela somente leitura. */
    static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    /** Valor inteiro da coluna indicada na linha selecionada. */
    static int inteiroSelecionado(JTable tabela, int coluna) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            throw new RegraNegocioException("Selecione uma linha da tabela.");
        }
        return (Integer) tabela.getValueAt(linha, coluna);
    }

    static int inteiro(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new RegraNegocioException(campo + " deve ser um número inteiro.");
        }
    }

    /** Formulário simples de campos de texto. Devolve os valores ou null se o usuário cancelar. */
    static String[] formulario(Component pai, String titulo, String[] rotulos, String[] iniciais) {
        JPanel painel = new JPanel(new GridLayout(rotulos.length, 2, 6, 6));
        JTextField[] campos = new JTextField[rotulos.length];
        for (int i = 0; i < rotulos.length; i++) {
            painel.add(new JLabel(rotulos[i]));
            campos[i] = new JTextField(iniciais[i], 20);
            painel.add(campos[i]);
        }
        int resposta = JOptionPane.showConfirmDialog(pai, painel, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resposta != JOptionPane.OK_OPTION) {
            return null;
        }
        String[] valores = new String[campos.length];
        for (int i = 0; i < campos.length; i++) {
            valores[i] = campos[i].getText();
        }
        return valores;
    }
}
