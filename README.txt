Prova 3 - Projeto e Análise de Algoritmos

============================================================
IDENTIFICAÇÃO
============================================================

João Pedro Aguiar Silva - 0083466
Victor Lopes Teodoro - 0083473

Semente utilizada:
S = 466473

A semente S foi formada pela concatenação dos três últimos dígitos
das matrículas dos dois alunos, em ordem alfabética dos nomes.

Uso de IA:
Foi utilizada IA generativa como apoio no desenvolvimento, organização
do código, geração dos scripts de gráficos e revisão textual. A dupla
revisou e compreende o conteúdo entregue.

============================================================
ESTRUTURA DO PROJETO
============================================================

Na raiz do arquivo compactado:

README.txt
resultados.csv
Q1/
Q2/
Q3/

Cada questão possui sua própria pasta com códigos, resultados e scripts
para geração dos gráficos.

Q1:
- Backtracking - Passeio do Cavalo
- Código Java em Q1/src
- Resultados em Q1/resultados
- Script de gráficos: Q1/gerar_graficos.py

Q2:
- Programação Dinâmica - Subsequência Crescente
- Código Java em Q2/src
- Resultados em Q2/resultados
- Script de gráficos: Q2/gerar_graficos.py

Q3:
- Mochila 0/1
- Código Java em Q3/src
- Entrada oficial em Q3/dados
- Resultados em Q3/resultados
- Script de gráficos: Q3/gerar_graficos.py

============================================================
REQUISITOS
============================================================

Java JDK instalado.

Python 3 instalado.

Biblioteca matplotlib instalada para gerar os gráficos:

python -m pip install matplotlib

============================================================
COMO EXECUTAR A Q1
============================================================

A partir da raiz do projeto:

cd Q1
javac -d bin src\*.java
java -cp bin src.MainQ1
python gerar_graficos.py

Saídas esperadas:

Q1/resultados/resultados.csv
Q1/resultados/grafico_tempo.png
Q1/resultados/grafico_chamadas.png

============================================================
COMO EXECUTAR A Q2
============================================================

A partir da raiz do projeto:

cd Q2
javac -d bin src\*.java
java -cp bin src.MainQ2
python gerar_graficos.py

Saídas esperadas:

Q2/resultados/resultados.csv
Q2/resultados/grafico_comparacoes.png
Q2/resultados/grafico_tempo.png

============================================================
COMO EXECUTAR A Q3
============================================================

Antes de executar, verifique se a instância oficial está em:

Q3/dados/knapPI_1_100_1000_1

A partir da raiz do projeto:

cd Q3
javac -d bin src\*.java
java -cp bin src.MainQ3
python gerar_graficos.py

Saídas esperadas:

Q3/resultados/resultados.csv
Q3/resultados/ag_historico.csv
Q3/resultados/grafico_valores.png
Q3/resultados/grafico_gap_percentual.png
Q3/resultados/grafico_ag_aptidao.png

============================================================
RESULTADOS
============================================================

Cada questão gera seu próprio arquivo resultados.csv dentro da pasta
correspondente.

Além disso, há um resultados.csv na raiz do projeto com um resumo
consolidado dos principais resultados.

Os resultados completos, tabelas, gráficos e discussões estão no PDF.