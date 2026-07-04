import csv
import os
from pathlib import Path
import matplotlib.pyplot as plt


PASTA_BASE = Path(__file__).parent
PASTA_RESULTADOS = PASTA_BASE / "resultados"

CAMINHO_RESULTADOS = PASTA_RESULTADOS / "resultados.csv"
CAMINHO_HISTORICO_AG = PASTA_RESULTADOS / "ag_historico.csv"

os.makedirs(PASTA_RESULTADOS, exist_ok=True)


def ler_resultados():
    dados = []

    with open(CAMINHO_RESULTADOS, mode="r", encoding="utf-8") as arquivo:
        leitor = csv.DictReader(arquivo)

        for linha in leitor:
            dados.append({
                "instancia": linha["instancia"],
                "n": int(linha["n"]),
                "metodo": linha["metodo"],
                "configuracao": linha["configuracao"],
                "valor": int(linha["valor"]),
                "peso": int(linha["peso"]),
                "tempo_ms": float(linha["tempo_ms"]),
                "Vref": int(linha["Vref"]),
                "gap": int(linha["gap"]),
                "gap_percentual": float(linha["gap_percentual"])
            })

    return dados


def ler_historico_ag():
    dados = []

    if not CAMINHO_HISTORICO_AG.exists():
        return dados

    with open(CAMINHO_HISTORICO_AG, mode="r", encoding="utf-8") as arquivo:
        leitor = csv.DictReader(arquivo)

        for linha in leitor:
            dados.append({
                "instancia": linha["instancia"],
                "configuracao": linha["configuracao"],
                "geracao": int(linha["geracao"]),
                "melhor_aptidao": float(linha["melhor_aptidao"]),
                "melhor_valor": int(linha["melhor_valor"]),
                "melhor_peso": int(linha["melhor_peso"])
            })

    return dados


def nome_metodo(dado):
    metodo = dado["metodo"]
    configuracao = dado["configuracao"]

    if metodo == "Algoritmo Genético":
        return "AG " + configuracao

    if metodo == "Programação Dinâmica":
        return "PD"

    return metodo


def gerar_grafico_valores(dados):
    instancias = sorted(set(d["instancia"] for d in dados))
    metodos = []

    for d in dados:
        nome = nome_metodo(d)

        if nome not in metodos:
            metodos.append(nome)

    largura = 0.12
    posicoes = list(range(len(instancias)))

    plt.figure(figsize=(12, 6))

    for indice_metodo, metodo in enumerate(metodos):
        valores = []

        for instancia in instancias:
            encontrado = None

            for d in dados:
                if d["instancia"] == instancia and nome_metodo(d) == metodo:
                    encontrado = d
                    break

            if encontrado is None:
                valores.append(0)
            else:
                valores.append(encontrado["valor"])

        deslocamento = (indice_metodo - len(metodos) / 2) * largura + largura / 2
        x = [p + deslocamento for p in posicoes]

        plt.bar(x, valores, width=largura, label=metodo)

    plt.xlabel("Instância")
    plt.ylabel("Valor obtido")
    plt.title("Comparação do valor obtido por método")
    plt.xticks(posicoes, instancias)
    plt.legend()
    plt.grid(axis="y")
    plt.tight_layout()
    plt.savefig(PASTA_RESULTADOS / "grafico_valores.png")
    plt.close()


def gerar_grafico_gap_percentual(dados):
    instancias = sorted(set(d["instancia"] for d in dados))
    metodos = []

    for d in dados:
        nome = nome_metodo(d)

        if nome not in metodos:
            metodos.append(nome)

    largura = 0.12
    posicoes = list(range(len(instancias)))

    plt.figure(figsize=(12, 6))

    for indice_metodo, metodo in enumerate(metodos):
        gaps = []

        for instancia in instancias:
            encontrado = None

            for d in dados:
                if d["instancia"] == instancia and nome_metodo(d) == metodo:
                    encontrado = d
                    break

            if encontrado is None:
                gaps.append(0)
            else:
                gaps.append(encontrado["gap_percentual"])

        deslocamento = (indice_metodo - len(metodos) / 2) * largura + largura / 2
        x = [p + deslocamento for p in posicoes]

        plt.bar(x, gaps, width=largura, label=metodo)

    plt.xlabel("Instância")
    plt.ylabel("Gap percentual (%)")
    plt.title("Gap percentual em relação ao valor ótimo")
    plt.xticks(posicoes, instancias)
    plt.legend()
    plt.grid(axis="y")
    plt.tight_layout()
    plt.savefig(PASTA_RESULTADOS / "grafico_gap_percentual.png")
    plt.close()


def gerar_grafico_ag_aptidao(dados_ag):
    if not dados_ag:
        print("Histórico do AG não encontrado. Gráfico do AG não foi gerado.")
        return

    series = sorted(set(
        (d["instancia"], d["configuracao"])
        for d in dados_ag
    ))

    plt.figure(figsize=(12, 6))

    for instancia, configuracao in series:
        dados_filtrados = [
            d for d in dados_ag
            if d["instancia"] == instancia and d["configuracao"] == configuracao
        ]

        dados_filtrados.sort(key=lambda d: d["geracao"])

        geracoes = [d["geracao"] for d in dados_filtrados]
        aptidoes = [d["melhor_aptidao"] for d in dados_filtrados]

        label = instancia + " - " + configuracao

        plt.plot(geracoes, aptidoes, label=label)

    plt.xlabel("Geração")
    plt.ylabel("Melhor aptidão")
    plt.title("Algoritmo Genético: geração x melhor aptidão")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(PASTA_RESULTADOS / "grafico_ag_aptidao.png")
    plt.close()


def main():
    dados = ler_resultados()
    dados_ag = ler_historico_ag()

    gerar_grafico_valores(dados)
    gerar_grafico_gap_percentual(dados)
    gerar_grafico_ag_aptidao(dados_ag)

    print("Gráficos gerados com sucesso.")
    print(f"Arquivo lido: {CAMINHO_RESULTADOS}")
    print(f"Histórico AG lido: {CAMINHO_HISTORICO_AG}")
    print(f"Gráficos salvos em: {PASTA_RESULTADOS}")


if __name__ == "__main__":
    main()