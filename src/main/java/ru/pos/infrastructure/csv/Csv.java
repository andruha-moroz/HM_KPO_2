package ru.pos.infrastructure.csv;

import java.util.ArrayList;
import java.util.List;

public final class Csv {
    private Csv() { }
    public static String row(String... fields) {
        return java.util.Arrays.stream(fields).map(Csv::escape).collect(java.util.stream.Collectors.joining(","));
    }
    private static String escape(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }
    public static List<String> parse(String line) {
        List<String> fields = new ArrayList<>(); StringBuilder current = new StringBuilder(); boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { current.append('"'); i++; }
                else quoted = !quoted;
            } else if (c == ',' && !quoted) { fields.add(current.toString()); current.setLength(0); }
            else current.append(c);
        }
        if (quoted) throw new IllegalArgumentException("Повреждённая CSV-строка: кавычка не закрыта");
        fields.add(current.toString()); return fields;
    }
}
