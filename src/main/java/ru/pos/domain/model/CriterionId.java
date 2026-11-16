package ru.pos.domain.model;

import java.util.UUID;

public record CriterionId(UUID value) {
    public CriterionId { if (value == null) throw new DomainException("ID критерия не может быть пустым"); }
    public static CriterionId newId() { return new CriterionId(UUID.randomUUID()); }
    public static CriterionId of(String value) { return new CriterionId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
