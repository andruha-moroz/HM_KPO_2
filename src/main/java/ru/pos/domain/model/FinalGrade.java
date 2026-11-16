package ru.pos.domain.model;

import java.math.BigDecimal;

public record FinalGrade(StudentId studentId, BigDecimal pointsOutOfTen, BigDecimal completionPercent,
                         int evaluatedCriteria, int totalCriteria) {
    public FinalGrade {
        if (studentId == null || pointsOutOfTen == null || completionPercent == null)
            throw new DomainException("Итог содержит незаполненные обязательные поля");
        if (pointsOutOfTen.signum() < 0 || pointsOutOfTen.compareTo(BigDecimal.TEN) > 0)
            throw new DomainException("Итоговый балл должен находиться в диапазоне 0..10");
        if (completionPercent.signum() < 0 || completionPercent.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new DomainException("Покрытие рубрикатора должно находиться в диапазоне 0..100%");
        if (evaluatedCriteria < 0 || totalCriteria < evaluatedCriteria)
            throw new DomainException("Некорректное количество оценённых критериев");
        pointsOutOfTen = pointsOutOfTen.setScale(2, java.math.RoundingMode.HALF_UP);
        completionPercent = completionPercent.setScale(1, java.math.RoundingMode.HALF_UP);
    }
}
