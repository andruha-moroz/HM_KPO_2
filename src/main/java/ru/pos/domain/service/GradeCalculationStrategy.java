package ru.pos.domain.service;

import ru.pos.domain.model.*;
import java.util.List;
import java.util.Map;

/** Поведенческий паттерн Strategy: политика итогового расчёта заменяется без изменения доменной службы. */
public interface GradeCalculationStrategy {
    FinalGrade calculate(StudentId studentId, List<Criterion> rubric, Map<CriterionId, Evaluation> evaluations);
}
