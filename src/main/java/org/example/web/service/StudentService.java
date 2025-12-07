package org.example.web.service;

import org.example.web.model.CheckResult;
import org.example.web.model.Student;
import org.example.web.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {
    StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private static boolean isStrangeCheck(LocalDateTime prev, LocalDateTime next, int diffSeconds) {
        return Math.abs(ChronoUnit.SECONDS.between(prev, next)) <= diffSeconds;
    }

    public Student findByName(String studentName) {
        return studentRepository.getStudent(studentName);
    }

    public void saveStudent(String studentData, int correctImplementation) {
        Student student = new Student();
        student.setCorrectImpl(correctImplementation);
        student.setName(studentData);
        studentRepository.save(student);
    }

    public void updateStudent(Student updatedStudent) {
        List<Student> students = studentRepository.getStudents();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getName().equals(updatedStudent.getName())) {
                students.set(i, updatedStudent);
                break;
            }
        }
        studentRepository.saveAll(students);
    }

    public boolean isStudentExist(String studentName) {
        return studentRepository.getStudent(studentName) != null;
    }

    public List<Student> findAllActive() {
        return studentRepository.getStudents().stream().filter(s -> !s.getCheckResults().isEmpty()).toList();
    }

    public List<Student> findSolved() {
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

    public List<Student> findAll() {
        return studentRepository.getStudents();
    }

    public Map<Integer, List<String>> findStrangeChecks() {
        Map<Integer, List<String>> strangeChecksStudentsList = new HashMap<>();
        List<Student> students = findAllActive();
        for (Student student : students) {
            List<CheckResult> checkResults = student.getCheckResults();
            LocalDateTime firstCheck = checkResults.getFirst().getTimestamp();
            LocalDateTime lastCheck = checkResults.getLast().getTimestamp();
            int strangerCount = 0;
            if (isStrangeCheck(firstCheck, lastCheck, 5)) strangerCount++;
            for (int i = 1; i < checkResults.size(); i++) {
                LocalDateTime prev = checkResults.get(i - 1).getTimestamp();
                LocalDateTime next = checkResults.get(i).getTimestamp();
                if (isStrangeCheck(prev, next, 3)) strangerCount++;
            }
            if (strangerCount >= 5)
                strangeChecksStudentsList
                        .computeIfAbsent(strangerCount, k -> new ArrayList<>())
                        .add(student.getName());
        }
        return strangeChecksStudentsList;
    }
}
