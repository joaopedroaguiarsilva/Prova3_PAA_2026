package src;

public class Item {

    private final int id;
    private final int valor;
    private final int peso;

    public Item(int id, int valor, int peso) {
        this.id = id;
        this.valor = valor;
        this.peso = peso;
    }

    public int getId() {
        return id;
    }

    public int getValor() {
        return valor;
    }

    public int getPeso() {
        return peso;
    }

    public double getRazaoValorPeso() {
        return (double) valor / peso;
    }

    @Override
    public String toString() {
        return "Item " + id +
                " [valor=" + valor +
                ", peso=" + peso +
                ", razão=" + String.format("%.2f", getRazaoValorPeso()) +
                "]";
    }
}