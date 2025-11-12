package org.example.web.controller;

import org.example.generator.Generator;
import org.example.web.model.ImplementationBatch;
import org.example.web.model.CheckResult;
import org.example.web.model.Student;
import org.example.web.service.StudentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class AppController {
    StudentService studentService = new StudentService();

    @GetMapping("/generate/{studentData}")
    public ResponseEntity<StreamingResponseBody> generate(@PathVariable("studentData") String studentData) throws IOException {
        if (studentData == null || studentData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter valid data");
        } else if (studentData.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter valid data");
        } else if (studentService.isStudentExist(studentData)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already exist");
        }

        StreamingResponseBody body = outputStream -> {
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputStream))) {
                ImplementationBatch impls = Generator.generateInMemory(1000,
                        "src/main/java/org/example/Engine.java",
                        "src/main/java/org/example/impl");

                for (int i = 0; i < impls.getImplementations().size(); i++) {
                    String className = "Engine" + i + ".java";
                    byte[] content = impls.getImplementations().get(i).getBytes();
                    ZipEntry entry = new ZipEntry(className);
                    zos.putNextEntry(entry);
                    zos.write(content);
                    zos.closeEntry();
                }

                studentService.saveStudent(studentData, impls.getCorrectImplementation());

                zos.finish();
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"impl.zip\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }

    @GetMapping("/check/{studentData}/{implNum}")
    public ResponseEntity<String> check(
            @PathVariable("studentData") String studentData,
            @PathVariable("implNum") String implNum) throws IOException {

        Student studentInfo = studentService.findByName(studentData);
        if (studentInfo == null) {
            return ResponseEntity.notFound().build();
        }

        boolean correct = studentInfo.getCorrectImpl() == Integer.parseInt(implNum);
        studentInfo.addCheckResult(new CheckResult(LocalDateTime.now(), correct));

        studentService.updateStudent(studentInfo);

        return ResponseEntity.ok().body(correct ? "That's right! Well done!" : "Try again!!");
    }

    @GetMapping("/all/active/admin")
    public ResponseEntity<List<Student>> allActiveAdmin() throws IOException {
        List<Student> studentsInfo = studentService.findAllActive();
        return ResponseEntity.ok().body(studentsInfo);
    }

    @GetMapping("/all/admin")
    public ResponseEntity<List<Student>> allAdmin() throws IOException {
        return ResponseEntity.ok().body(studentService.findAll());
    }

    @GetMapping("/all/solved/admin")
    public ResponseEntity<List<Student>> solved() throws IOException {
        List<Student> studentsInfo = studentService.findSolved();
        return ResponseEntity.ok().body(studentsInfo);
    }
}
