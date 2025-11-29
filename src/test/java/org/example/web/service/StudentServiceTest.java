package org.example.web.service;

import org.example.web.model.CheckResult;
import org.example.web.model.Student;
import org.example.web.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void findByName_whenStudentExists_returnsStudent() {
        String studentName = "Test";
        Student expectedStudent = new Student();
        expectedStudent.setName(studentName);
        expectedStudent.setCorrectImpl(1);

        when(studentRepository.getStudent(studentName)).thenReturn(expectedStudent);

        // Act
        Student actualStudent = studentService.findByName(studentName);

        // Assert
        assertNotNull(actualStudent);
        assertEquals(expectedStudent.getName(), actualStudent.getName());
        assertEquals(expectedStudent.getCorrectImpl(), actualStudent.getCorrectImpl());

        verify(studentRepository, times(1)).getStudent(studentName);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    void findByName_whenStudentNotExists_returnsNull() {
        // Arrange
        when(studentRepository.getStudent(anyString())).thenReturn(null);

        // Act
        Student result = studentService.findByName("not exist");

        // Assert
        assertNull(result);
        verify(studentRepository, only()).getStudent("not exist");
    }

    @Test
    void saveStudent_successfullySavesStudent() {
        // Arrange
        String studentName = "Test";
        int correctImpl = 1;

        doNothing().when(studentRepository).save(any(Student.class));

        // Act
        studentService.saveStudent(studentName, correctImpl);

        // Assert
        verify(studentRepository, times(1)).save(argThat(student ->
                student != null &&
                        studentName.equals(student.getName()) &&
                        correctImpl == student.getCorrectImpl()
        ));
    }

    @Test
    void saveStudent_withNullName_stillCallsRepository() {
        // Act
        studentService.saveStudent(null, 50);

        // Assert
        verify(studentRepository, times(1)).save(argThat(student ->
                student != null &&
                        student.getName() == null &&
                        student.getCorrectImpl() == 50
        ));
    }

    @Test
    void findSolved_returnsEmptyList_whenNoOneHasCorrectAnswer() {
        when(studentRepository.getStudents()).thenReturn(List.of(
                studentWithOnlyWrong(),
                studentWithNoChecks(),
                studentWithOnlyWrong()
        ));

        assertTrue(studentService.findSolved().isEmpty());
    }

    @Test
    void findSolved_returnsEmptyList_whenAllStudentsHaveEmptyCheckResults() {
        when(studentRepository.getStudents()).thenReturn(List.of(
                studentWithNoChecks(),
                studentWithNoChecks()
        ));

        assertTrue(studentService.findSolved().isEmpty());
    }

    @Test
    void findSolved_returnsOriginalStudentInstances_notCopies() {
        Student original = new Student();
        original.setName("John");
        original.addCheckResult(correct());

        when(studentRepository.getStudents()).thenReturn(new ArrayList<>(List.of(original)));

        List<Student> result = studentService.findSolved();

        assertEquals(1, result.size());
        assertSame(original, result.getFirst());
    }

    private CheckResult correct() {
        CheckResult r = new CheckResult();
        r.setCorrect(true);
        return r;
    }

    private CheckResult wrong() {
        CheckResult r = new CheckResult();
        r.setCorrect(false);
        return r;
    }

    private Student studentWithOnlyWrong() {
        Student s = new Student();
        s.addCheckResult(wrong());
        return s;
    }

    private Student studentWithNoChecks() {
        Student s = new Student();
        s.addCheckResult(new CheckResult());
        return s;
    }
}
