package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubsequenciaCrescente {

    private final int[] vetor;
    private final int[] best;
    private final int[] next;

    private final EstatisticasPD estatisticas;

    private int indiceInicio;
    private int tamanhoMaior;

    public SubsequenciaCrescente(int[] vetor) {
        this.vetor = vetor;
        this.best = new int[vetor.length];
        this.next = new int[vetor.length];
        this.estatisticas = new EstatisticasPD();

        Arrays.fill(next, -1);
    }

    public void resolver() {

        long inicio = System.nanoTime();

        int n = vetor.length;

        Arrays.fill(best, 1);
        Arrays.fill(next, -1);

        tamanhoMaior = 1;
        indiceInicio = 0;

        for (int i = n - 1; i >= 0; i--) {
            best[i] = 1;
            next[i] = -1;

            for (int j = i + 1; j < n; j++) {
                estatisticas.comparacoes++;
                if (vetor[j] > vetor[i]) {
                    int candidato = 1 + best[j];

                    if (candidato > best[i]) {
                        best[i] = candidato;
                        next[i] = j;
                        estatisticas.atualizacoes++;
                    }
                }
            }

            if (best[i] > tamanhoMaior) {
                tamanhoMaior = best[i];
                indiceInicio = i;
            }
        }

        long fim = System.nanoTime();

        estatisticas.tempoMs = (fim - inicio) / 1_000_000;
    }

    public List<Integer> obterSubsequencia() {
        List<Integer> subsequencia = new ArrayList<>();

        int atual = indiceInicio;

        while (atual != -1) {
            subsequencia.add(vetor[atual]);
            atual = next[atual];
        }

        return subsequencia;
    }

    public void imprimirResultadoCompleto() {
        System.out.println("Vetor:");
        System.out.println(Arrays.toString(vetor));

        System.out.println();

        System.out.println("Vetor best:");
        System.out.println(Arrays.toString(best));

        System.out.println();

        System.out.println("Vetor next:");
        System.out.println(Arrays.toString(next));

        System.out.println();

        System.out.println("Tamanho da maior subsequência crescente: " + tamanhoMaior);
        System.out.println("Subsequência encontrada: " + obterSubsequencia());

        System.out.println();

        System.out.println("Comparações: " + estatisticas.comparacoes);
        System.out.println("Atualizações: " + estatisticas.atualizacoes);
        System.out.println("Tempo: " + estatisticas.tempoMs + " ms");
    }

    public int getTamanhoMaior() {
        return tamanhoMaior;
    }

    public int[] getBest() {
        return best;
    }

    public int[] getNext() {
        return next;
    }

    public EstatisticasPD getEstatisticas() {
        return estatisticas;
    }
}