package org.example.web.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository<T> {
    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<List<T>> typeRef;

    public JsonRepository(String filePath, TypeReference<List<T>> typeRef) {
        this.file = new File(filePath);
        this.typeRef = typeRef;

        // 🔹 Реєструємо підтримку LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 🔹 Ініціалізуємо порожній файл при першому запуску
        try {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<T>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize JSON file: " + filePath, e);
        }
    }

    public List<T> findAll() throws IOException {
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        return mapper.readValue(file, typeRef);
    }

    public void saveAll(List<T> data) throws IOException {
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }

    public void add(T entity) throws IOException {
        List<T> list = findAll();
        list.add(entity);
        saveAll(list);
    }
}
