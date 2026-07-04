package src;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PasseioCavalo {

    private final int n;
    private final int[][] tabuleiro;
    private final boolean usarHeuristica;

    private final Estatisticas estatisticas;

    private final int[] dx = {2, 1, -1, -2, -2, -1, 1, 2};
    private final int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

    private static final long LIMITE_TEMPO_NS = 10L * 60L * 1_000_000_000L;

    private long inicioExecucao;
    private boolean tempoEsgotado;

    public PasseioCavalo(int n, boolean usarHeuristica) {
        this.n = n;
        this.usarHeuristica = usarHeuristica;
        this.tabuleiro = new int[n][n];
        this.estatisticas = new Estatisticas();
    }

    public boolean resolver(int linha, int coluna) {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tabuleiro[i][j] = 0;
            }
        }

        estatisticas.chamadas = 0;
        estatisticas.tentativas = 0;
        estatisticas.backtracks = 0;
        estatisticas.tempo = 0;
        estatisticas.tempoEsgotado = false;
        
        tempoEsgotado = false;
        
        long inicio = System.nanoTime();
        inicioExecucao = inicio;

        int x = linha - 1;
        int y = coluna - 1;

        tabuleiro[x][y] = 1;

        boolean encontrou = tentar(2, x, y);

        long fim = System.nanoTime();

        estatisticas.tempo = (fim - inicio) / 1_000_000;

        return encontrou;
    }

    private boolean tentar(int passo, int x, int y) {

        estatisticas.chamadas++;
    
        if (tempoLimiteAtingido()) {
            tempoEsgotado = true;
            return false;
        }
    
        if (passo > n * n) {
            return true;
        }
    
        List<int[]> movimentos = obterMovimentos(x, y);
    
        for (int[] movimento : movimentos) {
    
            if (tempoEsgotado) {
                return false;
            }
    
            estatisticas.tentativas++;
    
            int nx = movimento[0];
            int ny = movimento[1];
    
            tabuleiro[nx][ny] = passo;
    
            if (tentar(passo + 1, nx, ny)) {
                return true;
            }
    
            tabuleiro[nx][ny] = 0;
    
            if (tempoEsgotado) {
                return false;
            }
    
            estatisticas.backtracks++;
        }
    
        return false;
    }

    private boolean movimentoValido(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < n && tabuleiro[x][y] == 0;
    }

    public void imprimirTabuleiro() {
        System.out.println();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%4d", tabuleiro[i][j]);
            }

            System.out.println();
        }

        System.out.println();
    }

    public Estatisticas getEstatisticas() {
        return estatisticas;
    }

    public boolean usandoHeuristica() {
        return usarHeuristica;
    }

    private List<int[]> obterMovimentos(int x, int y) {
        List<int[]> movimentos = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if (movimentoValido(nx, ny)) {
                movimentos.add(new int[]{nx, ny});
            }
        }

        if (usarHeuristica) {
            movimentos.sort(Comparator.comparingInt(m -> contarSaidas(m[0], m[1])));
        }

        return movimentos;
    }

    private int contarSaidas(int x, int y) {
        int total = 0;
        for (int i = 0; i < 8; i++) {

            int nx = x + dx[i];
            int ny = y + dy[i];
            
            if (movimentoValido(nx, ny)) {
                total++;
            }
        }

        return total;
    }

    private boolean tempoLimiteAtingido() {

        long agora = System.nanoTime();
    
        return agora - inicioExecucao >= LIMITE_TEMPO_NS;
    }
}