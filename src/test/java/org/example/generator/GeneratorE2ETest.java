package org.example.generator;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorE2ETest {

    @Test
    void generateAndCheckIfCorrectImplFoundTest() throws IOException, InterruptedException {
        Path summaryFile = Path.of("tests_summary.txt");
        Files.deleteIfExists(summaryFile);

        runCommand("mvn exec:java@generate");
        runCommand("mvn test-compile && mvn exec:java@run-tests");

        assertTrue(Files.exists(summaryFile), "Summary file does not exist");
        assertEquals(1, Files.readAllLines(summaryFile).size(), "There should be exactly one correct implementation");
    }

    private static void runCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("bash", "-c", command);
        }

        builder.redirectErrorStream(true);

        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Process finished with exit code: " + exitCode);
        }
    }
}
