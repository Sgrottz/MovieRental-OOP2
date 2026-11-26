package locadora.presentation.gui;

import locadora.application.ServicoLocadora;
import locadora.presentation.FabricaInterface;
import locadora.presentation.Tela;

/** Fábrica concreta: família de telas Swing. */
public class FabricaGui implements FabricaInterface {
    @Override
    public Tela criarMenuPrincipal(ServicoLocadora servico) {
        return new MenuPrincipalGui(servico, this);
    }

    @Override
    public Tela criarTelaCliente(ServicoLocadora servico) {
        return new TelaClienteGui(servico);
    }

    @Override
    public Tela criarTelaFilme(ServicoLocadora servico) {
        return new TelaFilmeGui(servico);
    }

    @Override
    public Tela criarTelaLocacao(ServicoLocadora servico) {
        return new TelaLocacaoGui(servico);
    }
}
