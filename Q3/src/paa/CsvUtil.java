package paa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvUtil {
    private static final String[] HEADER = new String[] {
            "section",
            "question",
            "instance",
            "algorithm",
            "configuration",
            "seed",
            "n",
            "capacity",
            "value",
            "weight",
            "items",
            "comparisons",
            "updates",
            "recursive_calls",
            "backtracks",
            "time_ms",
            "lis_length",
            "gap_abs",
            "gap_percent",
            "notes"
    };

    private CsvUtil() {
    }

    public static void write(Path file, List<ResultRow> rows) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        try {
            writer.write(joinCsv(HEADER));
            writer.newLine();
            for (ResultRow row : rows) {
                String[] values = new String[HEADER.length];
                for (int i = 0; i < HEADER.length; i++) {
                    values[i] = row.get(HEADER[i]);
                }
                writer.write(joinCsv(values));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public static String escape(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        boolean needsQuotes = text.indexOf(',') >= 0 || text.indexOf('"') >= 0 || text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0;
        if (!needsQuotes) {
            return text;
        }
        String escaped = text.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }

    private static String joinCsv(String[] values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(escape(values[i]));
        }
        return builder.toString();
    }
}
