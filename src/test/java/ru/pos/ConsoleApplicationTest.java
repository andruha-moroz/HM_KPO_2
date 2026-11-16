package ru.pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.pos.application.PosFacade;
import ru.pos.domain.model.*;
import ru.pos.domain.service.*;
import ru.pos.domain.port.*;
import ru.pos.infrastructure.csv.*;
import ru.pos.infrastructure.export.CsvFinalGradeExporter;
import ru.pos.presentation.ConsoleApplication;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.Clock;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleApplicationTest {
    @TempDir Path directory;

    @Test void consoleRunsStudentRubricEvaluationGradeAndExportWorkflow() {
        Path export = directory.resolve("exports/grades.csv");
        PosFacade app = createApplication();
        String input = String.join("\n",
                "1", "1", "Alice",             // add student
                "1", "2",                       // list students
                "2", "1", "Homework", "10", "100", // add criterion
                "2", "2",                       // list criteria
                "3", "1", "1", "11", "",    // invalid score is rejected
                "3", "1", "1", "8.5", "good work", // valid assessment
                "4", "1", "n",                 // inspect assessments
                "5", "1",                       // calculate final grade
                "6",                             // export CSV
                "0") + "\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (Scanner scanner = new Scanner(input);
             PrintStream capturedOut = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            new ConsoleApplication(app, export, scanner).run();
        } finally {
            System.setOut(originalOut);
        }
        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Alice"));
        assertTrue(text.contains("Ошибка: Балл 11"));
        assertTrue(text.contains("Итог Alice: 8.50/10"));
        assertTrue(Files.exists(export));
        assertEquals(1, app.evaluationsFor(app.students().get(0).id()).size());
    }

    @Test void facadeCanDeleteAssessmentAndRejectMissingRecords() {
        PosFacade app = createApplication();
        Student student = app.addStudent("Bob");
        Criterion criterion = app.addCriterion("Quiz", new BigDecimal("10"), Weight.of("100"));
        Evaluation evaluation = app.setEvaluation(student.id(), criterion.id(), Score.of("7"), "");
        app.deleteEvaluation(evaluation.id());

        assertTrue(app.evaluationsFor(student.id()).isEmpty());
        assertEquals(new BigDecimal("0.00"), app.finalGrade(student.id()).pointsOutOfTen());
        assertThrows(DomainException.class, () -> app.deleteEvaluation(evaluation.id()));
        StudentId unknown = StudentId.newId();
        assertThrows(DomainException.class, () -> app.finalGrade(unknown));
        assertThrows(DomainException.class, () -> app.evaluationsFor(unknown));
    }

    private PosFacade createApplication() {
        StudentRepository students = new CsvStudentRepository(directory);
        CriterionRepository criteria = new CsvCriterionRepository(directory);
        EvaluationRepository evaluations = new CsvEvaluationRepository(directory);
        return new PosFacade(students, criteria, evaluations, new EvaluationFactory(Clock.systemUTC()),
                new FinalGradeDomainService(new WeightedAverageStrategy()), new CsvFinalGradeExporter());
    }

}
