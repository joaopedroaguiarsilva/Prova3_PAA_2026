# P3 - Projeto e Análise de Algoritmos

Semente da dupla: **466473**

Integrantes:

- Joao Pedro Aguiar Silva - 0083466
- Victor Lopes Teodoro - 0083473

## Questão 2 - Subsequência Crescente

### Recorrência

Usamos a formulação reversa da subsequência crescente:

$$
best[i] = 1 + \max(best[j]) \text{ para } j > i \text{ e } v[j] > v[i],
$$

com $best[i] = 1$ quando não existe $j$ válido. O vetor $next$ guarda o próximo índice de uma subsequência ótima a partir de cada posição.

### Sequência inicial

v = [32, 19, 32, 17, 31, 43, 30, 29, 54, 16, 28, 66, 15, 41, 65, 14, 50]

### Resultado da instância inicial

- `best = [4, 5, 4, 5, 4, 3, 3, 3, 2, 4, 3, 1, 3, 2, 1, 2, 1]`
- `next = [6, 3, 6, 5, 6, 9, 9, 9, 12, 11, 14, 0, 14, 15, 0, 17, 0]`
- tamanho da maior subsequência crescente: **5**
- uma subsequência máxima encontrada: **[19, 32, 43, 54, 66]**

### Sequências geradas com a semente

Para `n = 100, 250, 500, 1000, 2000, 4000`, usamos `Random(466473)` e geramos os primeiros `n` inteiros no intervalo `[1, 10000]`.

### Medições

| n | comparacoes | tempo_ms | tamanho LIS |
|---:|---:|---:|---:|
| 100 | 4950 | 0 | 16 |
| 250 | 31125 | 1 | 28 |
| 500 | 124750 | 2 | 43 |
| 1000 | 499500 | 4 | 58 |
| 2000 | 1999000 | 16 | 84 |
| 4000 | 7998000 | 44 | 121 |

### Análise

As comparações cresceram de forma quadrática, como esperado para a DP $O(n^2)$. O tempo seguiu a mesma tendência, com pequenas variações de ambiente. O tamanho da LIS cresceu com a entrada, sem mudar a ordem de complexidade.

## Questão 3 - Mochila 0/1

### Instâncias

- `instancia_20`: 20 itens, capacidade 466
- `instancia_50`: 50 itens, capacidade 1250
- `knapPI_1_100_1000_1`: 100 itens, capacidade 995

### Métodos

- Guloso por maior razão valor/peso
- Guloso por maior valor absoluto
- Algoritmo genético com duas configurações
- Programação dinâmica exata como referência

### Configurações do AG

- Configuração A: população 80, 250 gerações, mutação 0.02, torneio 3, elitismo 2, penalidade 1000
- Configuração B: população 120, 320 gerações, mutação 0.05, torneio 4, elitismo 4, penalidade 2000

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

### Análise

O guloso por razão foi melhor que o guloso por valor em todas as instâncias. O guloso por valor absoluto ficou mais distante da referência na instância oficial. O AG com a configuração A alcançou a solução ótima nas três instâncias; a configuração B ficou abaixo do ótimo na instância oficial, mas ainda acima dos gulosos. A programação dinâmica deu a referência exata e foi viável nas instâncias testadas.

### Gráfico do AG

Foram gerados gráficos de geração x melhor aptidão em `Q3/outputs` para cada instância.

## Reflexão final

- A questão que exigiu mais esforço foi a Mochila 0/1, porque envolveu várias técnicas, comparação entre abordagens e coleta de métricas.
- O resultado empírico mais marcante foi a diferença entre o guloso por valor absoluto e o guloso por razão na instância oficial.
- Eu melhoraria principalmente o algoritmo genético, testando mais configurações e adicionando uma estratégia de reparo mais refinada para indivíduos inviáveis.

## Uso de IA

Foi utilizado apoio de IA para os scripts de gráfico e para montar as tabelas com os testes.

