package src;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AlgoritmoGenetico {
    public static ResultadoMochila resolver(InstanciaMochila instancia, ConfiguracaoAG config, PrintWriter historicoCsv) {
        long inicio = System.nanoTime();

        Random random = new Random(config.getSemente());

        List<Individuo> populacao = criarPopulacaoInicial(instancia, config, random);

        long avaliacoes = 0;
        long cruzamentos = 0;
        long mutacoes = 0;

        Individuo melhorValidoGlobal = null;

        for (int geracao = 0; geracao <= config.getGeracoes(); geracao++) {
            for (Individuo individuo : populacao) {
                avaliar(individuo, instancia, config);
                avaliacoes++;
            }

            Individuo melhorDaGeracao = obterMelhorPorAptidao(populacao);
            Individuo melhorValidoDaGeracao = obterMelhorValido(populacao, instancia);

            if (melhorValidoDaGeracao != null) {
                if (melhorValidoGlobal == null || melhorValidoDaGeracao.valor > melhorValidoGlobal.valor) {
                    melhorValidoGlobal = melhorValidoDaGeracao.copiar();
                }
            }

            salvarHistorico(historicoCsv, config, instancia, geracao, melhorDaGeracao);

            if (geracao == config.getGeracoes()) {
                break;
            }

            List<Individuo> novaPopulacao = new ArrayList<>();

            novaPopulacao.add(melhorDaGeracao.copiar());

            while (novaPopulacao.size() < config.getTamanhoPopulacao()) {
                Individuo pai1 = selecionarPorTorneio(populacao, config, random);
                Individuo pai2 = selecionarPorTorneio(populacao, config, random);

                Individuo[] filhos = cruzar(pai1, pai2, config, random);

                cruzamentos++;

                mutacoes += mutar(filhos[0], config, random);

                if (novaPopulacao.size() < config.getTamanhoPopulacao()) {
                    novaPopulacao.add(filhos[0]);
                }

                if (novaPopulacao.size() < config.getTamanhoPopulacao()) {
                    mutacoes += mutar(filhos[1], config, random);
                    novaPopulacao.add(filhos[1]);
                }
            }

            populacao = novaPopulacao;
        }

        if (melhorValidoGlobal == null) {
            melhorValidoGlobal = criarIndividuoVazio(instancia);
            avaliar(melhorValidoGlobal, instancia, config);
            avaliacoes++;
        }

        long fim = System.nanoTime();
        long tempoMs = (fim - inicio) / 1_000_000;

        List<Integer> itensEscolhidos = obterItensEscolhidos( melhorValidoGlobal, instancia);

        ResultadoMochila resultado = new ResultadoMochila(instancia.getNome(), "Algoritmo Genético", config.getNome(), melhorValidoGlobal.valor, melhorValidoGlobal.peso, itensEscolhidos, tempoMs);

        resultado.setAvaliacoes(avaliacoes);
        resultado.setCruzamentos(cruzamentos);
        resultado.setMutacoes(mutacoes);
        resultado.setGeracoes(config.getGeracoes());

        return resultado;
    }

    private static List<Individuo> criarPopulacaoInicial(InstanciaMochila instancia, ConfiguracaoAG config, Random random) {
        List<Individuo> populacao = new ArrayList<>();

        int n = instancia.getQuantidadeItens();

        for (int i = 0; i < config.getTamanhoPopulacao(); i++) {
            Individuo individuo = new Individuo(n);

            int pesoAtual = 0;

            for (int j = 0; j < n; j++) {
                if (random.nextBoolean()) {
                    Item item = instancia.getItens().get(j);

                    if (pesoAtual + item.getPeso() <= instancia.getCapacidade()) {
                        individuo.genes[j] = true;
                        pesoAtual += item.getPeso();
                    }
                }
            }

            populacao.add(individuo);
        }

        return populacao;
    }

    private static void avaliar(Individuo individuo, InstanciaMochila instancia, ConfiguracaoAG config) {
        int valor = 0;
        int peso = 0;

        for (int i = 0; i < individuo.genes.length; i++) {
            if (individuo.genes[i]) {
                Item item = instancia.getItens().get(i);

                valor += item.getValor();
                peso += item.getPeso();
            }
        }

        individuo.valor = valor;
        individuo.peso = peso;

        if (peso <= instancia.getCapacidade()) {
            individuo.aptidao = valor;
        } else {
            int excesso = peso - instancia.getCapacidade();

            individuo.aptidao = valor - (config.getPenalidadePorExcesso() * excesso);
        }
    }

    private static Individuo selecionarPorTorneio(List<Individuo> populacao, ConfiguracaoAG config, Random random) {
        Individuo melhor = null;

        for (int i = 0; i < config.getTamanhoTorneio(); i++) {
            Individuo candidato = populacao.get(random.nextInt(populacao.size()));

            if (melhor == null || candidato.aptidao > melhor.aptidao) {
                melhor = candidato;
            }
        }

        return melhor;
    }

    private static Individuo[] cruzar(Individuo pai1, Individuo pai2, ConfiguracaoAG config, Random random) {
        int n = pai1.genes.length;

        Individuo filho1 = new Individuo(n);
        Individuo filho2 = new Individuo(n);

        if (random.nextDouble() > config.getTaxaCruzamento()) {
            filho1 = pai1.copiar();
            filho2 = pai2.copiar();

            return new Individuo[]{filho1, filho2};
        }

        int pontoCorte = 1 + random.nextInt(n - 1);

        for (int i = 0; i < n; i++) {
            if (i < pontoCorte) {
                filho1.genes[i] = pai1.genes[i];
                filho2.genes[i] = pai2.genes[i];
            } else {
                filho1.genes[i] = pai2.genes[i];
                filho2.genes[i] = pai1.genes[i];
            }
        }

        return new Individuo[]{filho1, filho2};
    }

    private static long mutar(Individuo individuo, ConfiguracaoAG config, Random random) {
        long mutacoes = 0;

        for (int i = 0; i < individuo.genes.length; i++) {
            if (random.nextDouble() < config.getTaxaMutacao()) {
                individuo.genes[i] = !individuo.genes[i];
                mutacoes++;
            }
        }

        return mutacoes;
    }

    private static Individuo obterMelhorPorAptidao(List<Individuo> populacao) {
        Individuo melhor = populacao.get(0);

        for (Individuo individuo : populacao) {
            if (individuo.aptidao > melhor.aptidao) {
                melhor = individuo;
            }
        }

        return melhor;
    }

    private static Individuo obterMelhorValido(List<Individuo> populacao, InstanciaMochila instancia) {

        Individuo melhor = null;
        for (Individuo individuo : populacao) {
            if (individuo.peso <= instancia.getCapacidade()) {
                if (melhor == null || individuo.valor > melhor.valor) {
                    melhor = individuo;
                }
            }
        }

        return melhor;
    }

    private static Individuo criarIndividuoVazio(InstanciaMochila instancia) {
        return new Individuo(instancia.getQuantidadeItens());
    }

    private static List<Integer> obterItensEscolhidos(
            Individuo individuo,
            InstanciaMochila instancia) {

        List<Integer> itensEscolhidos = new ArrayList<>();

        for (int i = 0; i < individuo.genes.length; i++) {
            if (individuo.genes[i]) {
                itensEscolhidos.add(instancia.getItens().get(i).getId());
            }
        }

        return itensEscolhidos;
    }

    private static void salvarHistorico(PrintWriter historicoCsv, ConfiguracaoAG config, InstanciaMochila instancia, int geracao, Individuo melhor) {
        if (historicoCsv == null) {
            return;
        }

        historicoCsv.println(
                config.getSemente() + "," +
                instancia.getNome() + "," +
                config.getNome() + "," +
                geracao + "," +
                String.format(Locale.US, "%.2f", melhor.aptidao) + "," +
                melhor.valor + "," +
                melhor.peso
        );

        historicoCsv.flush();
    }

    private static class Individuo {

        boolean[] genes;

        int valor;
        int peso;
        double aptidao;

        Individuo(int tamanho) {
            this.genes = new boolean[tamanho];
            this.valor = 0;
            this.peso = 0;
            this.aptidao = 0;
        }

        Individuo copiar() {
            Individuo copia = new Individuo(genes.length);
            for (int i = 0; i < genes.length; i++) {
                copia.genes[i] = genes[i];
            }

            copia.valor = valor;
            copia.peso = peso;
            copia.aptidao = aptidao;

            return copia;
        }
    }
}