package org.example.web.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository<T> {
    private final Path filePath;
    private final ObjectMapper mapper;
    private final TypeReference<List<T>> typeRef;

    public JsonRepository(String pathString, TypeReference<List<T>> typeRef) {
        this.filePath = Path.of(pathString).toAbsolutePath().normalize();
        this.typeRef = typeRef;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);

        initFile();
    }

    private void initFile() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Files.writeString(filePath, "[]", StandardOpenOption.WRITE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize repository at: " + filePath, e);
        }
    }

    public List<T> findAll() {
        try {
            if (Files.notExists(filePath) || Files.size(filePath) == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(filePath.toFile(), typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON: " + filePath, e);
        }
    }

    public void saveAll(List<T> data) {
        try {
            mapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON: " + filePath, e);
        }
    }

    public synchronized void add(T entity) {
        List<T> list = findAll();
        list.add(entity);
        saveAll(list);
    }
}