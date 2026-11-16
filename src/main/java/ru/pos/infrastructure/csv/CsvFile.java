package ru.pos.infrastructure.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

final class CsvFile {
    private CsvFile() { }

    static List<List<String>> read(Path path, String header) {
        try {
            if (!Files.exists(path)) {
                write(path, header, List.of());
                return List.of();
            }
            List<List<String>> rows = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                reader.readLine(); // header
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isBlank()) rows.add(Csv.parse(line));
                }
            }
            return rows;
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать " + path + ": " + e.getMessage(), e);
        }
    }

    static void write(Path path, String header, List<String> rows) {
        try {
            Files.createDirectories(path.getParent());
            Path temp = path.resolveSibling(path.getFileName() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(header);
                writer.newLine();
                for (String row : rows) {
                    writer.write(row);
                    writer.newLine();
                }
            }
            try {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить " + path + ": " + e.getMessage(), e);
        }
    }
}
