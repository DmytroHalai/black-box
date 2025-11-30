package org.example.web.controller;

import org.example.generator.Generator;
import org.example.web.model.CheckResult;
import org.example.web.model.ImplementationBatch;
import org.example.web.model.Student;
import org.example.web.service.StudentService;
import org.example.web.utils.Message;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppController.class)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void StudentController_GetAllStudents_ReturnAllStudents() throws Exception {
        List<Student> students = init();

        when(studentService.findAll()).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/all/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))

                .andExpect(jsonPath("$[0].name").value(students.getFirst().getName()))
                .andExpect(jsonPath("$[0].correctImpl").value(students.getFirst().getCorrectImpl()))
                .andExpect(jsonPath("$[0].checkResults.length()").value(students.getFirst().getCheckResults().size()))

                .andExpect(jsonPath("$[1].name").value(students.get(1).getName()))
                .andExpect(jsonPath("$[1].correctImpl").value(students.get(1).getCorrectImpl()));

        verify(studentService, times(1)).findAll();
    }

    @Test
    void StudentController_CheckImpl() throws Exception {
        Student student1 = new Student();
        student1.setName("John");
        student1.setCorrectImpl(123);

        when(studentService.findByName(student1.getName())).thenReturn(student1);
        when(studentService.isStudentExist(student1.getName())).thenReturn(true);

        StringBuilder urlBuilder = new StringBuilder("/check/" + student1.getName() + "/");
        String correctImpl = urlBuilder.append(student1.getCorrectImpl()).toString();
        String incorrectImpl = urlBuilder.append(1).toString();
        System.out.println(incorrectImpl);

        mockMvc.perform(get(correctImpl))
                .andExpect(status().isOk())
                .andExpect(content().string(Message.DONE.getText()));

        mockMvc.perform(get(incorrectImpl))
                .andExpect(status().isOk())
                .andExpect(content().string(Message.WRONG.getText()));
    }

    @Test
    void StudentController_CheckImpl_BadRequestStudentName() throws Exception {
        String incorrectName = "don't exist";
        when(studentService.findByName(incorrectName)).thenReturn(null);
        mockMvc.perform(get("/check/" + incorrectName + "/111"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void StudentController_CheckImpl_BadRequestImplNum() throws Exception {
        String NAN = "don't exist";
        Student student = new Student();
        student.setName("John");
        student.setCorrectImpl(123);
        when(studentService.findByName(student.getName())).thenReturn(student);
        mockMvc.perform(get("/check/" + student.getName() + "/" + NAN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void StudentController_GetAllActiveStudents_ReturnAllActiveStudents() throws Exception {
        List<Student> students = init();

        when(studentService.findAllActive()).thenReturn(students);

        mockMvc.perform(get("/all/active/admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value(students.getFirst().getName()))
                .andExpect(jsonPath("$[0].correctImpl").value(students.getFirst().getCorrectImpl()))
                .andExpect(jsonPath("$[0].checkResults.length()").value(students.getFirst().getCheckResults().size()))
                .andExpect(jsonPath("$[0].checkResults[0].correct").value(students.getFirst().getCheckResults().getFirst().isCorrect()))

                .andExpect(jsonPath("$[1].name").value(students.get(1).getName()))
                .andExpect(jsonPath("$[1].correctImpl").value(students.get(1).getCorrectImpl()))
                .andExpect(jsonPath("$[1].checkResults.length()").value(students.get(1).getCheckResults().size()))
                .andExpect(jsonPath("$[1].checkResults[0].correct").value(students.get(1).getCheckResults().getFirst().isCorrect()))

                .andExpect(jsonPath("$[2].name").value(students.get(2).getName()))
                .andExpect(jsonPath("$[2].correctImpl").value(students.get(2).getCorrectImpl()))
                .andExpect(jsonPath("$[2].checkResults.length()").value(students.get(2).getCheckResults().size()))
                .andExpect(jsonPath("$[2].checkResults[0].correct").value(students.get(2).getCheckResults().getFirst().isCorrect()));
    }

    @Test
    void StudentController_GetAllActiveStudents_EmptyList() throws Exception {
        List<Student> students = new ArrayList<>();
        when(studentService.findAllActive()).thenReturn(students);
        mockMvc.perform(get("/all/active/admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void StudentController_GetAllSolvedStudents_ReturnAllSolved() throws Exception {
        List<Student> students = init();

        when(studentService.findSolved()).thenReturn(students);

        mockMvc.perform(get("/all/solved/admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value(students.getFirst().getName()))
                .andExpect(jsonPath("$[0].correctImpl").value(students.getFirst().getCorrectImpl()))
                .andExpect(jsonPath("$[0].checkResults.length()").value(students.getFirst().getCheckResults().size()))

                .andExpect(jsonPath("$[1].name").value(students.get(1).getName()))
                .andExpect(jsonPath("$[1].correctImpl").value(students.get(1).getCorrectImpl()))
                .andExpect(jsonPath("$[1].checkResults.length()").value(students.get(1).getCheckResults().size()))

                .andExpect(jsonPath("$[2].name").value(students.get(2).getName()))
                .andExpect(jsonPath("$[2].correctImpl").value(students.get(2).getCorrectImpl()))
                .andExpect(jsonPath("$[2].checkResults.length()").value(students.get(2).getCheckResults().size()));
    }

    @Test
    void StudentController_GetAllSolvedStudents_EmptyList() throws Exception {
        List<Student> students = new ArrayList<>();
        when(studentService.findSolved()).thenReturn(students);
        mockMvc.perform(get("/all/solved/admin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void generate_createsZipWithImplementations_andSavesStudent() throws Exception {
        ImplementationBatch mockBatch = mock(ImplementationBatch.class);
        when(mockBatch.getImplementations()).thenReturn(
                List.of(
                        "public class Engine0 {}",
                        "public class Engine1 {}"
                )
        );
        when(mockBatch.getCorrectImplementation()).thenReturn(42);

        try (MockedStatic<Generator> generatorMock = Mockito.mockStatic(Generator.class)) {
            generatorMock.when(() -> Generator.generateWebApi(
                    anyInt(),
                    anyString(),
                    anyString()
            )).thenReturn(mockBatch);

            when(studentService.isStudentExist("John")).thenReturn(false);

            MvcResult mvcResult = mockMvc.perform(get("/generate/John"))
                    .andExpect(request().asyncStarted())  // ← важливо!
                    .andReturn();

            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .andExpect(result -> {
                        byte[] contentAsBytes = result.getResponse().getContentAsByteArray();
                        assertThat(contentAsBytes).startsWith(new byte[]{80, 75});
                    });
        }
    }

    private List<Student> init() {
        Student student1 = new Student();
        student1.setName("John");
        student1.setCorrectImpl(123);
        student1.addCheckResult(new CheckResult(LocalDateTime.now(), false));
        student1.addCheckResult(new CheckResult(LocalDateTime.now().plusDays(1), true));

        Student student2 = new Student();
        student2.setName("Jane");
        student2.setCorrectImpl(456);
        student2.addCheckResult(new CheckResult(LocalDateTime.now().plusDays(1), true));

        Student student3 = new Student();
        student3.setName("Joe");
        student3.setCorrectImpl(789);
        student3.addCheckResult(new CheckResult(LocalDateTime.now().plusDays(1), true));

        return Arrays.asList(student1, student2, student3);
    }
}