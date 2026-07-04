package paa;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class LisExperiment {
    private static final long SEED = 466473L;
    private static final int[] INITIAL_VECTOR = new int[] {
            32, 19, 32, 17, 31, 43, 30, 29, 54, 16, 28, 66, 15, 41, 65, 14, 50
    };
    private static final int[] SIZES = new int[] { 100, 250, 500, 1000, 2000, 4000 };

    private LisExperiment() {
    }

    public static List<ResultRow> run(Path root) throws IOException {
        Path q2Dir = root.resolve("Q2");
        Path outputsDir = q2Dir.resolve("outputs");
        Files.createDirectories(outputsDir);

        LisResult initial = solveLIS(INITIAL_VECTOR);
        writeAnalysisFile(q2Dir.resolve("analise_q2.txt"), initial);

        List<ResultRow> rows = new ArrayList<ResultRow>();
        List<Double> xValues = new ArrayList<Double>();
        List<Double> comparisonsSeries = new ArrayList<Double>();
        List<Double> timeSeries = new ArrayList<Double>();

        for (int n : SIZES) {
            int[] sequence = generateSequence(n, SEED);
            long start = System.nanoTime();
            LisResult result = solveLIS(sequence);
            long elapsedMs = Math.max(0L, (System.nanoTime() - start) / 1_000_000L);

            ResultRow row = new ResultRow()
                    .put("section", "Q2")
                    .put("question", "Subsequencia crescente")
                    .put("instance", "sequencia_aleatoria_semente")
                    .put("algorithm", "LIS-DP")
                    .put("configuration", "best/next de tras para frente")
                    .put("seed", SEED)
                    .put("n", n)
                    .put("comparisons", result.comparisons)
                    .put("updates", result.updates)
                    .put("time_ms", elapsedMs)
                    .put("lis_length", result.length())
                    .put("notes", "Sequencia gerada com Random(S) no intervalo [1, 10000].");
            rows.add(row);

            xValues.add((double) n);
            comparisonsSeries.add((double) result.comparisons);
            timeSeries.add((double) elapsedMs);
        }

        ChartUtil.saveLineChart(
                outputsDir.resolve("q2_comparacoes.png"),
                "Q2 - Numero de comparacoes por tamanho da sequencia",
                "n",
                "Comparacoes",
                Arrays.asList(new ChartUtil.Series(
                        "comparacoes",
                        toArray(xValues),
                        toArray(comparisonsSeries),
                        new Color(0, 102, 204))));

        ChartUtil.saveLineChart(
                outputsDir.resolve("q2_tempo.png"),
                "Q2 - Tempo por tamanho da sequencia",
                "n",
                "Tempo (ms)",
                Arrays.asList(new ChartUtil.Series(
                        "tempo",
                        toArray(xValues),
                        toArray(timeSeries),
                        new Color(204, 102, 0))));

        return rows;
    }

    private static int[] generateSequence(int n, long seed) {
        Random random = new Random(seed);
        int[] sequence = new int[n];
        for (int i = 0; i < n; i++) {
            sequence[i] = random.nextInt(10_000) + 1;
        }
        return sequence;
    }

    private static LisResult solveLIS(int[] values) {
        int n = values.length;
        int[] best = new int[n];
        int[] next = new int[n];
        Arrays.fill(best, 1);
        Arrays.fill(next, -1);

        long comparisons = 0L;
        long updates = 0L;

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (values[j] > values[i]) {
                    int candidate = best[j] + 1;
                    if (candidate > best[i]) {
                        best[i] = candidate;
                        next[i] = j;
                        updates++;
                    }
                }
            }
        }

        int start = 0;
        for (int i = 1; i < n; i++) {
            if (best[i] > best[start]) {
                start = i;
            }
        }

        List<Integer> subsequence = new ArrayList<Integer>();
        int current = start;
        while (current != -1) {
            subsequence.add(values[current]);
            current = next[current];
            updates++;
        }

        return new LisResult(best, next, subsequence, comparisons, updates);
    }

    private static void writeAnalysisFile(Path file, LisResult result) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("Questão 2 - Subsequência Crescente\n\n");
        builder.append("Semente usada: ").append(SEED).append("\n\n");
        builder.append("Recorrência usada:\n");
        builder.append("best[i] = 1 + max(best[j]) para j > i e v[j] > v[i], ou 1 se não existir tal j.\n");
        builder.append("next[i] guarda o próximo índice de uma subsequência ótima a partir de i.\n\n");
        builder.append("Vetor inicial v = ").append(Arrays.toString(INITIAL_VECTOR)).append("\n\n");
        builder.append("best = ").append(Arrays.toString(result.best)).append("\n");
        builder.append("next = ").append(formatNext(result.next)).append("\n");
        builder.append("Tamanho da maior subsequência crescente = ").append(result.length()).append("\n");
        builder.append("Uma subsequência máxima encontrada = ").append(result.subsequenceAsString()).append("\n\n");
        builder.append("Geração das sequências para n = 100, 250, 500, 1000, 2000, 4000:\n");
        builder.append("Para cada n, geramos os primeiros n inteiros de Random(S) no intervalo [1, 10000].\n");

        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String formatNext(int[] next) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < next.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            if (next[i] == -1) {
                builder.append(0);
            } else {
                builder.append(next[i] + 1);
            }
        }
        builder.append(']');
        return builder.toString();
    }

    private static double[] toArray(List<Double> values) {
        double[] array = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i);
        }
        return array;
    }

    private static final class LisResult {
        private final int[] best;
        private final int[] next;
        private final List<Integer> subsequence;
        private final long comparisons;
        private final long updates;

        private LisResult(int[] best, int[] next, List<Integer> subsequence, long comparisons, long updates) {
            this.best = best;
            this.next = next;
            this.subsequence = subsequence;
            this.comparisons = comparisons;
            this.updates = updates;
        }

        private int length() {
            int max = 0;
            for (int value : best) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }

        private String subsequenceAsString() {
            return subsequence.toString();
        }
    }
}
