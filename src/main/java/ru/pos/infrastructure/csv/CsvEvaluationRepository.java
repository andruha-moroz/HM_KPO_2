package ru.pos.infrastructure.csv;

import ru.pos.domain.model.*;
import ru.pos.domain.port.EvaluationRepository;
import java.time.Instant;
import java.nio.file.Path;
import java.util.*;

public final class CsvEvaluationRepository implements EvaluationRepository {
    private static final String HEADER = "id,student_id,criterion_id,score,comment,created_at";
    private final Path file;
    public CsvEvaluationRepository(Path directory) { file = directory.resolve("evaluations.csv"); }
    public synchronized void save(Evaluation evaluation) {
        Map<EvaluationId, Evaluation> all = new LinkedHashMap<>(); findAll().forEach(e -> all.put(e.id(), e));
        findByStudentAndCriterion(evaluation.studentId(), evaluation.criterionId()).ifPresent(old -> all.remove(old.id()));
        all.put(evaluation.id(), evaluation); write(all.values());
    }
    private void write(Collection<Evaluation> evaluations) {
        List<String> rows = evaluations.stream().map(e -> Csv.row(e.id().toString(), e.studentId().toString(), e.criterionId().toString(), e.score().toString(), e.comment(), e.createdAt().toString())).toList();
        CsvFile.write(file, HEADER, rows);
    }
    public synchronized Optional<Evaluation> findByStudentAndCriterion(StudentId s, CriterionId c) { return findAll().stream().filter(e -> e.studentId().equals(s) && e.criterionId().equals(c)).findFirst(); }
    public synchronized Optional<Evaluation> findById(EvaluationId id) { return findAll().stream().filter(e -> e.id().equals(id)).findFirst(); }
    public synchronized List<Evaluation> findByStudent(StudentId studentId) { return findAll().stream().filter(e -> e.studentId().equals(studentId)).toList(); }
    public synchronized List<Evaluation> findAll() {
        return CsvFile.read(file, HEADER).stream().map(r -> new Evaluation(EvaluationId.of(r.get(0)), StudentId.of(r.get(1)), CriterionId.of(r.get(2)), Score.of(r.get(3)), r.get(4), Instant.parse(r.get(5)))).toList();
    }
    public synchronized void delete(EvaluationId id) { List<Evaluation> all = findAll().stream().filter(e -> !e.id().equals(id)).toList(); write(all); }
}
