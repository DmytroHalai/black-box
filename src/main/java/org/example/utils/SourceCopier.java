package org.example.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

public class SourceCopier {

    private static final Pattern PRIVATE_BLOCK_PATTERN = Pattern.compile(
            "//begin of private[\\s\\S]*?//end of private", Pattern.MULTILINE | Pattern.DOTALL);

    private static final List<String> ALWAYS_FRESH_FOLDERS = List.of(
            "src/main/java/org/example/logic",
            "src/main/java/org/example/runner",
            "src/test/java/org/example/logic/api"
    );

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java SourceCopier <target-folder>");
            System.exit(1);
        }

        Path targetRoot = Paths.get(args[0]).toAbsolutePath().normalize();
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path studentRepo = projectRoot.resolve("student.repo");

        if (!Files.exists(studentRepo)) {
            System.err.println("Error: folder 'student.repo' not found at " + studentRepo);
            System.exit(1);
        }

        deleteRecursively(targetRoot);
        Files.createDirectories(targetRoot);

        System.out.println("Copying base project from student.repo...");
        copyDirectoryWithCleaning(studentRepo, targetRoot);

        System.out.println("Overwriting fresh code from current project...");
        for (String freshPath : ALWAYS_FRESH_FOLDERS) {
            Path source = projectRoot.resolve(freshPath);
            if (Files.exists(source)) {
                Path target = targetRoot.resolve(freshPath);
                deleteIfExists(target);
                copyDirectoryWithCleaning(source, target);
                System.out.println("Updated: " + freshPath);
            } else {
                System.out.println("Not found in current project: " + freshPath);
            }
        }

        System.out.println("Done! Ready for student: " + targetRoot);
    }

    private static void copyDirectoryWithCleaning(Path sourceDir, Path targetDir) throws IOException {
        Files.walk(sourceDir).forEach(source -> {
            Path target = targetDir.resolve(sourceDir.relativize(source));
            try {
                if (Files.isDirectory(source)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    if (source.toString().endsWith(".java")) {
                        String content = Files.readString(source, StandardCharsets.UTF_8);
                        String cleaned = PRIVATE_BLOCK_PATTERN.matcher(content).replaceAll("");
                        Files.writeString(target, cleaned, StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    } else {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Copy failed: " + source + " → " + target, e);
            }
        });
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }

    private static void deleteIfExists(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }
}