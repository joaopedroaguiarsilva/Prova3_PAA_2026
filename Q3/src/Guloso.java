package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Guloso {

    public static ResultadoMochila resolverPorRazao(InstanciaMochila instancia) {
        long inicio = System.nanoTime();

        List<Item> itensOrdenados = instancia.copiarItens();

        itensOrdenados.sort(Comparator.comparingDouble(Item::getRazaoValorPeso).reversed());

        ResultadoParcial parcial = escolherItens(instancia, itensOrdenados);

        long fim = System.nanoTime();

        long tempoMs = (fim - inicio) / 1_000_000;

        ResultadoMochila resultado = new ResultadoMochila(
                instancia.getNome(),
                "Guloso razão",
                "-",
                parcial.valorTotal,
                parcial.pesoTotal,
                parcial.itensEscolhidos,
                tempoMs
        );

        resultado.setComparacoes(itensOrdenados.size());

        return resultado;
    }

    public static ResultadoMochila resolverPorValor(InstanciaMochila instancia) {

        long inicio = System.nanoTime();

        List<Item> itensOrdenados = instancia.copiarItens();

        itensOrdenados.sort(
                Comparator.comparingInt(Item::getValor).reversed()
        );

        ResultadoParcial parcial = escolherItens(instancia, itensOrdenados);

        long fim = System.nanoTime();

        long tempoMs = (fim - inicio) / 1_000_000;

        ResultadoMochila resultado = new ResultadoMochila(
                instancia.getNome(),
                "Guloso valor",
                "-",
                parcial.valorTotal,
                parcial.pesoTotal,
                parcial.itensEscolhidos,
                tempoMs
        );

        resultado.setComparacoes(itensOrdenados.size());

        return resultado;
    }

    private static ResultadoParcial escolherItens(
            InstanciaMochila instancia,
            List<Item> itensOrdenados) {

        int pesoTotal = 0;
        int valorTotal = 0;

        List<Integer> itensEscolhidos = new ArrayList<>();

        for (Item item : itensOrdenados) {

            if (pesoTotal + item.getPeso() <= instancia.getCapacidade()) {

                pesoTotal += item.getPeso();
                valorTotal += item.getValor();

                itensEscolhidos.add(item.getId());
            }
        }

        return new ResultadoParcial(valorTotal, pesoTotal, itensEscolhidos);
    }

    private static class ResultadoParcial {

        int valorTotal;
        int pesoTotal;
        List<Integer> itensEscolhidos;

        ResultadoParcial(int valorTotal, int pesoTotal, List<Integer> itensEscolhidos) {
            this.valorTotal = valorTotal;
            this.pesoTotal = pesoTotal;
            this.itensEscolhidos = itensEscolhidos;
        }
    }
}