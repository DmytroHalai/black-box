package org.example.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.web.model.CheckResult;
import org.example.web.model.Student;
import org.example.web.service.StudentService;
import org.example.web.utils.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppController.class)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void StudentController_GetAllStudents_ReturnAllStudents() throws Exception {
        // Arrange
        Student student1 = new Student();
        student1.setName("John");
        student1.setCorrectImpl(123);
        student1.addCheckResult(new CheckResult(LocalDateTime.now(), false));
        student1.addCheckResult(new CheckResult(LocalDateTime.now().plusDays(1), true));

        Student student2 = new Student();
        student2.setName("Jane");
        student2.setCorrectImpl(456);

        when(studentService.findAll()).thenReturn(Arrays.asList(student1, student2));

        // Act & Assert
        mockMvc.perform(get("/all/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))

                // First student
                .andExpect(jsonPath("$[0].name").value(student1.getName()))
                .andExpect(jsonPath("$[0].correctImpl").value(student1.getCorrectImpl()))
                .andExpect(jsonPath("$[0].checkResults.length()").value(student1.getCheckResults().size()))
                .andExpect(jsonPath("$[0].checkResults[0].correct").value(student1.getCheckResults().getFirst().isCorrect()))
                .andExpect(jsonPath("$[0].checkResults[1].correct").value(student1.getCheckResults().get(1).isCorrect()))

                // Second student
                .andExpect(jsonPath("$[1].name").value(student2.getName()))
                .andExpect(jsonPath("$[1].correctImpl").value(student2.getCorrectImpl()))
                .andExpect(jsonPath("$[1].checkResults").isEmpty());

        verify(studentService, times(1)).findAll();
    }

    //      /check/{studentData}/{implNum}

    @Test
    void StudentController_CheckCorrectImpl() throws Exception {
        Student student1 = new Student();
        student1.setName("John");
        student1.setCorrectImpl(123);

        when(studentService.findByName(student1.getName())).thenReturn(student1);
        StringBuilder urlBuilder = new StringBuilder("/check/" + student1.getName() + "/");
        String correctImpl = urlBuilder.append(student1.getCorrectImpl()).toString();
        String uncorrectImpl = urlBuilder.append(1).toString();
        System.out.println(uncorrectImpl);

        mockMvc.perform(get(correctImpl))
                .andExpect(status().isOk())
                .andExpect(content().string(Message.DONE.getText()));

        mockMvc.perform(get(uncorrectImpl))
                .andExpect(status().isOk())
                .andExpect(content().string(Message.WRONG.getText()));
    }
}