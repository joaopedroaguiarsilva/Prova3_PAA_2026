package src;

import java.util.ArrayList;
import java.util.List;

public class ResultadoMochila {

    private final String nomeInstancia;
    private final String metodo;
    private final String configuracao;

    private final int valorObtido;
    private final int pesoUsado;
    private final List<Integer> itensEscolhidos;

    private final long tempoMs;

    private long comparacoes;
    private long avaliacoes;
    private long cruzamentos;
    private long mutacoes;
    private long geracoes;

    private int valorReferencia;

    public ResultadoMochila(
            String nomeInstancia,
            String metodo,
            String configuracao,
            int valorObtido,
            int pesoUsado,
            List<Integer> itensEscolhidos,
            long tempoMs) {

        this.nomeInstancia = nomeInstancia;
        this.metodo = metodo;
        this.configuracao = configuracao;
        this.valorObtido = valorObtido;
        this.pesoUsado = pesoUsado;
        this.itensEscolhidos = new ArrayList<>(itensEscolhidos);
        this.tempoMs = tempoMs;

        this.comparacoes = 0;
        this.avaliacoes = 0;
        this.cruzamentos = 0;
        this.mutacoes = 0;
        this.geracoes = 0;

        this.valorReferencia = 0;
    }

    public String getNomeInstancia() {
        return nomeInstancia;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getConfiguracao() {
        return configuracao;
    }

    public int getValorObtido() {
        return valorObtido;
    }

    public int getPesoUsado() {
        return pesoUsado;
    }

    public List<Integer> getItensEscolhidos() {
        return itensEscolhidos;
    }

    public long getTempoMs() {
        return tempoMs;
    }

    public long getComparacoes() {
        return comparacoes;
    }

    public void setComparacoes(long comparacoes) {
        this.comparacoes = comparacoes;
    }

    public long getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(long avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public long getCruzamentos() {
        return cruzamentos;
    }

    public void setCruzamentos(long cruzamentos) {
        this.cruzamentos = cruzamentos;
    }

    public long getMutacoes() {
        return mutacoes;
    }

    public void setMutacoes(long mutacoes) {
        this.mutacoes = mutacoes;
    }

    public long getGeracoes() {
        return geracoes;
    }

    public void setGeracoes(long geracoes) {
        this.geracoes = geracoes;
    }

    public int getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(int valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public int getGapAbsoluto() {
        return valorReferencia - valorObtido;
    }

    public double getGapPercentual() {
        if (valorReferencia == 0) {
            return 0.0;
        }

        return ((double) getGapAbsoluto() / valorReferencia) * 100.0;
    }

    public String getItensEscolhidosComoTexto() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < itensEscolhidos.size(); i++) {
            sb.append(itensEscolhidos.get(i));

            if (i < itensEscolhidos.size() - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public void imprimir() {
        System.out.println("Método: " + metodo);
        System.out.println("Configuração: " + configuracao);
        System.out.println("Valor obtido: " + valorObtido);
        System.out.println("Peso usado: " + pesoUsado);
        System.out.println("Itens escolhidos: " + getItensEscolhidosComoTexto());
        System.out.println("Tempo: " + tempoMs + " ms");
        System.out.println("Valor referência: " + valorReferencia);
        System.out.println("Gap absoluto: " + getGapAbsoluto());
        System.out.printf("Gap percentual: %.2f%%\n", getGapPercentual());
        System.out.println();
    }
}