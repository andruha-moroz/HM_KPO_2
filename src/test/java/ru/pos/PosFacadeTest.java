package ru.pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.pos.application.PosFacade;
import ru.pos.domain.model.*;
import ru.pos.domain.service.*;
import ru.pos.infrastructure.csv.*;
import ru.pos.infrastructure.export.CsvFinalGradeExporter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Clock;
import static org.junit.jupiter.api.Assertions.*;

class PosFacadeTest {
    @TempDir Path directory;
    @Test void applicationSavesAndUpdatesOneEvaluationPerStudentAndCriterion() {
        PosFacade app = new PosFacade(new CsvStudentRepository(directory), new CsvCriterionRepository(directory),
                new CsvEvaluationRepository(directory), new EvaluationFactory(Clock.systemUTC()),
                new FinalGradeDomainService(new WeightedAverageStrategy()), new CsvFinalGradeExporter());
        Student student = app.addStudent("Иван Петров");
        Criterion criterion = app.addCriterion("Задание", new BigDecimal("10"), Weight.of("100"));
        app.setEvaluation(student.id(), criterion.id(), Score.of("7"), "черновик");
        app.setEvaluation(student.id(), criterion.id(), Score.of("9"), "исправлено");
        assertEquals(1, app.evaluationsFor(student.id()).size());
        assertEquals(new BigDecimal("9.00"), app.finalGrade(student.id()).pointsOutOfTen());
    }

    @Test void cannotEvaluateUnknownCriterion() {
        PosFacade app = new PosFacade(new CsvStudentRepository(directory), new CsvCriterionRepository(directory),
                new CsvEvaluationRepository(directory), new EvaluationFactory(Clock.systemUTC()),
                new FinalGradeDomainService(new WeightedAverageStrategy()), new CsvFinalGradeExporter());
        Student student = app.addStudent("Иван Петров");
        assertThrows(DomainException.class, () -> app.setEvaluation(student.id(), CriterionId.newId(), Score.of("1"), ""));
    }
}
