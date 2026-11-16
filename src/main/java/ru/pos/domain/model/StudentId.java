package ru.pos.domain.model;

import java.util.UUID;

public record StudentId(UUID value) {
    public StudentId { if (value == null) throw new DomainException("ID студента не может быть пустым"); }
    public static StudentId newId() { return new StudentId(UUID.randomUUID()); }
    public static StudentId of(String value) { return new StudentId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
