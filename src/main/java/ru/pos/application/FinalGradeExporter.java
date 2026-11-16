package ru.pos.application;

import ru.pos.domain.model.Student;
import ru.pos.domain.model.FinalGrade;
import java.nio.file.Path;
import java.util.List;

public interface FinalGradeExporter {
    Path export(List<Student> students, List<FinalGrade> grades, Path destination);
}
