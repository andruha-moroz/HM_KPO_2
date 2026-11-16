package ru.pos.domain.model;

import java.time.Instant;

public record Evaluation(EvaluationId id, StudentId studentId, CriterionId criterionId,
                         Score score, String comment, Instant createdAt) {
    public Evaluation {
        if (id == null || studentId == null || criterionId == null || score == null || createdAt == null)
            throw new DomainException("Оценка содержит незаполненные обязательные поля");
        comment = comment == null ? "" : comment.trim();
        if (comment.contains("\n") || comment.contains("\r"))
            throw new DomainException("Комментарий к оценке должен быть в одну строку");
    }
}
