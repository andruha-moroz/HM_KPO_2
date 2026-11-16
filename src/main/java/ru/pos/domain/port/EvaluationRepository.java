package ru.pos.domain.port;

import ru.pos.domain.model.Evaluation;
import ru.pos.domain.model.EvaluationId;
import ru.pos.domain.model.StudentId;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    void save(Evaluation evaluation);
    Optional<Evaluation> findByStudentAndCriterion(StudentId studentId, ru.pos.domain.model.CriterionId criterionId);
    Optional<Evaluation> findById(EvaluationId id);
    List<Evaluation> findByStudent(StudentId studentId);
    List<Evaluation> findAll();
    void delete(EvaluationId id);
}
