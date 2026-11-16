package ru.pos;

import org.junit.jupiter.api.Test;
import ru.pos.domain.model.*;
import ru.pos.domain.service.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DomainRulesTest {
    private final EvaluationFactory factory = new EvaluationFactory(Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));

    @Test void evaluationCannotExceedCriterionMaximum() {
        Student student = Student.create("Анна");
        Criterion criterion = Criterion.create("Проект", new BigDecimal("10"), Weight.of("60"));
        assertThrows(DomainException.class, () -> factory.create(student, criterion, Score.of("10.01"), ""));
    }

    @Test void scoreAndWeightValueObjectsRejectInvalidValues() {
        assertThrows(DomainException.class, () -> Score.of("-1"));
        assertThrows(DomainException.class, () -> Weight.of("0"));
        assertThrows(DomainException.class, () -> Student.create("   "));
    }

    @Test void weightedGradeUsesFullRubricAndReportsCompletion() {
        Student student = Student.create("Анна");
        Criterion project = Criterion.create("Проект", new BigDecimal("10"), Weight.of("60"));
        Criterion defense = Criterion.create("Защита", new BigDecimal("10"), Weight.of("40"));
        Evaluation evaluation = factory.create(student, project, Score.of("9"), "");
        FinalGrade partial = new WeightedAverageStrategy().calculate(student.id(), List.of(project, defense), Map.of(project.id(), evaluation));
        assertEquals(new BigDecimal("5.40"), partial.pointsOutOfTen());
        assertEquals(new BigDecimal("60.0"), partial.completionPercent());
        Evaluation defenseEvaluation = factory.create(student, defense, Score.of("8"), "");
        FinalGrade complete = new FinalGradeDomainService(new WeightedAverageStrategy()).calculate(student, List.of(project, defense), List.of(evaluation, defenseEvaluation));
        assertEquals(new BigDecimal("8.60"), complete.pointsOutOfTen());
        assertEquals(2, complete.evaluatedCriteria());
    }
}
