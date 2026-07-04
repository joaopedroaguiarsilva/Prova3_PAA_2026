import csv
import os
from pathlib import Path
import matplotlib.pyplot as plt

PASTA_BASE = Path(__file__).parent

PASTA_RESULTADOS = PASTA_BASE / "resultados"
CAMINHO_CSV = PASTA_RESULTADOS / "resultados.csv"

os.makedirs(PASTA_RESULTADOS, exist_ok=True)

dados = []

with open(CAMINHO_CSV, mode="r", encoding="utf-8") as arquivo:
    leitor = csv.DictReader(arquivo)

    for linha in leitor:
        dados.append({
            "n": int(linha["n"]),
            "comparacoes": int(linha["comparacoes"]),
            "tempo_ms": float(linha["tempo_ms"])
        })

dados.sort(key=lambda item: item["n"])

# Gráfico n x comparações
plt.figure()

plt.plot(
    [item["n"] for item in dados],
    [item["comparacoes"] for item in dados],
    marker="o"
)

plt.xlabel("Tamanho da sequência (n)")
plt.ylabel("Comparações")
plt.title("n x comparações")
plt.grid(True)
plt.savefig(PASTA_RESULTADOS / "grafico_comparacoes.png")
plt.close()

# Gráfico n x tempo
plt.figure()

plt.plot(
    [item["n"] for item in dados],
    [item["tempo_ms"] for item in dados],
    marker="o"
)

plt.xlabel("Tamanho da sequência (n)")
plt.ylabel("Tempo (ms)")
plt.title("n x tempo de execução")
plt.grid(True)
plt.savefig(PASTA_RESULTADOS / "grafico_tempo.png")
plt.close()

print("Gráficos gerados com sucesso.")
print(f"Arquivo lido: {CAMINHO_CSV}")
print(f"Gráficos salvos em: {PASTA_RESULTADOS}")