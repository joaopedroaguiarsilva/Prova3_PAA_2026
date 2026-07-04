package paa;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResultRow {
    private final LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();

    public ResultRow put(String key, Object value) {
        if (value == null) {
            values.put(key, "");
        } else {
            values.put(key, String.valueOf(value));
        }
        return this;
    }

    public String get(String key) {
        String value = values.get(key);
        return value == null ? "" : value;
    }

    public Map<String, String> values() {
        return values;
    }
}
