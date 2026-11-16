package ru.pos.domain.service;

import ru.pos.domain.model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/** Текущий балл = средневзвешенный процент набранных баллов, пересчитанный на шкалу 0..10. */
public final class WeightedAverageStrategy implements GradeCalculationStrategy {
    @Override public FinalGrade calculate(StudentId studentId, List<Criterion> rubric, Map<CriterionId, Evaluation> evaluations) {
        BigDecimal totalWeight = BigDecimal.ZERO, earned = BigDecimal.ZERO, completedWeight = BigDecimal.ZERO;
        int evaluated = 0;
        for (Criterion criterion : rubric) {
            BigDecimal weight = criterion.weight().value();
            totalWeight = totalWeight.add(weight);
            Evaluation evaluation = evaluations.get(criterion.id());
            if (evaluation != null) {
                BigDecimal ratio = evaluation.score().value().divide(criterion.maxScore(), 12, RoundingMode.HALF_UP);
                earned = earned.add(ratio.multiply(weight));
                completedWeight = completedWeight.add(weight);
                evaluated++;
            }
        }
        BigDecimal grade = totalWeight.signum() == 0 ? BigDecimal.ZERO : earned.multiply(BigDecimal.TEN).divide(totalWeight, 8, RoundingMode.HALF_UP);
        BigDecimal completion = totalWeight.signum() == 0 ? BigDecimal.ZERO : completedWeight.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 8, RoundingMode.HALF_UP);
        return new FinalGrade(studentId, grade, completion, evaluated, rubric.size());
    }
}
