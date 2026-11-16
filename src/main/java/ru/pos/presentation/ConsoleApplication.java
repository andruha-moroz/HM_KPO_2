package ru.pos.presentation;

import ru.pos.application.PosFacade;
import ru.pos.domain.model.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Консольный primary adapter. Он отвечает за диалог и переводит ввод в вызовы фасада. */
public final class ConsoleApplication {
    private final PosFacade app;
    private final Path exportPath;
    private final Scanner input;
    public ConsoleApplication(PosFacade app, Path exportPath, Scanner input) {
        this.app = app; this.exportPath = exportPath; this.input = input;
    }

    public void run() {
        System.out.println("ПОС — Преподавательская Оценка Студентов");
        while (true) {
            System.out.println("\n1. Студенты  2. Критерии  3. Поставить/изменить оценку  4. Оценки студента  5. Итог студента  6. Экспорт CSV  0. Выход");
            try {
                String choice = ask("Выберите действие");
                switch (choice) {
                    case "1" -> studentsMenu();
                    case "2" -> criteriaMenu();
                    case "3" -> setEvaluation();
                    case "4" -> showEvaluations();
                    case "5" -> showGrade();
                    case "6" -> System.out.println("Ведомость сохранена: " + app.exportGrades(exportPath).toAbsolutePath());
                    case "0" -> { System.out.println("До свидания!"); return; }
                    default -> System.out.println("Выберите пункт меню от 0 до 6.");
                }
            } catch (java.util.NoSuchElementException e) { System.out.println("До свидания!"); return; }
            catch (DomainException | IllegalArgumentException e) { System.out.println("Ошибка: " + e.getMessage()); }
            catch (RuntimeException e) { System.out.println("Ошибка приложения: " + e.getMessage()); }
        }
    }
    private void studentsMenu() {
        System.out.println("\n1. Добавить студента  2. Список студентов");
        switch (ask("Действие")) {
            case "1" -> { Student s = app.addStudent(ask("ФИО студента")); System.out.println("Добавлен: " + s.name() + " [" + s.id() + "]"); }
            case "2" -> { List<Student> all = app.students(); if (all.isEmpty()) System.out.println("Студентов пока нет."); else all.forEach(s -> System.out.println(s.id() + " | " + s.name())); }
            default -> System.out.println("Неизвестное действие.");
        }
    }
    private void criteriaMenu() {
        System.out.println("\n1. Добавить критерий  2. Список критериев");
        switch (ask("Действие")) {
            case "1" -> {
                String name = ask("Название критерия"); BigDecimal max = decimal("Максимальный балл"); Weight weight = Weight.of(ask("Вес критерия (любое положительное число, например 40)"));
                Criterion c = app.addCriterion(name, max, weight); System.out.println("Добавлен: " + c.name() + " [id=" + c.id() + ", максимум=" + c.maxScore() + ", вес=" + c.weight() + "]");
            }
            case "2" -> { List<Criterion> all = app.criteria(); if (all.isEmpty()) System.out.println("Критериев пока нет."); else all.forEach(c -> System.out.printf("%s | %s | максимум %s | вес %s%n", c.id(), c.name(), c.maxScore(), c.weight())); }
            default -> System.out.println("Неизвестное действие.");
        }
    }
    private void setEvaluation() {
        List<Student> students = app.students(); List<Criterion> criteria = app.criteria();
        if (students.isEmpty() || criteria.isEmpty()) { System.out.println("Сначала добавьте хотя бы одного студента и один критерий."); return; }
        Student student = selectStudent(students); Criterion criterion = selectCriterion(criteria);
        Evaluation e = app.setEvaluation(student.id(), criterion.id(), Score.of(ask("Балл (от 0 до " + criterion.maxScore() + ")")), ask("Комментарий (можно оставить пустым)"));
        System.out.println("Оценка сохранена: " + e.score() + "/" + criterion.maxScore() + ". Если оценка уже была, она заменена.");
    }
    private void showEvaluations() {
        List<Student> students = app.students(); if (students.isEmpty()) { System.out.println("Студентов пока нет."); return; }
        Student student = selectStudent(students); List<Evaluation> grades = app.evaluationsFor(student.id());
        if (grades.isEmpty()) { System.out.println("У студента пока нет оценок."); return; }
        for (Evaluation e : grades) {
            String criterionName = app.criteria().stream().filter(c -> c.id().equals(e.criterionId())).map(Criterion::name).findFirst().orElse("[критерий удалён]");
            System.out.printf("%s | %s: %s | %s | %s%n", e.id(), criterionName, e.score(), e.comment(), e.createdAt());
        }
        if ("y".equalsIgnoreCase(ask("Отменить оценку? (y/N)"))) { app.deleteEvaluation(EvaluationId.of(ask("ID оценки"))); System.out.println("Оценка удалена."); }
    }
    private void showGrade() {
        List<Student> students = app.students(); if (students.isEmpty()) { System.out.println("Студентов пока нет."); return; }
        Student student = selectStudent(students); FinalGrade g = app.finalGrade(student.id());
        System.out.printf("Итог %s: %s/10; оценено критериев: %d/%d; покрытие веса: %s%%%n", student.name(), g.pointsOutOfTen(), g.evaluatedCriteria(), g.totalCriteria(), g.completionPercent());
    }
    private Student selectStudent(List<Student> students) {
        for (int i = 0; i < students.size(); i++) System.out.printf("%d. %s%n", i + 1, students.get(i).name());
        int n = integer("Номер студента"); if (n < 1 || n > students.size()) throw new IllegalArgumentException("Нет студента с таким номером"); return students.get(n - 1);
    }
    private Criterion selectCriterion(List<Criterion> criteria) {
        for (int i = 0; i < criteria.size(); i++) { Criterion c = criteria.get(i); System.out.printf("%d. %s (макс. %s, вес %s)%n", i + 1, c.name(), c.maxScore(), c.weight()); }
        int n = integer("Номер критерия"); if (n < 1 || n > criteria.size()) throw new IllegalArgumentException("Нет критерия с таким номером"); return criteria.get(n - 1);
    }
    private String ask(String prompt) { System.out.print(prompt + ": "); return input.nextLine().trim(); }
    private int integer(String prompt) { return Integer.parseInt(ask(prompt)); }
    private BigDecimal decimal(String prompt) { return new BigDecimal(ask(prompt).replace(',', '.')); }
}
