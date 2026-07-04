package src;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainQ1 {

    private static final int S = 466473;

    private static final Path PASTA_BASE = definirPastaBase();
    private static final Path PASTA_RESULTADOS = PASTA_BASE.resolve("resultados");
    private static final Path CAMINHO_CSV = PASTA_RESULTADOS.resolve("resultados.csv");

    public static void main(String[] args) {

        try {

            criarPastaResultados();

            try (PrintWriter csv = new PrintWriter(new OutputStreamWriter(new FileOutputStream(CAMINHO_CSV.toFile()), StandardCharsets.UTF_8))) {

                escreverCabecalhoCSV(csv);

                executarTeste(5, false, "principal", csv);
                executarTeste(5, true, "principal", csv);

                executarTeste(6, false, "principal", csv);
                executarTeste(6, true, "principal", csv);

                executarTeste8x8(csv);

                System.out.println("Arquivo CSV gerado em: " + CAMINHO_CSV.toAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Erro ao gerar o arquivo CSV.");
            e.printStackTrace();
        }
    }

    private static Path definirPastaBase() {

        Path atual = Paths.get("").toAbsolutePath().normalize();

        if (atual.getFileName() != null && atual.getFileName().toString().equalsIgnoreCase("Q1")) {
            return atual;
        }

        Path possivelQ1 = atual.resolve("Q1");

        if (Files.exists(possivelQ1) && Files.isDirectory(possivelQ1)) {
            return possivelQ1;
        }

        return atual;
    }

    private static void criarPastaResultados() throws IOException {
        Files.createDirectories(PASTA_RESULTADOS);
    }

    private static void escreverCabecalhoCSV(PrintWriter csv) {

        csv.println(
                "S," +
                "n," +
                "teste," +
                "linha," +
                "coluna," +
                "casa_inicial," +
                "estrategia," +
                "solucao," +
                "chamadas," +
                "tentativas," +
                "backtracks," +
                "tempo_ms"
        );
    }

    private static void executarTeste(
            int n,
            boolean heuristica,
            String nomeTeste,
            PrintWriter csv) {

        int linha = (S % n) + 1;
        int coluna = ((S / n) % n) + 1;

        executarTesteComCasa(n, linha, coluna, heuristica, nomeTeste, csv);
    }

    private static void executarTeste8x8(PrintWriter csv) {
        executarTeste(8, false, "principal", csv);
        executarTeste(8, true, "principal", csv);

        int linha1 = (S % 8) + 1;
        int coluna1 = ((S / 8) % 8) + 1;

        int linha2 = ((S / 64) % 8) + 1;
        int coluna2 = ((S / 512) % 8) + 1;

        if (linha1 == linha2 && coluna1 == coluna2) {

            linha2 = 1;
            coluna2 = 1;

            if (linha1 == 1 && coluna1 == 1) {
                linha2 = 1;
                coluna2 = 2;
            }
        }

        executarTesteComCasa(8, linha2, coluna2, true, "segundo_8x8", csv);
    }

    private static void executarTesteComCasa(int n, int linha, int coluna, boolean heuristica, String nomeTeste, PrintWriter csv) {
        PasseioCavalo passeio = new PasseioCavalo(n, heuristica);

        boolean encontrou = passeio.resolver(linha, coluna);

        imprimirResultado(n, linha, coluna, heuristica, encontrou, passeio);

        salvarResultadoCSV(csv, n, nomeTeste, linha, coluna, heuristica, encontrou, passeio);

        csv.flush();
    }

    private static void imprimirResultado(int n, int linha, int coluna, boolean heuristica, boolean encontrou, PasseioCavalo passeio) {
        Estatisticas e = passeio.getEstatisticas();

        System.out.println("=========================================");
        System.out.println("Tabuleiro: " + n + " x " + n);
        System.out.println("Casa inicial: (" + linha + ", " + coluna + ")");
        System.out.println("Estratégia: " + (heuristica ? "Warnsdorff" : "Backtracking puro"));

        System.out.println("Solução encontrada: " + (encontrou ? "SIM" : "NÃO"));

        System.out.println("Chamadas recursivas: " + e.chamadas);
        System.out.println("Tentativas: " + e.tentativas);
        System.out.println("Backtracks: " + e.backtracks);
        System.out.println("Tempo: " + e.tempo + " ms");
        System.out.println("Tempo esgotado: " + (e.tempoEsgotado ? "SIM" : "NÃO"));

        if (encontrou) {
            passeio.imprimirTabuleiro();
        }

        System.out.println();
    }

    private static void salvarResultadoCSV(PrintWriter csv, int n, String nomeTeste, int linha, int coluna, boolean heuristica, boolean encontrou, PasseioCavalo passeio) {

        Estatisticas e = passeio.getEstatisticas();

        String casaInicial = "(" + linha + "," + coluna + ")";
        String estrategia = heuristica ? "Warnsdorff" : "Backtracking puro";
        String solucao = encontrou ? "SIM" : "NAO";

        csv.println(
                S + "," +
                n + "," +
                formatarCSV(nomeTeste) + "," +
                linha + "," +
                coluna + "," +
                formatarCSV(casaInicial) + "," +
                formatarCSV(estrategia) + "," +
                formatarCSV(solucao) + "," +
                e.chamadas + "," +
                e.tentativas + "," +
                e.backtracks + "," +
                e.tempo
        );
    }

    private static String formatarCSV(String valor) {
        String valorTratado = valor.replace("\"", "\"\"");

        return "\"" + valorTratado + "\"";
    }
}