package paa;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        String mode = args.length == 0 ? "all" : args[0].toLowerCase(Locale.ROOT);
        Path root = Paths.get(System.getProperty("user.dir"));

        List<ResultRow> rows = new ArrayList<ResultRow>();
        if ("q2".equals(mode) || "all".equals(mode)) {
            rows.addAll(LisExperiment.run(root));
        }
        if ("q3".equals(mode) || "all".equals(mode)) {
            rows.addAll(KnapsackExperiment.run(root));
        }

        CsvUtil.write(root.resolve("results.csv"), rows);
        System.out.println("Arquivo results.csv gerado com " + rows.size() + " linhas de resultados.");
    }
}
