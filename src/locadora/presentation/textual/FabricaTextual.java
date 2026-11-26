package locadora.presentation.textual;

import locadora.application.ServicoLocadora;
import locadora.presentation.FabricaInterface;
import locadora.presentation.Tela;

/** Fábrica concreta: família de telas de console. */
public class FabricaTextual implements FabricaInterface {
    @Override
    public Tela criarMenuPrincipal(ServicoLocadora servico) {
        return new MenuPrincipalTextual(servico, this);
    }

    @Override
    public Tela criarTelaCliente(ServicoLocadora servico) {
        return new TelaClienteTextual(servico);
    }

    @Override
    public Tela criarTelaFilme(ServicoLocadora servico) {
        return new TelaFilmeTextual(servico);
    }

    @Override
    public Tela criarTelaLocacao(ServicoLocadora servico) {
        return new TelaLocacaoTextual(servico);
    }
}
