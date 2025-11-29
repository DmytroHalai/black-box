package org.example.web.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.web.model.Student;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class StudentRepository {
    private final JsonRepository<Student> studentRepo = new JsonRepository<>(
            "data/check_results.json",
            new TypeReference<List<Student>>() {});

    public List<Student> getStudents() {
        return studentRepo.findAll();
    }

    public Student getStudent(String name) {
        return studentRepo.findAll().stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }

    public void save(Student student) {
        studentRepo.add(student);
    }

    public void saveAll(List<Student> students)  {
        studentRepo.saveAll(students);
    }
}
