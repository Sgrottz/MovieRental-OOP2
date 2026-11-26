# Locadora de Filmes — POO2

Sistema em Java (CRUD de **Cliente** e **Filme**, aluguel/devolução com multa, persistência em arquivo),
com arquitetura em camadas organizada por contexto (`locadora`) e depois por camada.

## Como abrir e executar
1. IntelliJ: **File > Open** e escolha a pasta do projeto. Se pedir, defina o **Project SDK** (JDK 17 ou superior).
2. Execute a classe `Main` (pasta `src`, pacote padrão).
3. Para trocar a interface ou o formato de arquivo, edite **`locadora.properties`** (na raiz do projeto) — sem tocar no código:
   ```
   interface=textual     # ou gui
   formato=serializado   # ou csv
   ```
   > A pasta de execução deve ser a raiz do projeto (padrão do IntelliJ), onde está o `locadora.properties`.

## Estrutura
```
src/
├── Main.java                         ponto de entrada (monta as peças)
└── locadora/
    ├── domain/          Locadora, Cliente, Locacao, Filme (abstrata), FilmeLancamento,
    │                    FilmeCatalogo, TipoFilme, Validador (estática), RegraNegocioException
    ├── application/     ServicoLocadora, RepositorioLocadora (interface), Configuracao (Singleton)
    ├── infrastructure/  RepositorioSerializado, RepositorioCsv, FabricaRepositorio (estática)
    └── presentation/    Tela, FabricaInterface (Abstract Factory), Formatacao (estática)
        ├── textual/     FabricaTextual + telas de console
        └── gui/         FabricaGui + telas Swing
docs/                    diagramas UML (Mermaid)
locadora.properties      configuração
```

## Onde cada requisito é atendido
| Requisito | Onde |
|---|---|
| 2+ classes de negócio com listas entre si | `Locadora` → `List<Cliente>`/`List<Filme>`; `Cliente` → `List<Locacao>` |
| CRUD | Cliente e Filme (`Locadora`/`ServicoLocadora` + telas) |
| Interface com 2+ implementações | `RepositorioLocadora` (Serializado, CSV); `FabricaInterface` (Textual, Gui); `Tela` (8 telas) |
| Classe abstrata com 2+ implementações | `Filme` → `FilmeLancamento`, `FilmeCatalogo` |
| Classe estática | `Validador`, `Formatacao`, `FabricaRepositorio` |
| Abstract Factory | `FabricaInterface` + `FabricaTextual`/`FabricaGui`, escolhida por `locadora.properties` |
| Outros padrões | Singleton (`Configuracao`), Repository (`RepositorioLocadora`) |
| Salvar/carregar | Menu principal; serializado ou CSV |
| Menor visibilidade | telas, repositórios e `Console`/`Gui` são *package-private*; `Filme.retirarCopia`, `Cliente.alugar` etc. também |
| Domínio sem I/O | nenhuma classe de `domain` usa console/Swing |

## Regras de negócio
- Máximo de 3 locações em aberto por cliente; cliente com locação atrasada não aluga.
- Mesmo filme não pode ser alugado duas vezes simultaneamente pelo mesmo cliente.
- Filme sem cópia disponível não pode ser alugado._
- Multa = dias de atraso × multa diária do tipo (Lançamento R$ 4,00; Catálogo R$ 2,00).
- Não exclui cliente com locação em aberto, nem filme com histórico de locações.
- CPF único (11 dígitos).
- Datas de aluguel/devolução são opcionais na interface (vazio = hoje); informe uma data passada para simular atraso.
