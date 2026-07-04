package paa;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class KnapsackExperiment {
    private static final long SEED = 466473L;
    private static final String OFFICIAL_URL = "https://raw.githubusercontent.com/iqbalbasyar/swarm-intelligence/refs/heads/master/Bat%20Algorithm/Bat%20Algortihm%20on%20Solving%20Knapsack%20Problem/data/large_scale/knapPI_1_100_1000_1";

    private KnapsackExperiment() {
    }

    public static List<ResultRow> run(Path root) throws IOException {
        Path q3Dir = root.resolve("Q3");
        Path instancesDir = q3Dir.resolve("instancias");
        Path outputsDir = q3Dir.resolve("outputs");
        Files.createDirectories(instancesDir);
        Files.createDirectories(outputsDir);

        List<ResultRow> rows = new ArrayList<ResultRow>();
        StringBuilder summary = new StringBuilder();
        summary.append("Questão 3 - Mochila 0/1\n\n");
        summary.append("Semente usada: ").append(SEED).append("\n");
        summary.append("Instâncias geradas: pesos inteiros em [1, 100], valores inteiros em [1, 500] e capacidade floor(0,5 * soma dos pesos).\n\n");
        summary.append("Algoritmo genético:\n");
        summary.append("- representação: vetor booleano por item\n");
        summary.append("- fitness: valor - penalidade * excesso de peso\n");
        summary.append("- seleção: torneio\n");
        summary.append("- cruzamento: ponto único\n");
        summary.append("- mutação: troca de bit\n");
        summary.append("- duas configurações executadas\n\n");

        List<KnapsackInstance> instances = new ArrayList<KnapsackInstance>();
        instances.add(generateInstance("instancia_20", 20, SEED, instancesDir.resolve("instancia_20.txt")));
        instances.add(generateInstance("instancia_50", 50, SEED, instancesDir.resolve("instancia_50.txt")));
        instances.add(loadOfficialInstance("knapPI_1_100_1000_1", instancesDir.resolve("knapPI_1_100_1000_1.txt")));

        for (KnapsackInstance instance : instances) {
            summary.append(instance.name)
                    .append(": n=").append(instance.size())
                    .append(", capacidade=").append(instance.capacity)
                    .append("\n");
        }
        summary.append('\n');

        for (KnapsackInstance instance : instances) {
            ExactResult optimum = solveDynamicProgramming(instance);

            ResultRow greedyRatioRow = buildGreedyRow(instance, optimum.value, true);
            ResultRow greedyValueRow = buildGreedyRow(instance, optimum.value, false);

            GAConfig configA = new GAConfig("A", 80, 250, 0.02, 3, 2, 1000L);
            GAConfig configB = new GAConfig("B", 120, 320, 0.05, 4, 4, 2000L);
            GAResult gaA = solveGeneticAlgorithm(instance, configA);
            GAResult gaB = solveGeneticAlgorithm(instance, configB);

            ResultRow gaRowA = buildGARow(instance, optimum.value, configA, gaA);
            ResultRow gaRowB = buildGARow(instance, optimum.value, configB, gaB);
            ResultRow dpRow = buildDPRow(instance, optimum.value, optimum);

            rows.add(greedyRatioRow);
            rows.add(greedyValueRow);
            rows.add(gaRowA);
            rows.add(gaRowB);
            rows.add(dpRow);

            summary.append("Referência ótima para ").append(instance.name).append(": ").append(optimum.value).append("\n");
            summary.append("  Guloso razão: valor=").append(greedyRatioRow.get("value")).append(", peso=").append(greedyRatioRow.get("weight")).append("\n");
            summary.append("  Guloso valor: valor=").append(greedyValueRow.get("value")).append(", peso=").append(greedyValueRow.get("weight")).append("\n");
            summary.append("  GA A: melhor valor=").append(gaA.bestFeasible.value).append("\n");
            summary.append("  GA B: melhor valor=").append(gaB.bestFeasible.value).append("\n");
            summary.append("  DP: valor=").append(optimum.value).append("\n\n");

            saveGARunChart(outputsDir, instance, gaA, gaB);
        }

        Files.write(q3Dir.resolve("analise_q3.txt"), summary.toString().getBytes(StandardCharsets.UTF_8));
        return rows;
    }

    private static ResultRow buildGreedyRow(KnapsackInstance instance, long optimumValue, boolean ratio) {
        long start = System.nanoTime();
        GreedyResult result = ratio ? solveGreedyByRatio(instance) : solveGreedyByValue(instance);
        long elapsedMs = Math.max(0L, (System.nanoTime() - start) / 1_000_000L);
        return new ResultRow()
                .put("section", "Q3")
                .put("question", "Mochila 0/1")
                .put("instance", instance.name)
                .put("algorithm", ratio ? "Guloso-razão" : "Guloso-valor")
                .put("configuration", ratio ? "maior valor/peso" : "maior valor absoluto")
                .put("seed", SEED)
                .put("n", instance.size())
                .put("capacity", instance.capacity)
                .put("value", result.value)
                .put("weight", result.weight)
                .put("items", result.itemsAsString())
                .put("comparisons", result.comparisons)
                .put("updates", result.updates)
                .put("time_ms", elapsedMs)
                .put("gap_abs", optimumValue - result.value)
                .put("gap_percent", percentGap(optimumValue, result.value))
                .put("notes", result.note);
    }

    private static ResultRow buildGARow(KnapsackInstance instance, long optimumValue, GAConfig config, GAResult result) {
        return new ResultRow()
                .put("section", "Q3")
                .put("question", "Mochila 0/1")
                .put("instance", instance.name)
                .put("algorithm", "Genético")
                .put("configuration", config.name)
                .put("seed", SEED)
                .put("n", instance.size())
                .put("capacity", instance.capacity)
                .put("value", result.bestFeasible.value)
                .put("weight", result.bestFeasible.weight)
                .put("items", result.bestFeasible.itemsAsString())
                .put("comparisons", result.comparisons)
                .put("updates", result.updates)
                .put("time_ms", result.timeMs)
                .put("gap_abs", optimumValue - result.bestFeasible.value)
                .put("gap_percent", percentGap(optimumValue, result.bestFeasible.value))
                .put("notes", "pop=" + config.populationSize + ", geracoes=" + config.generations + ", mutacao=" + formatDouble(config.mutationRate) + ", selecao=torneio, cruzamento=ponto unico, penalidade=" + config.penaltyFactor);
    }

    private static ResultRow buildDPRow(KnapsackInstance instance, long optimumValue, ExactResult result) {
        return new ResultRow()
                .put("section", "Q3")
                .put("question", "Mochila 0/1")
                .put("instance", instance.name)
                .put("algorithm", "Programacao-dinamica")
                .put("configuration", "solução exata")
                .put("seed", SEED)
                .put("n", instance.size())
                .put("capacity", instance.capacity)
                .put("value", result.value)
                .put("weight", result.weight)
                .put("items", result.itemsAsString())
                .put("comparisons", result.comparisons)
                .put("updates", result.updates)
                .put("time_ms", result.timeMs)
                .put("gap_abs", optimumValue - result.value)
                .put("gap_percent", percentGap(optimumValue, result.value))
                .put("notes", "referência ótima; tabela 2D para reconstrução das escolhas");
    }

    private static void saveGARunChart(Path outputsDir, KnapsackInstance instance, GAResult gaA, GAResult gaB) throws IOException {
        double[] xA = generationArray(gaA.bestFitnessByGeneration.length);
        double[] xB = generationArray(gaB.bestFitnessByGeneration.length);
        ChartUtil.saveLineChart(
                outputsDir.resolve(instance.name + "_ga.png"),
                "GA - melhor aptidão por geração - " + instance.name,
                "Geração",
                "Melhor aptidão",
                Arrays.asList(
                        new ChartUtil.Series("config A", xA, gaA.bestFitnessByGeneration, new Color(0, 102, 204)),
                        new ChartUtil.Series("config B", xB, gaB.bestFitnessByGeneration, new Color(204, 102, 0))));
    }

    private static double[] generationArray(int length) {
        double[] generations = new double[length];
        for (int i = 0; i < length; i++) {
            generations[i] = i + 1;
        }
        return generations;
    }

    private static double percentGap(long reference, long value) {
        if (reference <= 0) {
            return 0.0;
        }
        return ((double) (reference - value) * 100.0) / (double) reference;
    }

    private static String formatDouble(double value) {
        return String.format(Locale.US, "%.4f", value);
    }

    private static KnapsackInstance generateInstance(String name, int n, long seed, Path file) throws IOException {
        Random random = new Random(seed);
        int[] values = new int[n];
        int[] weights = new int[n];
        int totalWeight = 0;
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt(500) + 1;
            weights[i] = random.nextInt(100) + 1;
            totalWeight += weights[i];
        }

        int capacity = (int) Math.floor(totalWeight * 0.5);
        StringBuilder builder = new StringBuilder();
        builder.append(n).append(' ').append(capacity).append('\n');
        for (int i = 0; i < n; i++) {
            builder.append(values[i]).append(' ').append(weights[i]).append('\n');
        }

        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
        return new KnapsackInstance(name, values, weights, capacity);
    }

    private static KnapsackInstance loadOfficialInstance(String name, Path file) throws IOException {
        if (!Files.exists(file)) {
            downloadOfficialInstance(file);
        }

        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        try {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Arquivo oficial vazio: " + file);
            }
            String[] header = headerLine.trim().split("\\s+");
            int n = Integer.parseInt(header[0]);
            int capacity = Integer.parseInt(header[1]);
            int[] values = new int[n];
            int[] weights = new int[n];
            for (int i = 0; i < n; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Arquivo oficial incompleto: itens insuficientes.");
                }
                String[] parts = line.trim().split("\\s+");
                values[i] = Integer.parseInt(parts[0]);
                weights[i] = Integer.parseInt(parts[1]);
            }
            return new KnapsackInstance(name, values, weights, capacity);
        } finally {
            reader.close();
        }
    }

    private static void downloadOfficialInstance(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OFFICIAL_URL))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new IOException("Falha ao baixar instancia oficial. HTTP " + response.statusCode());
            }
            Files.write(file, response.body().getBytes(StandardCharsets.UTF_8));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Download interrompido.", exception);
        }
    }

    private static GreedyResult solveGreedyByRatio(KnapsackInstance instance) {
        SortResult sortResult = sortByRatio(instance);
        long comparisons = sortResult.comparisons;
        long updates = 0L;
        int weight = 0;
        long value = 0L;
        List<Integer> chosen = new ArrayList<Integer>();
        for (Item item : sortResult.items) {
            comparisons++;
            if (weight + item.weight <= instance.capacity) {
                weight += item.weight;
                value += item.value;
                chosen.add(item.index + 1);
                updates++;
            }
        }
        return new GreedyResult(value, weight, chosen, comparisons, updates, "ordenacao por maior valor/peso");
    }

    private static GreedyResult solveGreedyByValue(KnapsackInstance instance) {
        SortResult sortResult = sortByValue(instance);
        long comparisons = sortResult.comparisons;
        long updates = 0L;
        int weight = 0;
        long value = 0L;
        List<Integer> chosen = new ArrayList<Integer>();
        for (Item item : sortResult.items) {
            comparisons++;
            if (weight + item.weight <= instance.capacity) {
                weight += item.weight;
                value += item.value;
                chosen.add(item.index + 1);
                updates++;
            }
        }
        return new GreedyResult(value, weight, chosen, comparisons, updates, "ordenacao por maior valor absoluto");
    }

    private static ExactResult solveDynamicProgramming(KnapsackInstance instance) {
        int n = instance.values.length;
        int capacity = instance.capacity;
        long start = System.nanoTime();
        long[][] dp = new long[n + 1][capacity + 1];
        boolean[][] take = new boolean[n + 1][capacity + 1];
        long comparisons = 0L;
        long updates = 0L;

        for (int i = 1; i <= n; i++) {
            int itemWeight = instance.weights[i - 1];
            int itemValue = instance.values[i - 1];
            for (int c = 0; c <= capacity; c++) {
                long without = dp[i - 1][c];
                long with = Long.MIN_VALUE / 4;
                comparisons++;
                if (itemWeight <= c) {
                    with = dp[i - 1][c - itemWeight] + itemValue;
                    comparisons++;
                    if (with > without) {
                        dp[i][c] = with;
                        take[i][c] = true;
                        updates++;
                    } else {
                        dp[i][c] = without;
                    }
                } else {
                    dp[i][c] = without;
                }
            }
        }

        List<Integer> chosen = new ArrayList<Integer>();
        int c = capacity;
        int weight = 0;
        for (int i = n; i >= 1; i--) {
            if (take[i][c]) {
                chosen.add(i);
                c -= instance.weights[i - 1];
                weight += instance.weights[i - 1];
            }
        }
        Collections.reverse(chosen);
        long elapsedMs = Math.max(0L, (System.nanoTime() - start) / 1_000_000L);
        return new ExactResult(dp[n][capacity], weight, chosen, comparisons, updates, elapsedMs);
    }

    private static GAResult solveGeneticAlgorithm(KnapsackInstance instance, GAConfig config) {
        long start = System.nanoTime();
        Random random = new Random(SEED + 31L * instance.name.hashCode() + config.name.hashCode());
        int n = instance.values.length;
        Individual[] population = new Individual[config.populationSize];

        boolean[] emptyGenes = new boolean[n];
        population[0] = evaluateIndividual(instance, emptyGenes, config.penaltyFactor);
        for (int i = 1; i < population.length; i++) {
            boolean[] genes = new boolean[n];
            for (int j = 0; j < n; j++) {
                genes[j] = random.nextDouble() < 0.35;
            }
            population[i] = evaluateIndividual(instance, genes, config.penaltyFactor);
        }

        double[] bestFitnessByGeneration = new double[config.generations];
        Individual bestFeasible = population[0].copy();
        long comparisons = 0L;
        long updates = 0L;

        for (int generation = 0; generation < config.generations; generation++) {
            SortPopulationResult sorted = sortPopulation(population);
            population = sorted.population;
            comparisons += sorted.comparisons;

            bestFitnessByGeneration[generation] = population[0].fitness;
            if (population[0].isFeasible(instance.capacity) && population[0].value > bestFeasible.value) {
                bestFeasible = population[0].copy();
            }

            Individual[] nextPopulation = new Individual[config.populationSize];
            int eliteLimit = Math.min(config.elitism, population.length);
            for (int i = 0; i < eliteLimit; i++) {
                nextPopulation[i] = population[i].copy();
                updates++;
            }

            int index = eliteLimit;
            while (index < nextPopulation.length) {
                Individual parent1 = tournamentSelection(population, config.tournamentSize, random);
                Individual parent2 = tournamentSelection(population, config.tournamentSize, random);
                comparisons += config.tournamentSize * 2L;

                boolean[] childGenes = crossover(parent1.genes, parent2.genes, random);
                updates++;
                int mutatedBits = mutate(childGenes, config.mutationRate, random);
                updates += mutatedBits;

                Individual child = evaluateIndividual(instance, childGenes, config.penaltyFactor);
                nextPopulation[index] = child;
                if (child.isFeasible(instance.capacity) && child.value > bestFeasible.value) {
                    bestFeasible = child.copy();
                }
                index++;
            }

            population = nextPopulation;
        }

        SortPopulationResult finalSorted = sortPopulation(population);
        population = finalSorted.population;
        comparisons += finalSorted.comparisons;
        for (Individual individual : population) {
            if (individual.isFeasible(instance.capacity) && individual.value > bestFeasible.value) {
                bestFeasible = individual.copy();
            }
        }

        long elapsedMs = Math.max(0L, (System.nanoTime() - start) / 1_000_000L);
        return new GAResult(bestFeasible, bestFitnessByGeneration, comparisons, updates, config, elapsedMs);
    }

    private static SortResult sortByRatio(KnapsackInstance instance) {
        final long[] comparisons = new long[] { 0L };
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < instance.values.length; i++) {
            items.add(new Item(i, instance.values[i], instance.weights[i]));
        }
        Collections.sort(items, new Comparator<Item>() {
            public int compare(Item a, Item b) {
                comparisons[0]++;
                double ratioA = a.ratio();
                double ratioB = b.ratio();
                if (ratioA < ratioB) {
                    return 1;
                }
                if (ratioA > ratioB) {
                    return -1;
                }
                if (a.value != b.value) {
                    return Integer.compare(b.value, a.value);
                }
                if (a.weight != b.weight) {
                    return Integer.compare(a.weight, b.weight);
                }
                return Integer.compare(a.index, b.index);
            }
        });
        return new SortResult(items, comparisons[0]);
    }

    private static SortResult sortByValue(KnapsackInstance instance) {
        final long[] comparisons = new long[] { 0L };
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < instance.values.length; i++) {
            items.add(new Item(i, instance.values[i], instance.weights[i]));
        }
        Collections.sort(items, new Comparator<Item>() {
            public int compare(Item a, Item b) {
                comparisons[0]++;
                if (a.value != b.value) {
                    return Integer.compare(b.value, a.value);
                }
                double ratioA = a.ratio();
                double ratioB = b.ratio();
                if (ratioA < ratioB) {
                    return 1;
                }
                if (ratioA > ratioB) {
                    return -1;
                }
                return Integer.compare(a.index, b.index);
            }
        });
        return new SortResult(items, comparisons[0]);
    }

    private static SortPopulationResult sortPopulation(Individual[] population) {
        final long[] comparisons = new long[] { 0L };
        Individual[] copy = population.clone();
        Arrays.sort(copy, new Comparator<Individual>() {
            public int compare(Individual a, Individual b) {
                comparisons[0]++;
                if (a.fitness < b.fitness) {
                    return 1;
                }
                if (a.fitness > b.fitness) {
                    return -1;
                }
                return 0;
            }
        });
        return new SortPopulationResult(copy, comparisons[0]);
    }

    private static Individual tournamentSelection(Individual[] population, int tournamentSize, Random random) {
        Individual best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Individual candidate = population[random.nextInt(population.length)];
            if (best == null || candidate.fitness > best.fitness) {
                best = candidate;
            }
        }
        return best;
    }

    private static boolean[] crossover(boolean[] first, boolean[] second, Random random) {
        boolean[] child = new boolean[first.length];
        int point = random.nextInt(first.length);
        for (int i = 0; i < first.length; i++) {
            child[i] = i < point ? first[i] : second[i];
        }
        return child;
    }

    private static int mutate(boolean[] genes, double rate, Random random) {
        int mutated = 0;
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < rate) {
                genes[i] = !genes[i];
                mutated++;
            }
        }
        return mutated;
    }

    private static Individual evaluateIndividual(KnapsackInstance instance, boolean[] genes, long penaltyFactor) {
        int weight = 0;
        long value = 0L;
        List<Integer> items = new ArrayList<Integer>();
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                weight += instance.weights[i];
                value += instance.values[i];
                items.add(i + 1);
            }
        }
        long penalty = Math.max(0, weight - instance.capacity) * penaltyFactor;
        double fitness = value - penalty;
        return new Individual(genes.clone(), value, weight, fitness, items);
    }

    private static final class SortResult {
        private final List<Item> items;
        private final long comparisons;

        private SortResult(List<Item> items, long comparisons) {
            this.items = items;
            this.comparisons = comparisons;
        }
    }

    private static final class SortPopulationResult {
        private final Individual[] population;
        private final long comparisons;

        private SortPopulationResult(Individual[] population, long comparisons) {
            this.population = population;
            this.comparisons = comparisons;
        }
    }

    private static final class GAConfig {
        private final String name;
        private final int populationSize;
        private final int generations;
        private final double mutationRate;
        private final int tournamentSize;
        private final int elitism;
        private final long penaltyFactor;

        private GAConfig(String name, int populationSize, int generations, double mutationRate, int tournamentSize, int elitism, long penaltyFactor) {
            this.name = name;
            this.populationSize = populationSize;
            this.generations = generations;
            this.mutationRate = mutationRate;
            this.tournamentSize = tournamentSize;
            this.elitism = elitism;
            this.penaltyFactor = penaltyFactor;
        }
    }

    private static final class GAResult {
        private final Individual bestFeasible;
        private final double[] bestFitnessByGeneration;
        private final long comparisons;
        private final long updates;
        private final long timeMs;

        private GAResult(Individual bestFeasible, double[] bestFitnessByGeneration, long comparisons, long updates, GAConfig config, long timeMs) {
            this.bestFeasible = bestFeasible;
            this.bestFitnessByGeneration = bestFitnessByGeneration;
            this.comparisons = comparisons;
            this.updates = updates;
            this.timeMs = timeMs;
        }
    }

    private static final class GreedyResult {
        private final long value;
        private final int weight;
        private final List<Integer> items;
        private final long comparisons;
        private final long updates;
        private final String note;

        private GreedyResult(long value, int weight, List<Integer> items, long comparisons, long updates, String note) {
            this.value = value;
            this.weight = weight;
            this.items = items;
            this.comparisons = comparisons;
            this.updates = updates;
            this.note = note;
        }

        private String itemsAsString() {
            return items.toString();
        }
    }

    private static final class ExactResult {
        private final long value;
        private final int weight;
        private final List<Integer> items;
        private final long comparisons;
        private final long updates;
        private final long timeMs;

        private ExactResult(long value, int weight, List<Integer> items, long comparisons, long updates, long timeMs) {
            this.value = value;
            this.weight = weight;
            this.items = items;
            this.comparisons = comparisons;
            this.updates = updates;
            this.timeMs = timeMs;
        }

        private String itemsAsString() {
            return items.toString();
        }
    }

    private static final class KnapsackInstance {
        private final String name;
        private final int[] values;
        private final int[] weights;
        private final int capacity;

        private KnapsackInstance(String name, int[] values, int[] weights, int capacity) {
            this.name = name;
            this.values = values;
            this.weights = weights;
            this.capacity = capacity;
        }

        private int size() {
            return values.length;
        }
    }

    private static final class Item {
        private final int index;
        private final int value;
        private final int weight;

        private Item(int index, int value, int weight) {
            this.index = index;
            this.value = value;
            this.weight = weight;
        }

        private double ratio() {
            return (double) value / (double) weight;
        }
    }

    private static final class Individual {
        private final boolean[] genes;
        private final long value;
        private final int weight;
        private final double fitness;
        private final List<Integer> items;

        private Individual(boolean[] genes, long value, int weight, double fitness, List<Integer> items) {
            this.genes = genes;
            this.value = value;
            this.weight = weight;
            this.fitness = fitness;
            this.items = items;
        }

        private boolean isFeasible(int capacity) {
            return weight <= capacity;
        }

        private Individual copy() {
            return new Individual(genes.clone(), value, weight, fitness, new ArrayList<Integer>(items));
        }

        private String itemsAsString() {
            return items.toString();
        }
    }
}
