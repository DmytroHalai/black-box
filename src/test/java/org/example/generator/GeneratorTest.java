package org.example.generator;

import org.example.web.model.ImplementationBatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    private static final Path ENGINE_PATH = Paths.get("src/main/java/org/example/Engine.java");

    private static final Path OUTPUT_DIR = Paths.get("src/test/java/org/example/testengines");

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(OUTPUT_DIR)) {
            deleteRecursively(OUTPUT_DIR);
        }
        Files.createDirectory(OUTPUT_DIR);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(OUTPUT_DIR)) {
            deleteRecursively(OUTPUT_DIR);
        }
    }

    @Test
    void testGenerate_createsFolderAndImplementationsAndMutatesSome() throws Exception {
        int num = 20;

        Generator.generate(
                num,
                ENGINE_PATH.toString(),
                OUTPUT_DIR.toString()
        );

        // directory exists
        assertTrue(Files.exists(OUTPUT_DIR), "Output directory must exist after generate()");

        // correct num of impls
        List<Path> javaFiles;
        try (var stream = Files.list(OUTPUT_DIR)) {
            javaFiles = stream.filter(p -> p.getFileName().toString().endsWith(".java")).toList();
        }

        assertEquals(num, javaFiles.size(),
                "Number of generated .java files must be equal to num");
    }

    @Test
    void testGenerateInMemory_returnsBatchWithCorrectSizeAndClassNames() throws Exception {
        int num = 20;

        ImplementationBatch batch = Generator.generateInMemory(
                num,
                ENGINE_PATH.toString(),
                OUTPUT_DIR.toString()
        );

        assertNotNull(batch, "Batch must not be null");

        List<String> implementations = batch.getImplementations();
        assertNotNull(implementations, "Implementations list must not be null");
        assertEquals(num, implementations.size(),
                "Implementations list size must be equal to num");

        int correctIndex = batch.getCorrectImplementation();
        assertTrue(correctIndex >= 0 && correctIndex < num,
                "Correct implementation index must be within [0, num)");

        String expectedPackageLine = "package org.example.testengines;";

        for (int i = 0; i < num; i++) {
            String impl = implementations.get(i);
            assertNotNull(impl, "Implementation source must not be null for index " + i);

            assertTrue(impl.contains(expectedPackageLine),
                    "Implementation " + i + " must contain correct package declaration");

            String classRegex = "class\\s+Engine" + i + "\\b";
            assertTrue(Pattern.compile(classRegex).matcher(impl).find(),
                    "Implementation " + i + " must contain class Engine" + i);
        }
    }

    private void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted((p1, p2) -> p2.getNameCount() - p1.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
