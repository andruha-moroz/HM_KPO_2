package ru.pos.infrastructure.csv;

import ru.pos.domain.model.*;
import ru.pos.domain.port.StudentRepository;
import java.nio.file.Path;
import java.util.*;

public final class CsvStudentRepository implements StudentRepository {
    private static final String HEADER = "id,name";
    private final Path file;
    public CsvStudentRepository(Path directory) { file = directory.resolve("students.csv"); }
    public synchronized void save(Student student) {
        Map<StudentId, Student> all = new LinkedHashMap<>(); findAll().forEach(s -> all.put(s.id(), s)); all.put(student.id(), student);
        List<String> rows = all.values().stream().map(s -> Csv.row(s.id().toString(), s.name())).toList(); CsvFile.write(file, HEADER, rows);
    }
    public synchronized Optional<Student> findById(StudentId id) { return findAll().stream().filter(s -> s.id().equals(id)).findFirst(); }
    public synchronized List<Student> findAll() {
        return CsvFile.read(file, HEADER).stream().map(r -> new Student(StudentId.of(r.get(0)), r.get(1))).toList();
    }
}
