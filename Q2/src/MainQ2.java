package src;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class MainQ2 {
    private static final int S = 466473;

    private static final Path PASTA_BASE = definirPastaBase();
    private static final Path PASTA_RESULTADOS = PASTA_BASE.resolve("resultados");
    private static final Path CAMINHO_CSV = PASTA_RESULTADOS.resolve("resultados.csv");

    public static void main(String[] args) {
        try {

            criarPastaResultados();

            try (PrintWriter csv = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(CAMINHO_CSV.toFile()),
                            StandardCharsets.UTF_8))) {

                escreverCabecalhoCSV(csv);

                executarVetorDoEnunciado();

                executarTesteGerado(100, csv);
                executarTesteGerado(250, csv);
                executarTesteGerado(500, csv);
                executarTesteGerado(1000, csv);
                executarTesteGerado(2000, csv);
                executarTesteGerado(4000, csv);

                System.out.println("Arquivo CSV gerado em: " + CAMINHO_CSV.toAbsolutePath());
            }

        } catch (IOException e) {
            System.out.println("Erro ao gerar CSV.");
            e.printStackTrace();
        }
    }

    private static void executarVetorDoEnunciado() {
        int[] vetor = {32, 19, 32, 17, 31, 43, 30, 29, 54, 16, 28, 66, 15, 41, 65, 14, 50};

        SubsequenciaCrescente sc = new SubsequenciaCrescente(vetor);

        sc.resolver();

        System.out.println("=========================================");
        System.out.println("VETOR FIXO DO ENUNCIADO");
        System.out.println("=========================================");

        sc.imprimirResultadoCompleto();
    }

    private static void executarTesteGerado(int n, PrintWriter csv) {
        int[] vetor = gerarSequencia(n);

        SubsequenciaCrescente sc = new SubsequenciaCrescente(vetor);

        sc.resolver();

        EstatisticasPD e = sc.getEstatisticas();

        System.out.println("=========================================");
        System.out.println("n = " + n);
        System.out.println("Tamanho da LIS: " + sc.getTamanhoMaior());
        System.out.println("Comparações: " + e.comparacoes);
        System.out.println("Atualizações: " + e.atualizacoes);
        System.out.println("Tempo: " + e.tempoMs + " ms");

        salvarCSV(csv, n, e, sc.getTamanhoMaior());

        csv.flush();
    }

    private static int[] gerarSequencia(int n) {
        int[] vetor = new int[n];

        Random random = new Random(S);

        for (int i = 0; i < n; i++) {
            vetor[i] = random.nextInt(10000);
        }

        return vetor;
    }

    private static void escreverCabecalhoCSV(PrintWriter csv) {
        csv.println("S,n,comparacoes,atualizacoes,tempo_ms,tamanho_subsequencia");
    }

    private static void salvarCSV(PrintWriter csv, int n, EstatisticasPD e, int tamanhoSubsequencia) {

        csv.println(S + "," + n + "," + e.comparacoes + "," + e.atualizacoes + "," + e.tempoMs + "," + tamanhoSubsequencia);
    }

    private static Path definirPastaBase() {
        Path atual = Paths.get("").toAbsolutePath().normalize();

        if (atual.getFileName() != null && atual.getFileName().toString().equalsIgnoreCase("Q2")) {
            return atual;
        }

        Path possivelQ2 = atual.resolve("Q2");

        if (Files.exists(possivelQ2) && Files.isDirectory(possivelQ2)) {
            return possivelQ2;
        }

        return atual;
    }

    private static void criarPastaResultados() throws IOException {
        Files.createDirectories(PASTA_RESULTADOS);
    }
}