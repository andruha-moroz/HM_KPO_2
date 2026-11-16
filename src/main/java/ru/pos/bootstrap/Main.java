package ru.pos.bootstrap;

import ru.pos.application.*;
import ru.pos.domain.model.*;
import ru.pos.domain.port.*;
import ru.pos.domain.service.*;
import ru.pos.infrastructure.csv.*;
import ru.pos.infrastructure.export.CsvFinalGradeExporter;
import ru.pos.presentation.ConsoleApplication;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        boolean demo = args.length > 0 && "--demo".equals(args[0]);
        Path data = Path.of(demo ? "data/demo" : "data");
        ServiceContainer container = configure(data);
        PosFacade facade = container.resolve(PosFacade.class);
        if (demo) runDemo(facade);
        else try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            new ConsoleApplication(facade, Path.of("exports/final_grades.csv"), scanner).run();
        }
    }
    private static ServiceContainer configure(Path dataDirectory) {
        ServiceContainer c = new ServiceContainer();
        c.registerInstance(Path.class, dataDirectory);
        c.register(Clock.class, Clock::systemUTC);
        c.register(StudentRepository.class, () -> new CsvStudentRepository(c.resolve(Path.class)));
        c.register(CriterionRepository.class, () -> new CsvCriterionRepository(c.resolve(Path.class)));
        c.register(EvaluationRepository.class, () -> new CsvEvaluationRepository(c.resolve(Path.class)));
        c.register(EvaluationFactory.class, () -> new EvaluationFactory(c.resolve(Clock.class)));
        c.register(GradeCalculationStrategy.class, WeightedAverageStrategy::new);
        c.register(FinalGradeDomainService.class, () -> new FinalGradeDomainService(c.resolve(GradeCalculationStrategy.class)));
        c.register(FinalGradeExporter.class, CsvFinalGradeExporter::new);
        c.register(PosFacade.class, () -> new PosFacade(c.resolve(StudentRepository.class), c.resolve(CriterionRepository.class),
                c.resolve(EvaluationRepository.class), c.resolve(EvaluationFactory.class), c.resolve(FinalGradeDomainService.class), c.resolve(FinalGradeExporter.class)));
        return c;
    }
    private static void runDemo(PosFacade app) {
        Student anna = app.students().stream().filter(s -> s.name().equals("Анна Смирнова")).findFirst().orElseGet(() -> app.addStudent("Анна Смирнова"));
        Student ivan = app.students().stream().filter(s -> s.name().equals("Иван Петров")).findFirst().orElseGet(() -> app.addStudent("Иван Петров"));
        Criterion project = app.criteria().stream().filter(c -> c.name().equals("Проект")).findFirst()
                .orElseGet(() -> app.addCriterion("Проект", new java.math.BigDecimal("10"), Weight.of("60")));
        Criterion defense = app.criteria().stream().filter(c -> c.name().equals("Защита")).findFirst()
                .orElseGet(() -> app.addCriterion("Защита", new java.math.BigDecimal("10"), Weight.of("40")));
        app.setEvaluation(anna.id(), project.id(), Score.of("9"), "Полный проект");
        app.setEvaluation(anna.id(), defense.id(), Score.of("8"), "Уверенная защита");
        app.setEvaluation(ivan.id(), project.id(), Score.of("7"), "");
        app.setEvaluation(ivan.id(), defense.id(), Score.of("9"), "");
        for (Student student : app.students()) {
            FinalGrade grade = app.finalGrade(student.id());
            System.out.printf("%-20s итог: %s/10, заполнено: %s%% (%d/%d критериев)%n", student.name(), grade.pointsOutOfTen(), grade.completionPercent(), grade.evaluatedCriteria(), grade.totalCriteria());
        }
        System.out.println("CSV экспортирован: " + app.exportGrades(Path.of("exports/demo_final_grades.csv")).toAbsolutePath());
    }
}
