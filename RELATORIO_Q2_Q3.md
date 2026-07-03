# P3 - Projeto e Analise de Algoritmos

Semente da dupla: **466473**

Integrantes:

- Joao Pedro Aguiar Silva - 0083466
- Victor Lopes Teodoro - 0083473

## Questao 2 - Subsequencia Crescente

### Recorrencia

Foi usada a formulacao reversa da subsequencia crescente:

$$
best[i] = 1 + \max(best[j]) \text{ para } j > i \text{ e } v[j] > v[i],
$$

com $best[i] = 1$ quando nao existe $j$ valido. O vetor $next$ guarda o proximo indice de uma subsequencia otima a partir de cada posicao.

### Sequencia inicial

\[32, 19, 32, 17, 31, 43, 30, 29, 54, 16, 28, 66, 15, 41, 65, 14, 50\]

### Resultado da instancia inicial

- `best = [4, 5, 4, 5, 4, 3, 3, 3, 2, 4, 3, 1, 3, 2, 1, 2, 1]`
- `next = [6, 3, 6, 5, 6, 9, 9, 9, 12, 11, 14, 0, 14, 15, 0, 17, 0]`
- tamanho da maior subsequencia crescente: **5**
- uma subsequencia maxima encontrada: **[19, 32, 43, 54, 66]**

### Sequencias geradas com a semente

Para `n = 100, 250, 500, 1000, 2000, 4000`, foi usado `Random(466473)` e os primeiros `n` inteiros no intervalo `[1, 10000]`.

### Medicoes

| n | comparacoes | tempo_ms | tamanho LIS |
|---:|---:|---:|---:|
| 100 | 4950 | 0 | 16 |
| 250 | 31125 | 1 | 28 |
| 500 | 124750 | 2 | 43 |
| 1000 | 499500 | 4 | 58 |
| 2000 | 1999000 | 16 | 84 |
| 4000 | 7998000 | 44 | 121 |

### Analise

As comparacoes cresceram de forma quadratica, como esperado para a DP $O(n^2)$. O tempo observou tendencia semelhante, mas com pequenas variacoes de ambiente. O tamanho da LIS cresceu com a entrada, sem alterar a ordem de complexidade.

## Questao 3 - Mochila 0/1

### Instancias

- `instancia_20`: 20 itens, capacidade 466
- `instancia_50`: 50 itens, capacidade 1250
- `knapPI_1_100_1000_1`: 100 itens, capacidade 995

### Metodos

- Guloso por maior razao valor/peso
- Guloso por maior valor absoluto
- Algoritmo genetico com duas configuracoes
- Programacao dinamica exata como referencia

### Configuracoes do AG

- Configuracao A: populacao 80, 250 geracoes, mutacao 0.02, torneio 3, elitismo 2, penalidade 1000
- Configuracao B: populacao 120, 320 geracoes, mutacao 0.05, torneio 4, elitismo 4, penalidade 2000

### Resumo dos resultados

#### instancia_20

- otimo DP: 3878
- guloso razao: 3878
- guloso valor: 3454
- GA A: 3878
- GA B: 3878

#### instancia_50

- otimo DP: 10693
- guloso razao: 10693
- guloso valor: 10234
- GA A: 10693
- GA B: 10693

#### knapPI_1_100_1000_1

- otimo DP: 9147
- guloso razao: 8817
- guloso valor: 5429
- GA A: 9147
- GA B: 7363

### Analise

O guloso por razao foi o melhor dos dois gulosos em todas as instancias. O guloso por valor absoluto funcionou bem nas instancias pequenas, mas ficou bem abaixo na instância oficial. O AG com a configuracao A alcançou a solucao otima nas tres instancias, enquanto a configuracao B melhorou em relacao aos gulosos, mas nao superou a referencia na instancia oficial. A programacao dinamica forneceu a referencia exata e, para as instancias testadas, teve custo computacional baixo o suficiente para ser viavel.

### Grafico do AG

Foram gerados graficos de geracao x melhor aptidao em `Q3/outputs` para cada instância.

## Reflexao final

- A questao que exigiu mais esforco foi a Mochila 0/1, porque envolveu varias tecnicas, comparacao entre abordagens e coleta de metricas.
- O resultado empirico mais marcante foi a diferenca entre o guloso por valor absoluto e o guloso por razao na instancia oficial.
- Eu melhoraria principalmente o algoritmo genetico, testando mais configuracoes e adicionando uma estrategia de reparo mais refinada para individuos inviaveis.

## Uso de IA

Foi utilizado apoio de IA para organizar a estrutura do projeto, acelerar a implementacao inicial em Java e revisar a consistencia dos resultados.
