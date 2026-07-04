package src;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Q3 - Mochila 0/1
 *
 * Métodos comparados:
 * 1. Guloso por razão valor/peso
 * 2. Guloso por maior valor absoluto
 * 3. Algoritmo Genético - configuração 1
 * 4. Algoritmo Genético - configuração 2
 * 5. Programação Dinâmica - referência ótima
 */
public class MainQ3 {

    private static final int S = 466473;

    private static final Path PASTA_BASE = definirPastaBase();
    private static final Path PASTA_RESULTADOS = PASTA_BASE.resolve("resultados");
    private static final Path PASTA_DADOS = PASTA_BASE.resolve("dados");

    private static final Path CAMINHO_RESULTADOS_CSV =
            PASTA_RESULTADOS.resolve("resultados.csv");

    private static final Path CAMINHO_HISTORICO_AG_CSV =
            PASTA_RESULTADOS.resolve("ag_historico.csv");

    private static final Path CAMINHO_INSTANCIA_OFICIAL =
            PASTA_DADOS.resolve("knapPI_1_100_1000_1.txt");

    public static void main(String[] args) {

        try {

            criarPastas();

            List<InstanciaMochila> instancias = carregarInstancias();

            try (
                    PrintWriter resultadosCsv = criarWriter(CAMINHO_RESULTADOS_CSV);
                    PrintWriter historicoAgCsv = criarWriter(CAMINHO_HISTORICO_AG_CSV)
            ) {

                escreverCabecalhoResultados(resultadosCsv);
                escreverCabecalhoHistoricoAG(historicoAgCsv);

                for (InstanciaMochila instancia : instancias) {
                    executarInstancia(instancia, resultadosCsv, historicoAgCsv);
                }

                System.out.println("=========================================");
                System.out.println("Arquivos gerados:");
                System.out.println(CAMINHO_RESULTADOS_CSV.toAbsolutePath());
                System.out.println(CAMINHO_HISTORICO_AG_CSV.toAbsolutePath());

            }

        } catch (IOException e) {
            System.out.println("Erro ao executar a Q3.");
            e.printStackTrace();
        }
    }

    private static List<InstanciaMochila> carregarInstancias() throws IOException {

        List<InstanciaMochila> instancias = new ArrayList<>();

        InstanciaMochila gerada20 =
                GeradorInstancias.gerarInstancia("gerada_20", 20, S);

        InstanciaMochila gerada50 =
                GeradorInstancias.gerarInstancia("gerada_50", 50, S);

        instancias.add(gerada20);
        instancias.add(gerada50);

        if (Files.exists(CAMINHO_INSTANCIA_OFICIAL)) {

            InstanciaMochila oficial =
                    LeitorInstanciaOficial.ler(CAMINHO_INSTANCIA_OFICIAL);

            instancias.add(oficial);

        } else {

            System.out.println("ATENÇÃO: instância oficial não encontrada.");
            System.out.println("Coloque o arquivo em:");
            System.out.println(CAMINHO_INSTANCIA_OFICIAL.toAbsolutePath());
            System.out.println("A execução continuará apenas com as instâncias geradas.");
            System.out.println();
        }

        return instancias;
    }

    private static void executarInstancia(
            InstanciaMochila instancia,
            PrintWriter resultadosCsv,
            PrintWriter historicoAgCsv) {

        System.out.println("=========================================");
        System.out.println(instancia);
        System.out.println("=========================================");

        // Primeiro executamos a PD para obter o valor ótimo de referência.
        ResultadoMochila resultadoPD = ProgramacaoDinamica.resolver(instancia);

        int valorReferencia = resultadoPD.getValorObtido();

        conferirSolucaoOficial(instancia, valorReferencia);

        ConfiguracaoAG config1 = new ConfiguracaoAG(
                "config1",
                100,        // tamanho da população
                300,        // gerações
                0.85,       // taxa de cruzamento
                0.02,       // taxa de mutação
                3,          // tamanho do torneio
                10,         // penalidade por excesso de peso
                S + 31
        );

        ConfiguracaoAG config2 = new ConfiguracaoAG(
                "config2",
                200,        // tamanho da população
                500,        // gerações
                0.90,       // taxa de cruzamento
                0.05,       // taxa de mutação
                3,          // tamanho do torneio
                10,         // penalidade por excesso de peso
                S + 73
        );

        List<ResultadoMochila> resultados = new ArrayList<>();

        resultados.add(Guloso.resolverPorRazao(instancia));
        resultados.add(Guloso.resolverPorValor(instancia));

        resultados.add(AlgoritmoGenetico.resolver(
                instancia,
                config1,
                historicoAgCsv
        ));

        resultados.add(AlgoritmoGenetico.resolver(
                instancia,
                config2,
                historicoAgCsv
        ));

        resultados.add(resultadoPD);

        for (ResultadoMochila resultado : resultados) {

            resultado.setValorReferencia(valorReferencia);

            resultado.imprimir();

            salvarResultadoCSV(
                    resultadosCsv,
                    instancia,
                    resultado
            );
        }

        resultadosCsv.flush();
        historicoAgCsv.flush();
    }

    private static void conferirSolucaoOficial(
            InstanciaMochila instancia,
            int valorReferencia) {

        if (instancia.getSolucaoOtimaOficial() == null) {
            return;
        }

        int valorOficial =
                ProgramacaoDinamica.calcularValorSolucaoOficial(instancia);

        int pesoOficial =
                ProgramacaoDinamica.calcularPesoSolucaoOficial(instancia);

        System.out.println("Conferência da solução oficial:");
        System.out.println("Valor da solução oficial: " + valorOficial);
        System.out.println("Peso da solução oficial: " + pesoOficial);
        System.out.println("Valor ótimo calculado por PD: " + valorReferencia);

        if (valorOficial == valorReferencia) {
            System.out.println("Conferência: OK");
        } else {
            System.out.println("Conferência: valor oficial diferente da PD.");
        }

        System.out.println();
    }

    private static void escreverCabecalhoResultados(PrintWriter csv) {

        csv.println(
                "S," +
                "instancia," +
                "n," +
                "capacidade," +
                "metodo," +
                "configuracao," +
                "valor," +
                "peso," +
                "itens," +
                "tempo_ms," +
                "comparacoes," +
                "avaliacoes," +
                "cruzamentos," +
                "mutacoes," +
                "geracoes," +
                "Vref," +
                "gap," +
                "gap_percentual"
        );
    }

    private static void escreverCabecalhoHistoricoAG(PrintWriter csv) {

        csv.println(
                "semente_config," +
                "instancia," +
                "configuracao," +
                "geracao," +
                "melhor_aptidao," +
                "melhor_valor," +
                "melhor_peso"
        );
    }

    private static void salvarResultadoCSV(
            PrintWriter csv,
            InstanciaMochila instancia,
            ResultadoMochila resultado) {

        csv.println(
                S + "," +
                formatarCSV(instancia.getNome()) + "," +
                instancia.getQuantidadeItens() + "," +
                instancia.getCapacidade() + "," +
                formatarCSV(resultado.getMetodo()) + "," +
                formatarCSV(resultado.getConfiguracao()) + "," +
                resultado.getValorObtido() + "," +
                resultado.getPesoUsado() + "," +
                formatarCSV(resultado.getItensEscolhidosComoTexto()) + "," +
                resultado.getTempoMs() + "," +
                resultado.getComparacoes() + "," +
                resultado.getAvaliacoes() + "," +
                resultado.getCruzamentos() + "," +
                resultado.getMutacoes() + "," +
                resultado.getGeracoes() + "," +
                resultado.getValorReferencia() + "," +
                resultado.getGapAbsoluto() + "," +
                String.format(java.util.Locale.US, "%.4f", resultado.getGapPercentual())
        );
    }

    private static PrintWriter criarWriter(Path caminho) throws IOException {

        return new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(caminho.toFile()),
                        StandardCharsets.UTF_8
                )
        );
    }

    private static void criarPastas() throws IOException {
        Files.createDirectories(PASTA_RESULTADOS);
        Files.createDirectories(PASTA_DADOS);
    }

    private static String formatarCSV(String valor) {

        String valorTratado = valor.replace("\"", "\"\"");

        return "\"" + valorTratado + "\"";
    }

    private static Path definirPastaBase() {

        Path atual = Paths.get("").toAbsolutePath().normalize();

        if (atual.getFileName() != null &&
                atual.getFileName().toString().equalsIgnoreCase("Q3")) {
            return atual;
        }

        Path possivelQ3 = atual.resolve("Q3");

        if (Files.exists(possivelQ3) && Files.isDirectory(possivelQ3)) {
            return possivelQ3;
        }

        return atual;
    }
}