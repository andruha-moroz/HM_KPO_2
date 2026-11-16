package ru.pos.domain.model;

public record Student(StudentId id, String name) {
    public Student {
        if (id == null) throw new DomainException("У студента должен быть ID");
        if (name == null || name.isBlank()) throw new DomainException("Имя студента не может быть пустым");
        if (name.contains("\n") || name.contains("\r")) throw new DomainException("Имя студента должно быть в одну строку");
        name = name.trim();
    }
    public static Student create(String name) { return new Student(StudentId.newId(), name); }
}
