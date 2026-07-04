package src;

public class ConfiguracaoAG {

    private final String nome;
    private final int tamanhoPopulacao;
    private final int geracoes;
    private final double taxaCruzamento;
    private final double taxaMutacao;
    private final int tamanhoTorneio;
    private final int penalidadePorExcesso;
    private final int semente;

    public ConfiguracaoAG(String nome, int tamanhoPopulacao, int geracoes, double taxaCruzamento, double taxaMutacao, int tamanhoTorneio, int penalidadePorExcesso, int semente) {
        this.nome = nome;
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.geracoes = geracoes;
        this.taxaCruzamento = taxaCruzamento;
        this.taxaMutacao = taxaMutacao;
        this.tamanhoTorneio = tamanhoTorneio;
        this.penalidadePorExcesso = penalidadePorExcesso;
        this.semente = semente;
    }

    public String getNome() {
        return nome;
    }

    public int getTamanhoPopulacao() {
        return tamanhoPopulacao;
    }

    public int getGeracoes() {
        return geracoes;
    }

    public double getTaxaCruzamento() {
        return taxaCruzamento;
    }

    public double getTaxaMutacao() {
        return taxaMutacao;
    }

    public int getTamanhoTorneio() {
        return tamanhoTorneio;
    }

    public int getPenalidadePorExcesso() {
        return penalidadePorExcesso;
    }

    public int getSemente() {
        return semente;
    }

    @Override
    public String toString() {
        return nome +
                " [população=" + tamanhoPopulacao +
                ", gerações=" + geracoes +
                ", cruzamento=" + taxaCruzamento +
                ", mutação=" + taxaMutacao +
                ", torneio=" + tamanhoTorneio +
                ", penalidade=" + penalidadePorExcesso +
                ", semente=" + semente +
                "]";
    }
}