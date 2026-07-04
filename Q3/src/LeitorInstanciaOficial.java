package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LeitorInstanciaOficial {

    public static InstanciaMochila ler(Path caminhoArquivo) throws IOException {

        List<String> linhas = Files.readAllLines(caminhoArquivo);

        if (linhas.isEmpty()) {
            throw new IOException("Arquivo da instância oficial está vazio.");
        }

        String[] primeiraLinha = linhas.get(0).trim().split("\\s+");

        int quantidadeItens = Integer.parseInt(primeiraLinha[0]);
        int capacidade = Integer.parseInt(primeiraLinha[1]);

        List<Item> itens = new ArrayList<>();

        for (int i = 1; i <= quantidadeItens; i++) {

            String linha = linhas.get(i).trim();

            String[] partes = linha.split("\\s+");

            int valor = Integer.parseInt(partes[0]);
            int peso = Integer.parseInt(partes[1]);

            itens.add(new Item(i, valor, peso));
        }

        InstanciaMochila instancia = new InstanciaMochila(
                "knapPI_1_100_1000_1.txt",
                itens,
                capacidade
        );

        int indiceLinhaSolucao = quantidadeItens + 1;

        if (linhas.size() > indiceLinhaSolucao) {

            String linhaSolucao = linhas.get(indiceLinhaSolucao).trim();

            String[] partes = linhaSolucao.split("\\s+");

            int[] solucaoOtimaOficial = new int[quantidadeItens];

            for (int i = 0; i < quantidadeItens && i < partes.length; i++) {
                solucaoOtimaOficial[i] = Integer.parseInt(partes[i]);
            }

            instancia.setSolucaoOtimaOficial(solucaoOtimaOficial);
        }

        return instancia;
    }
}