package ru.pos.domain.model;

import java.math.BigDecimal;

public record Criterion(CriterionId id, String name, BigDecimal maxScore, Weight weight) {
    public Criterion {
        if (id == null || weight == null) throw new DomainException("У критерия должны быть ID и вес");
        if (name == null || name.isBlank()) throw new DomainException("Название критерия не может быть пустым");
        if (name.contains("\n") || name.contains("\r")) throw new DomainException("Название критерия должно быть в одну строку");
        if (maxScore == null || maxScore.signum() <= 0) throw new DomainException("Максимум баллов должен быть больше нуля");
        name = name.trim();
        maxScore = maxScore.stripTrailingZeros();
    }
    public static Criterion create(String name, BigDecimal maxScore, Weight weight) {
        return new Criterion(CriterionId.newId(), name, maxScore, weight);
    }
}
