# NoBuildingRequired

Mod Fabric para Minecraft 26.2. Você entrega os materiais numa **Architect Table**, recebe um **Blueprint**, escolhe onde e em que direção a construção fica, e a Litematica cola ela no mundo. Funciona em survival.

## Requisitos

Na pasta `.minecraft/mods`:

| Mod | Versão |
|---|---|
| [Fabric Loader](https://fabricmc.net/use/installer/) | 0.19.3+ (Minecraft 26.2) |
| [Fabric API](https://modrinth.com/mod/fabric-api) | 0.160.0+ |
| [Litematica](https://modrinth.com/mod/litematica) | para 26.2 |
| [MaLiLib](https://modrinth.com/mod/malilib) | para 26.2 |
| `nobuildingrequired-<versão>.jar` | este mod |

Java 25 (o launcher do Minecraft já traz).

Outros mods Fabric (minimapa, shaders, etc.) vão na mesma pasta e não interferem.

## Como jogar

1. **Crafte a Architect Table**:

   ```
   papel        papel          papel
   diamante     mesa de ferreiro   diamante
   tábuas       tábuas         tábuas
   ```

2. **Coloque a mesa e clique nela.** Abre uma GUI com 27 slots e um painel à direita.

3. **Escolha a construção** com `<` e `>`. O painel lista os materiais que ainda faltam, os que faltam mais no topo. Role o mouse sobre o painel pra ver o resto.

4. **Entregue os materiais.** Jogue os itens nos slots. O que a construção precisa a mesa absorve na hora e o slot esvazia; o que não precisa fica no slot pra você pegar de volta. Não tem limite de tipos nem de quantidade — a mesa lembra tudo que você já entregou, mesmo fechando o jogo.

5. **Generate.** Quando a lista zerar, o botão acende. Ele consome os materiais entregues e te dá um **Blueprint** daquela construção.

6. **Posicione com o Blueprint na mão:**

   | Gesto | Efeito |
   |---|---|
   | Clique direito num bloco | Coloca o contorno da construção ali |
   | Shift + clique direito num bloco | Gira 90° |
   | Clique direito no ar | **Confirma**: cola a construção e consome o Blueprint |
   | Shift + clique direito no ar | Cancela |

   A colagem é progressiva, por chunk. O contorno some quando termina.

### Comando de debug

`/nbr list` lista as construções; `/nbr ghost <nome> [graus]`, `/nbr paste` e `/nbr clear` fazem o mesmo que o Blueprint, sem precisar dele. Útil pra testar.

## Gerar o `.jar`

Precisa do JDK 25. Na pasta do projeto:

```bash
./gradlew build
```

No Windows, se o Gradle reclamar de JVM 8, aponte o JDK antes:

```powershell
$env:JAVA_HOME = "$HOME\.jdks\temurin-25.0.4.1"; .\gradlew build
```

O jar sai em `build/libs/nobuildingrequired-<versão>.jar` (ignore o `-sources.jar`). Copie pra `%APPDATA%\.minecraft\mods` e abra o jogo no perfil Fabric 26.2.

Cada push no GitHub também gera o jar: **Actions → build → Artifacts**.

Pra rodar direto do projeto, sem instalar nada: `./gradlew runClient`. Esse cliente usa a pasta `run/` do projeto, não a sua `.minecraft`.

## As construções

Vêm do repositório [jsonmatica](https://github.com/ReformedTheo/jsonmatica), incluído aqui como submodule em `jsonmatica/`. Ao clonar:

```bash
git clone --recurse-submodules https://github.com/ReformedTheo/nobuildingrequired.git
```

Pra adicionar uma construção: copie o `.litematic` pra `jsonmatica/schematics/` com um nome em `snake_case` e dê push no jsonmatica. O workflow de lá regenera o `catalog.json` (a lista de materiais de cada uma). Depois, no mod:

```bash
git submodule update --remote jsonmatica
```

Commit e push, e o próximo build já leva a construção nova.

Como o custo é calculado: cada bloco da schematic vira o item que você usaria pra colocá-lo (`wall_torch` → `torch`, vaso com flor → `flower_pot` + flor, porta conta uma vez). Água, portal, spawner e outras coisas sem item não entram. Entidades (quadros, armor stands) e o conteúdo de baús também não. A tabela está em `jsonmatica/items.py`.

## Licença

CC0. Faça o que quiser.
