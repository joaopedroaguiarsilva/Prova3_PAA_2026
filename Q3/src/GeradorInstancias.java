package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeradorInstancias {

    public static InstanciaMochila gerarInstancia(String nome, int quantidadeItens, int semente) {

        Random random = new Random(semente + quantidadeItens);

        List<Item> itens = new ArrayList<>();

        int somaPesos = 0;

        for (int i = 1; i <= quantidadeItens; i++) {
            int peso = random.nextInt(100) + 1;   // [1, 100]
            int valor = random.nextInt(500) + 1;  // [1, 500]

            itens.add(new Item(i, valor, peso));

            somaPesos += peso;
        }

        int capacidade = (int) Math.floor(0.5 * somaPesos);

        return new InstanciaMochila(nome, itens, capacidade);
    }
}