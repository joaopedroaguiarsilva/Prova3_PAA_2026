package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramacaoDinamica {

    public static ResultadoMochila resolver(InstanciaMochila instancia) {

        long inicio = System.nanoTime();

        List<Item> itens = instancia.getItens();

        int n = itens.size();
        int capacidade = instancia.getCapacidade();

        int[][] dp = new int[n + 1][capacidade + 1];

        long estadosCalculados = 0;
        long comparacoes = 0;

        for (int i = 1; i <= n; i++) {

            Item item = itens.get(i - 1);

            for (int pesoAtual = 0; pesoAtual <= capacidade; pesoAtual++) {

                estadosCalculados++;

                int semItem = dp[i - 1][pesoAtual];
                int comItem = 0;

                if (item.getPeso() <= pesoAtual) {
                    comItem = item.getValor()
                            + dp[i - 1][pesoAtual - item.getPeso()];

                    comparacoes++;

                    dp[i][pesoAtual] = Math.max(semItem, comItem);
                } else {
                    dp[i][pesoAtual] = semItem;
                }
            }
        }

        List<Integer> itensEscolhidos = reconstruirSolucao(
                instancia,
                dp
        );

        int valorObtido = dp[n][capacidade];
        int pesoUsado = calcularPeso(instancia, itensEscolhidos);

        long fim = System.nanoTime();

        long tempoMs = (fim - inicio) / 1_000_000;

        ResultadoMochila resultado = new ResultadoMochila(
                instancia.getNome(),
                "Programação Dinâmica",
                "ótima",
                valorObtido,
                pesoUsado,
                itensEscolhidos,
                tempoMs
        );

        resultado.setComparacoes(comparacoes);
        resultado.setAvaliacoes(estadosCalculados);
        resultado.setValorReferencia(valorObtido);

        return resultado;
    }

    private static List<Integer> reconstruirSolucao( InstanciaMochila instancia, int[][] dp) {

        List<Item> itens = instancia.getItens();

        List<Integer> itensEscolhidos = new ArrayList<>();

        int i = itens.size();
        int pesoAtual = instancia.getCapacidade();

        while (i > 0 && pesoAtual >= 0) {

            Item item = itens.get(i - 1);

            if (dp[i][pesoAtual] != dp[i - 1][pesoAtual]) {

                itensEscolhidos.add(item.getId());

                pesoAtual -= item.getPeso();
            }

            i--;
        }

        Collections.reverse(itensEscolhidos);

        return itensEscolhidos;
    }

    private static int calcularPeso(InstanciaMochila instancia, List<Integer> itensEscolhidos) {

        int pesoTotal = 0;

        for (int id : itensEscolhidos) {

            Item item = instancia.getItens().get(id - 1);

            pesoTotal += item.getPeso();
        }

        return pesoTotal;
    }

    public static int calcularValorSolucao(InstanciaMochila instancia, List<Integer> itensEscolhidos) {

        int valorTotal = 0;

        for (int id : itensEscolhidos) {

            Item item = instancia.getItens().get(id - 1);

            valorTotal += item.getValor();
        }

        return valorTotal;
    }

    public static int calcularPesoSolucao(
            InstanciaMochila instancia,
            List<Integer> itensEscolhidos) {

        int pesoTotal = 0;

        for (int id : itensEscolhidos) {

            Item item = instancia.getItens().get(id - 1);

            pesoTotal += item.getPeso();
        }

        return pesoTotal;
    }

    public static int calcularValorSolucaoOficial(InstanciaMochila instancia) {
        int[] solucao = instancia.getSolucaoOtimaOficial();

        if (solucao == null) {
            return 0;
        }

        int valorTotal = 0;

        for (int i = 0; i < solucao.length; i++) {

            if (solucao[i] == 1) {
                valorTotal += instancia.getItens().get(i).getValor();
            }
        }

        return valorTotal;
    }

    public static int calcularPesoSolucaoOficial(InstanciaMochila instancia) {
        int[] solucao = instancia.getSolucaoOtimaOficial();

        if (solucao == null) {
            return 0;
        }

        int pesoTotal = 0;

        for (int i = 0; i < solucao.length; i++) {

            if (solucao[i] == 1) {
                pesoTotal += instancia.getItens().get(i).getPeso();
            }
        }

        return pesoTotal;
    }
}