package ru.pos.domain.service;

import ru.pos.domain.model.*;
import java.time.Clock;

/** Фабрика оценок: единственная точка создания оценки, проверяющая границу критерия. */
public final class EvaluationFactory {
    private final Clock clock;
    public EvaluationFactory(Clock clock) { this.clock = clock; }

    public Evaluation create(Student student, Criterion criterion, Score score, String comment) {
        if (student == null || criterion == null || score == null) throw new DomainException("Для оценки нужны студент, критерий и балл");
        if (score.value().compareTo(criterion.maxScore()) > 0)
            throw new DomainException("Балл " + score + " превышает максимум критерия «" + criterion.name() + "» (" + criterion.maxScore() + ")");
        return new Evaluation(EvaluationId.newId(), student.id(), criterion.id(), score, comment, clock.instant());
    }
}
