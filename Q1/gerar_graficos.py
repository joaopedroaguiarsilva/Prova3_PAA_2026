import csv
import os
from pathlib import Path
import matplotlib.pyplot as plt

# Pega a pasta onde está este arquivo Python
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
            "estrategia": linha["estrategia"],
            "tempo_ms": float(linha["tempo_ms"]),
            "chamadas": int(linha["chamadas"])
        })

estrategias = sorted(set(item["estrategia"] for item in dados))

# Gráfico n x tempo
plt.figure()

for estrategia in estrategias:
    dados_estrategia = [
        item for item in dados
        if item["estrategia"] == estrategia
    ]

    dados_estrategia.sort(key=lambda item: item["n"])

    plt.plot(
        [item["n"] for item in dados_estrategia],
        [item["tempo_ms"] for item in dados_estrategia],
        marker="o",
        label=estrategia
    )

plt.xlabel("Tamanho do tabuleiro (n)")
plt.ylabel("Tempo (ms)")
plt.title("n x tempo de execução")
plt.legend()
plt.grid(True)
plt.savefig(PASTA_RESULTADOS / "grafico_tempo.png")
plt.close()

# Gráfico n x chamadas recursivas
plt.figure()

for estrategia in estrategias:
    dados_estrategia = [
        item for item in dados
        if item["estrategia"] == estrategia
    ]

    dados_estrategia.sort(key=lambda item: item["n"])

    plt.plot(
        [item["n"] for item in dados_estrategia],
        [item["chamadas"] for item in dados_estrategia],
        marker="o",
        label=estrategia
    )

plt.xlabel("Tamanho do tabuleiro (n)")
plt.ylabel("Chamadas recursivas")
plt.title("n x chamadas recursivas")
plt.legend()
plt.grid(True)
plt.savefig(PASTA_RESULTADOS / "grafico_chamadas.png")
plt.close()

print("Gráficos gerados com sucesso.")
print(f"Arquivo lido: {CAMINHO_CSV}")
print(f"Gráficos salvos em: {PASTA_RESULTADOS}")