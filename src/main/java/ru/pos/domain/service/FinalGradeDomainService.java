package ru.pos.domain.service;

import ru.pos.domain.model.*;
import java.util.List;
import java.util.Map;

/** Правило итоговой оценки зависит от рубрикатора и набора оценок, поэтому живёт в Domain Service. */
public final class FinalGradeDomainService {
    private final GradeCalculationStrategy strategy;
    public FinalGradeDomainService(GradeCalculationStrategy strategy) { this.strategy = strategy; }
    public FinalGrade calculate(Student student, List<Criterion> rubric, List<Evaluation> evaluations) {
        Map<CriterionId, Evaluation> byCriterion = evaluations.stream().collect(java.util.stream.Collectors.toMap(Evaluation::criterionId, e -> e, (a,b) -> b));
        return strategy.calculate(student.id(), rubric, byCriterion);
    }
}
