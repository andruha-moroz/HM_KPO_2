package ru.pos.infrastructure.export;

import ru.pos.application.FinalGradeExporter;
import ru.pos.domain.model.*;
import ru.pos.infrastructure.csv.Csv;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class CsvFinalGradeExporter implements FinalGradeExporter {
    @Override public Path export(List<Student> students, List<FinalGrade> grades, Path destination) {
        try {
            Path parent = destination.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(Csv.row("student_id", "student_name", "grade_out_of_10", "completion_percent", "evaluated_criteria", "total_criteria"));
                writer.newLine();
                for (int i = 0; i < students.size(); i++) {
                    Student s = students.get(i);
                    FinalGrade g = grades.get(i);
                    writer.write(Csv.row(s.id().toString(), s.name(), g.pointsOutOfTen().toPlainString(),
                            g.completionPercent().toPlainString(), String.valueOf(g.evaluatedCriteria()), String.valueOf(g.totalCriteria())));
                    writer.newLine();
                }
            }
            return destination;
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось экспортировать ведомость: " + e.getMessage(), e);
        }
    }
}
