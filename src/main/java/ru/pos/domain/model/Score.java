package ru.pos.domain.model;

import java.math.BigDecimal;

public record Score(BigDecimal value) {
    public Score {
        if (value == null || value.signum() < 0) throw new DomainException("Балл должен быть неотрицательным числом");
        value = value.stripTrailingZeros();
    }
    public static Score of(String value) { return new Score(new BigDecimal(value.trim().replace(',', '.'))); }
    public static Score of(BigDecimal value) { return new Score(value); }
    @Override public String toString() { return value.toPlainString(); }
}
