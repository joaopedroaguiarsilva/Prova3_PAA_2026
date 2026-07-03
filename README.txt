Projeto - PAA 3a avaliacao

Semente da dupla: 466473
Nomes e matriculas:
Joao Pedro Aguiar Silva - 0083466
Victor Lopes Teodoro - 0083473

Estrutura:
- src\paa\*.java: codigo-fonte em Java
- Q2: analise, graficos e saidas da questao 2
- Q3: analise, instancias e graficos da questao 3

Como compilar no Windows PowerShell:
1. Criar a pasta de saida, se necessario:
   New-Item -ItemType Directory -Force out
2. Compilar:
   $files = Get-ChildItem -Recurse -Path src\paa -Filter *.java | ForEach-Object { $_.FullName }
   javac -encoding UTF-8 -d out $files

Como executar:
- Apenas Q2:
  java -cp out paa.Main q2
- Apenas Q3:
  java -cp out paa.Main q3
- Tudo:
  java -cp out paa.Main all

Saidas geradas:
- results.csv na raiz do projeto
- Q2\analise_q2.txt, Q2\outputs\q2_comparacoes.png, Q2\outputs\q2_tempo.png
- Q3\analise_q3.txt, Q3\instancias\*, Q3\outputs\*_ga.png

Observacao:
- A Q1 fica reservada para a outra pessoa da dupla e deve ser adicionada no mesmo padrao de organizacao.
