from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import Image, PageBreak, Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle


ROOT = Path(__file__).resolve().parent
OUTPUT = ROOT / "P3_466473_0083466_0083473.pdf"


def add_table(story, headers, rows, col_widths=None):
    data = [headers] + rows
    table = Table(data, colWidths=col_widths)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#1f4e79")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("FONTSIZE", (0, 0), (-1, -1), 8),
                ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#9aa5b1")),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.whitesmoke, colors.HexColor("#eef3f8")]),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
                ("LEFTPADDING", (0, 0), (-1, -1), 4),
                ("RIGHTPADDING", (0, 0), (-1, -1), 4),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
            ]
        )
    )
    story.append(table)
    story.append(Spacer(1, 0.35 * cm))


def add_figure(story, styles, image_path, caption, width=16.5 * cm):
    img = Image(str(image_path))
    ratio = img.imageHeight / img.imageWidth
    img.drawWidth = width
    img.drawHeight = width * ratio
    story.append(Paragraph(caption, styles["SubSection"]))
    story.append(img)
    story.append(Spacer(1, 0.35 * cm))


def main():
    doc = SimpleDocTemplate(
        str(OUTPUT),
        pagesize=A4,
        rightMargin=1.4 * cm,
        leftMargin=1.4 * cm,
        topMargin=1.4 * cm,
        bottomMargin=1.4 * cm,
    )

    styles = getSampleStyleSheet()
    styles.add(ParagraphStyle(name="TitleCenter", parent=styles["Title"], alignment=TA_CENTER, fontName="Helvetica-Bold", fontSize=18, leading=22, spaceAfter=10))
    styles.add(ParagraphStyle(name="Section", parent=styles["Heading1"], fontName="Helvetica-Bold", fontSize=14, leading=18, textColor=colors.HexColor("#1f4e79"), spaceAfter=6))
    styles.add(ParagraphStyle(name="SubSection", parent=styles["Heading2"], fontName="Helvetica-Bold", fontSize=11, leading=14, textColor=colors.HexColor("#2f5d8a"), spaceAfter=4))
    styles.add(ParagraphStyle(name="BodySmall", parent=styles["BodyText"], fontName="Helvetica", fontSize=9, leading=12, spaceAfter=4))

    story = []

    story.append(Paragraph("P3 - Projeto e Análise de Algoritmos", styles["TitleCenter"]))
    story.append(Paragraph("Semente da dupla: 466473", styles["BodySmall"]))
    story.append(Paragraph("João Pedro Aguiar Silva - 0083466 | Victor Lopes Teodoro - 0083473", styles["BodySmall"]))
    story.append(Spacer(1, 0.2 * cm))

    story.append(Paragraph("Questão 2 - Subsequência Crescente", styles["Section"]))
    story.append(Paragraph("Recorrência: best[i] = 1 + max(best[j]) para j > i e v[j] > v[i], ou 1 se não existir tal j. O vetor next guarda o próximo índice de uma subsequência ótima a partir de cada posição.", styles["BodySmall"]))
    story.append(Paragraph("v = [32, 19, 32, 17, 31, 43, 30, 29, 54, 16, 28, 66, 15, 41, 65, 14, 50]", styles["BodySmall"]))
    story.append(Paragraph("best = [4, 5, 4, 5, 4, 3, 3, 3, 2, 4, 3, 1, 3, 2, 1, 2, 1]", styles["BodySmall"]))
    story.append(Paragraph("next = [6, 3, 6, 5, 6, 9, 9, 9, 12, 11, 14, 0, 14, 15, 0, 17, 0]", styles["BodySmall"]))
    story.append(Paragraph("Maior subsequência crescente: 5 | Exemplo encontrado: [19, 32, 43, 54, 66]", styles["BodySmall"]))
    story.append(Paragraph("As sequências de teste foram geradas com Random(466473) e os primeiros n inteiros no intervalo [1, 10000].", styles["BodySmall"]))

    add_table(
        story,
        ["n", "comparacoes", "tempo_ms", "tamanho LIS"],
        [
            ["100", "4950", "0", "16"],
            ["250", "31125", "1", "28"],
            ["500", "124750", "2", "43"],
            ["1000", "499500", "4", "58"],
            ["2000", "1999000", "16", "84"],
            ["4000", "7998000", "44", "121"],
        ],
        [2 * cm, 3.2 * cm, 2.4 * cm, 3 * cm],
    )

    add_figure(story, styles, ROOT / "Q2" / "outputs" / "q2_comparacoes.png", "Gráfico 1 - Número de comparações por tamanho da sequência")
    add_figure(story, styles, ROOT / "Q2" / "outputs" / "q2_tempo.png", "Gráfico 2 - Tempo por tamanho da sequência")

    story.append(Paragraph("Questão 3 - Mochila 0/1", styles["Section"]))
    story.append(Paragraph("Instâncias: 20 itens, 50 itens e a instância oficial knapPI_1_100_1000_1. Métodos: guloso por razão, guloso por valor, AG com duas configurações e DP exata como referência.", styles["BodySmall"]))
    story.append(Paragraph("Configurações do AG: A = pop 80, 250 gerações, mutação 0.02, torneio 3, elitismo 2, penalidade 1000; B = pop 120, 320 gerações, mutação 0.05, torneio 4, elitismo 4, penalidade 2000.", styles["BodySmall"]))

    story.append(Paragraph("Resumo por instancia", styles["SubSection"]))
    add_table(
        story,
        ["Instancia", "Otimo DP", "Guloso razao", "Guloso valor", "GA A", "GA B"],
        [
            ["instancia_20", "3878", "3878", "3454", "3878", "3878"],
            ["instancia_50", "10693", "10693", "10234", "10693", "10693"],
            ["knapPI_1_100_1000_1", "9147", "8817", "5429", "9147", "7363"],
        ],
        [3.1 * cm, 2.0 * cm, 2.3 * cm, 2.3 * cm, 2.0 * cm, 2.0 * cm],
    )

    add_figure(story, styles, ROOT / "Q3" / "outputs" / "instancia_20_ga.png", "Gráfico 3 - AG na instância instancia_20")
    add_figure(story, styles, ROOT / "Q3" / "outputs" / "instancia_50_ga.png", "Gráfico 4 - AG na instância instancia_50")
    add_figure(story, styles, ROOT / "Q3" / "outputs" / "knapPI_1_100_1000_1_ga.png", "Gráfico 5 - AG na instância knapPI_1_100_1000_1")

    story.append(Paragraph("Análise: o guloso por razão foi melhor que o guloso por valor em todas as instâncias. O AG com a configuração A atingiu o ótimo nas três instâncias. A configuração B ficou abaixo do ótimo na instância oficial, mas ainda acima dos gulosos. A DP deu a referência exata e foi viável nas instâncias testadas.", styles["BodySmall"]))
    story.append(Paragraph("Reflexão final: a questão que exigiu mais esforço foi a Mochila 0/1, o resultado empírico mais marcante foi a diferença entre os dois gulosos na instância oficial e a principal melhoria futura seria refinar o AG com mais testes de parâmetros e um reparo de indivíduos mais forte.", styles["BodySmall"]))
    story.append(Paragraph("Uso de IA: apoio para organizar o código, revisar o relatório e ajudar na montagem dos scripts e tabelas.", styles["BodySmall"]))

    doc.build(story)
    print(f"PDF gerado em: {OUTPUT}")


if __name__ == "__main__":
    main()
