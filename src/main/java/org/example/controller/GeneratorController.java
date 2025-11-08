package org.example.controller;

import org.example.generator.Generator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class GeneratorController {

    @GetMapping("/generate")
    public ResponseEntity<StreamingResponseBody> generate() {
        StreamingResponseBody body = outputStream -> {
            try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputStream))) {
                List<String> impls = Generator.generateInMemory(1000, "src/main/java/org/example/Engine.java");

                for (int i = 0; i < impls.size(); i++) {
                    String className = "Engine" + i + ".java";
                    byte[] content = impls.get(i).getBytes();
                    ZipEntry entry = new ZipEntry(className);
                    zos.putNextEntry(entry);
                    zos.write(content);
                    zos.closeEntry();
                }

                zos.finish();
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"engines.zip\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }
}
