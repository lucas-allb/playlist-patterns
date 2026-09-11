-----

<p align="center">
  <img alt="upe" src="./img/upe-logo.png"/>
</p>

-----

# Lista 01 — Padrões Estruturais

Disciplina do curso de Engenharia de Software da Universidade de Pernambuco — Campus Garanhuns

-----

## 📌 Sobre a lista

Esta é a primeira lista prática da disciplina e tem como objetivo consolidar os principais conceitos estudados na primeira parte da disciplina de Padrões de Projeto.

Esta atividade foca na resolução de pequeno sistema de gestão de playlists de músicas ou serviço de streaming.

Serão trabalhados os seguintes Padrões de Projeto:

- Facade
- Adapter
- Composite
- Proxy
- Decorator

Todas as soluções serão validadas por **testes automatizados executados por meio do mvn**.

## Como entregar

1. Faça um **fork** deste repositório para a sua conta do GitHub.
2. Clone o seu fork:
   ```bash
   git clone URL_DO_SEU_FORK
   cd playlist-patterns
   ```
3. Crie uma branch a partir da `main`:
   ```bash
    git checkout -b entrega-seu-nome
    ```
4. Implemente os exercícios. Você pode fazer quantos commits quiser:
    ```bash
    git add .
    git commit -m "Implementa Composite"
    ```
5. Suba a sua branch para o seu fork:
    ```bash
    git push -u origin entrega-seu-nome
    ```
6. Abra um Pull Request do seu fork para a `main` deste repositório.
7. O avaliador roda automaticamente e publica a nota no Pull Request.
8. Envie quantas vezes quiser, mas antes de fazê-lo garanta que sua solução passa localmente.
---

## Rodando localmente

```bash
mvn test # roda todos os testes
mvn checkstyle:check # confere o estilo de código (linter)
```

Para rodar um único teste:

```bash
mvn test -Dtest=Req03ProxyTest
```

Requisitos de ambiente: **JDK 17** e **Maven 3.8+**.

---

## Estrutura do projeto

```
src/main/java/com/playlist/
├── core/  compartilhado: Track, Subscription, exceções (pronto)
├── composite/ #1
├── adapter/ #2  (external/ = sistema de terceiros)
├── proxy/ #3
├── decorator/ #4
└── facade/ #5
```

Cada método que você precisa implementar já existe com a assinatura correta.

### ⛔ Arquivos que você não pode alterar

O avaliador restaura automaticamente a versão original destes caminhos antes de
corrigir, e sinaliza a alteração no comentário da nota:

```
.github/
src/test/                                   
src/main/java/com/playlist/core/
src/main/java/com/playlist/composite/MediaItem.java
src/main/java/com/playlist/adapter/TrackCatalog.java
src/main/java/com/playlist/adapter/external/
src/main/java/com/playlist/proxy/AudioStream.java
src/main/java/com/playlist/proxy/RemoteAudioStream.java
src/main/java/com/playlist/decorator/AudioTrack.java
src/main/java/com/playlist/decorator/RawAudioTrack.java
pom.xml
src/main/resources/legacy/
```

Todavia, você pode criar quantas classes, arquivos e pacotes quiser.

---

## Exercício 1 — Composite

Uma playlist pode conter faixas e outras playlists, em qualquer profundidade. 
"Rock dos anos 70" pode estar dentro de "Rock", que está dentro de "Biblioteca". 
E a interface precisa mostrar duração total e número de faixas para qualquer uma dessas coisas, sem se importar com o que ela é por dentro.

Implemente `TrackItem` (a folha) e `PlaylistNode` (o composite), ambos implementando `MediaItem`.

**`TrackItem`**

| Método | Comportamento |
|---|---|
| construtor | recebe uma `Track`; lança `IllegalArgumentException` se for `null` |
| `getName()` | o título da faixa |
| `getDurationSeconds()` | a duração da faixa |
| `getTrackCount()` | sempre `1` |
| `flatten()` | lista com a única faixa |
| `getTrack()` | a faixa original |

**`PlaylistNode`**

| Método | Comportamento                                                      |
|---|--------------------------------------------------------------------|
| construtor | recebe o nome; lança `IllegalArgumentException` se for `null` ou em branco |
| `add(item)` | adiciona no fim e devolve a própria playlist (permite encadear)    |
| `remove(item)` | remove um filho direto e devolve `true` se removeu                 |
| `getChildren()` | lista imutável dos filhos diretos, na ordem de inserção            |
| `contains(item)` | busca em qualquer profundidade                                     |
| `getDurationSeconds()` | soma recursiva; `0` se estiver vazia                               |
| `getTrackCount()` | soma recursiva; `0` se estiver vazia                               |
| `flatten()` | todas as faixas, em profundidade, na ordem de inserção             |

`add` lança `IllegalArgumentException` quando o item é `null`, quando é a própria playlist, ou quando adicioná-lo criaria um ciclo (o item é uma playlist que já contém esta aqui).

---

## Exercício 2 — Adapter

Compramos o acervo de uma gravadora antiga. Os dados vêm de um sistema de 1998 que você não pode alterar (`LegacyVinylCatalog`) e devolve texto puro:

```
VNL-0001|BOHEMIAN RHAPSODY|MERCURY, FREDDIE|354000|N
   id   |     título      |  SOBRENOME, NOME| ms  |premium
```

O resto do sistema conhece apenas a interface `TrackCatalog`, que fala em `Track`. 
Implemente `VinylCatalogAdapter` para fazer a adaptação desse conteúdo.

**Regras de conversão**

| Campo legado | Vira | Regra |
|---|---|---|
| número de catálogo | `id` | sem espaços nas pontas |
| título em caixa alta | `title` | primeira letra de cada palavra maiúscula, o resto minúsculo; espaços extras removidos |
| `"MERCURY, FREDDIE"` | `artist` | `"Freddie Mercury"` — nome, espaço, sobrenome, com a mesma capitalização |
| duração em milissegundos | `durationSeconds` | divisão inteira por 1000 (`356999` → `356`) |
| `Y` / `N` | `premium` | `Y` (em qualquer caixa) é `true` |

**Registros inválidos são ignorados.** É inválido o registro que não tem exatamente 5 campos, cujo id ou título fique vazio, ou cuja duração não seja um número inteiro não negativo. `findAll()` simplesmente não os inclui, e `findById()` devolve `Optional.empty()` para eles.

`findById(id)` deve usar `findRecordByCatalogNumber` do sistema legado. Não varra o catálogo inteiro. Ids nulos ou em branco devolvem `Optional.empty()`.

---

## Exercício 3 — Proxy

Abrir um `RemoteAudioStream` significa abrir conexão com o servidor de mídia, e cada leitura é uma transferência cara. 
Além disso, faixas marcadas como `premium` só podem ser ouvidas por quem assina o plano `PREMIUM`.

Implemente `ProtectedAudioStreamProxy`, que implementa `AudioStream` e fica entre quem quer ouvir e o objeto real, acumulando três responsabilidades:

**Proteção.** Se a faixa é `premium` e o plano é `FREE`, `readBytes()` lança `AccessDeniedException` e o objeto real sequer será criado.

**Lazy Loading.** O `Supplier<AudioStream>` recebido no construtor só pode ser chamado na primeira vez que o áudio for realmente pedido. `getTrackId()` responde direto pela `Track` e **não** dispara o carregamento. `isLoaded()` devolve `false` até o objeto real existir.

**Cache.** A partir da segunda chamada, `readBytes()` devolve o conteúdo já baixado: nem o `Supplier` nem o `readBytes()` do objeto real são chamados de novo. 
E o array devolvido é uma cópia.

O construtor de três argumentos lança `IllegalArgumentException` para qualquer argumento nulo. O construtor de dois argumentos é um atalho que usa
`RemoteAudioStream` como objeto real e continua Lazy.

---

## Exerc´cicio 4 — Decorator

Queremos aplicar efeitos em cima do áudio de forma combinável, sem criar uma classe para cada combinação possível. 
Implemente o decorador abstrato `AudioEffect` e três efeitos concretos.

**`AudioEffect`** guarda o `AudioTrack` decorado (`IllegalArgumentException` se for nulo), repassa `getTitle()` para ele, e monta `getEffectChain()` como:

```
<cadeia de quem foi decorado> + " -> " + describe()
```

**Efeitos concretos** (operando sobre `wrapped.getSamples()`):

| Classe | `getSamples()`                                                                                              | `describe()` |
|---|-------------------------------------------------------------------------------------------------------------|---|
| `VolumeEffect(track, factor)` | Cada amostra × `factor`, com corte em `[-1.0, 1.0]`                                                         | `volume(2.0)` — `String.format(Locale.ROOT, "volume(%.1f)", factor)` |
| `FadeInEffect(track, n)` | as `n` Primeiras amostras × `i / n` (onde `i` é o índice); as demais inalteradas; se `n <= 0`, não faz nada | `fadeIn(3)` |
| `NoiseGateEffect(track, limiar)` | Amostras com valor absoluto **menor** que o limiar viram `0.0`                                              | `noiseGate(0.30)` — `String.format(Locale.ROOT, "noiseGate(%.2f)", limiar)` |

Assim, `new NoiseGateEffect(new FadeInEffect(new VolumeEffect(raw, 2.0), 3), 0.3)` tem a cadeia:

```
original -> volume(2.0) -> fadeIn(3) -> noiseGate(0.30)
```

Nenhum efeito pode alterar as amostras do áudio decorado: chamar `getSamples()` duas vezes tem que dar exatamente o mesmo resultado, e o `RawAudioTrack` de origem precisa continuar intacto.

---

## Exercício 5 — Facade

Usuários não deveriam precisar montar adapter, proxy, composite e decorators na mão. 
Implemente `PlaylistFacade`, que recebe um `TrackCatalog` e um `Subscription` (`IllegalArgumentException` se algum for nulo) e expõe três
operações:

- **`buildLibrary(nome)`**: devolve um `PlaylistNode` com esse nome contendo um `TrackItem` para cada faixa de `catalog.findAll()`, na mesma ordem.

- **`listen(trackId)`**: devolve os bytes da faixa. Lança `TrackNotFoundException` se o id não existir no catálogo, e propaga o `AccessDeniedException` do proxy quando o plano não permite. 
Chamar `listen` duas vezes para a **mesma faixa** não pode abrir uma segunda conexão: guarde o proxy já criado.

- **`preview(trackId, volume, fadeInSamples)`**: devolve um `AudioTrack` já decorado. O áudio vem de `listen(trackId)`, e cada byte vira uma amostra
  dividida por `128.0`. Sobre esse `RawAudioTrack` (cujo título é o título da
  faixa) aplique **primeiro** o volume e **depois** o fade in, resultando na
  cadeia `original -> volume(2.0) -> fadeIn(4)`.

> O exercício 5 só passa se os exercícios 1, 3 e 4 estiverem funcionando.

---