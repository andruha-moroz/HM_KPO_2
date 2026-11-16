package ru.pos.domain.port;

import ru.pos.domain.model.Student;
import ru.pos.domain.model.StudentId;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void save(Student student);
    Optional<Student> findById(StudentId id);
    List<Student> findAll();
}
