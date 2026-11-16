package ru.pos.domain.model;

import java.math.BigDecimal;

public record Weight(BigDecimal value) {
    public Weight {
        if (value == null || value.signum() <= 0) throw new DomainException("Вес критерия должен быть больше нуля");
        value = value.stripTrailingZeros();
    }
    public static Weight of(String value) { return new Weight(new BigDecimal(value.trim().replace(',', '.'))); }
    public static Weight of(BigDecimal value) { return new Weight(value); }
    @Override public String toString() { return value.toPlainString(); }
}
