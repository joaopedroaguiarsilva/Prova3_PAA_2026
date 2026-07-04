package src;

import java.util.ArrayList;
import java.util.List;

public class InstanciaMochila {

    private final String nome;
    private final List<Item> itens;
    private final int capacidade;

    // Usado apenas na instância oficial, se existir
    private int[] solucaoOtimaOficial;

    public InstanciaMochila(String nome, List<Item> itens, int capacidade) {
        this.nome = nome;
        this.itens = itens;
        this.capacidade = capacidade;
        this.solucaoOtimaOficial = null;
    }

    public String getNome() {
        return nome;
    }

    public List<Item> getItens() {
        return itens;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getQuantidadeItens() {
        return itens.size();
    }

    public int[] getSolucaoOtimaOficial() {
        return solucaoOtimaOficial;
    }

    public void setSolucaoOtimaOficial(int[] solucaoOtimaOficial) {
        this.solucaoOtimaOficial = solucaoOtimaOficial;
    }

    public int getSomaPesos() {
        int soma = 0;

        for (Item item : itens) {
            soma += item.getPeso();
        }

        return soma;
    }

    public int getSomaValores() {
        int soma = 0;

        for (Item item : itens) {
            soma += item.getValor();
        }

        return soma;
    }

    public List<Item> copiarItens() {
        return new ArrayList<>(itens);
    }

    @Override
    public String toString() {
        return "Instância: " + nome +
                "\nQuantidade de itens: " + getQuantidadeItens() +
                "\nCapacidade: " + capacidade +
                "\nSoma dos pesos: " + getSomaPesos() +
                "\nSoma dos valores: " + getSomaValores();
    }
}