package org.example.web.service;

import org.example.web.model.CheckResult;
import org.example.web.model.Student;
import org.example.web.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {
    StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student findByName(String studentName) throws IOException {
        return studentRepository.getStudent(studentName);
    }

    public void saveStudent(String studentData, int correctImplementation) throws IOException {
        Student student = new Student();
        student.setCorrectImpl(correctImplementation);
        student.setName(studentData);
        studentRepository.save(student);
    }

    public void updateStudent(Student updatedStudent) throws IOException {
        List<Student> students = studentRepository.getStudents();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getName().equals(updatedStudent.getName())) {
                students.set(i, updatedStudent);
                break;
            }
        }
        studentRepository.saveAll(students);
    }

    public boolean isStudentExist(String studentName) throws IOException {
        return studentRepository.getStudent(studentName) != null;
    }

    public List<Student> findAllActive() throws IOException {
        return studentRepository.getStudents().stream().filter(s -> !s.getCheckResults().isEmpty()).toList();
    }

    public List<Student> findSolved() throws IOException {
        List<Student> result = new ArrayList<>();
        List<Student> students = studentRepository.getStudents().stream()
                .filter(s -> !s.getCheckResults().isEmpty()).toList();
        for (Student student : students) {
            List<CheckResult> checkResults = student.getCheckResults();
            for (CheckResult checkResult : checkResults) {
                if (checkResult.isCorrect()) {
                    result.add(student);
                    break;
                }
            }
        }
        return result;
    }

    public List<Student> findAll() throws IOException {
        return studentRepository.getStudents();
    }
}
