package ru.pos.domain.model;

import java.util.UUID;

public record EvaluationId(UUID value) {
    public EvaluationId { if (value == null) throw new DomainException("ID оценки не может быть пустым"); }
    public static EvaluationId newId() { return new EvaluationId(UUID.randomUUID()); }
    public static EvaluationId of(String value) { return new EvaluationId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
