package ru.pos.application;

import ru.pos.domain.model.*;
import ru.pos.domain.port.*;
import ru.pos.domain.service.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

/** GoF Facade: компактный API сценариев ПОС для консольного адаптера. */
public final class PosFacade {
    private final StudentRepository students;
    private final CriterionRepository criteria;
    private final EvaluationRepository evaluations;
    private final EvaluationFactory evaluationFactory;
    private final FinalGradeDomainService gradeService;
    private final FinalGradeExporter exporter;

    public PosFacade(StudentRepository students, CriterionRepository criteria, EvaluationRepository evaluations,
                     EvaluationFactory evaluationFactory, FinalGradeDomainService gradeService, FinalGradeExporter exporter) {
        this.students = students; this.criteria = criteria; this.evaluations = evaluations;
        this.evaluationFactory = evaluationFactory; this.gradeService = gradeService; this.exporter = exporter;
    }
    public Student addStudent(String name) { Student s = Student.create(name); students.save(s); return s; }
    public List<Student> students() { return students.findAll(); }
    public Criterion addCriterion(String name, BigDecimal maxScore, Weight weight) {
        Criterion c = Criterion.create(name, maxScore, weight); criteria.save(c); return c;
    }
    public List<Criterion> criteria() { return criteria.findAll(); }
    public Evaluation setEvaluation(StudentId studentId, CriterionId criterionId, Score score, String comment) {
        Student student = students.findById(studentId).orElseThrow(() -> new DomainException("Студент не найден"));
        Criterion criterion = criteria.findById(criterionId).orElseThrow(() -> new DomainException("Критерий отсутствует в рубрикаторе"));
        Evaluation e = evaluationFactory.create(student, criterion, score, comment); evaluations.save(e); return e;
    }
    public List<Evaluation> evaluationsFor(StudentId studentId) {
        if (students.findById(studentId).isEmpty()) throw new DomainException("Студент не найден");
        return evaluations.findByStudent(studentId);
    }
    public void deleteEvaluation(EvaluationId id) {
        if (evaluations.findById(id).isEmpty()) throw new DomainException("Оценка не найдена");
        evaluations.delete(id);
    }
    public FinalGrade finalGrade(StudentId studentId) {
        Student student = students.findById(studentId).orElseThrow(() -> new DomainException("Студент не найден"));
        return gradeService.calculate(student, criteria.findAll(), evaluations.findByStudent(studentId));
    }
    public Path exportGrades(Path path) {
        List<Student> studentList = students.findAll();
        List<FinalGrade> grades = studentList.stream().map(s -> finalGrade(s.id())).toList();
        return exporter.export(studentList, grades, path);
    }
}
