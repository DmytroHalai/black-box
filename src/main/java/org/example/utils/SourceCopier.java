package org.example.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Pattern;

public class SourceCopier {

    private static final Pattern PRIVATE_BLOCK_PATTERN = Pattern.compile("//begin of private[\\s\\S]*?//end of private", Pattern.MULTILINE | Pattern.DOTALL);

    private static final List<String> ALWAYS_FRESH_FOLDERS = List.of("src/main/java/org/example/logic", "src/main/java/org/example/runner", "src/test/java/org/example/logic/api");

    private static final String REPO_DIR_NAME = "student.repo";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }

        try {
            Path targetRoot = Path.of(args[0]).toAbsolutePath().normalize();
            Path projectRoot = Path.of(System.getProperty("user.dir"));
            Path studentRepo = projectRoot.resolve(REPO_DIR_NAME);

            if (!Files.exists(studentRepo)) {
                throw new FileNotFoundException("Base repository folder not found: " + studentRepo);
            }

            System.out.println("--- Starting Source Copier ---");

            System.out.println("[1/3] Cleaning target directory: " + targetRoot);
            deleteRecursively(targetRoot);
            Files.createDirectories(targetRoot);

            System.out.println("[2/3] Copying base project from " + REPO_DIR_NAME + "...");
            copyDirectoryWithCleaning(studentRepo, targetRoot);

            System.out.println("[3/3] Overwriting with fresh code...");
            for (String freshPathStr : ALWAYS_FRESH_FOLDERS) {
                Path sourcePath = projectRoot.resolve(freshPathStr);

                if (Files.exists(sourcePath)) {
                    Path targetPath = targetRoot.resolve(freshPathStr);
                    deleteRecursively(targetPath);
                    copyDirectoryWithCleaning(sourcePath, targetPath);
                    System.out.printf("   [+] Updated: %s%n", freshPathStr);
                } else {
                    System.out.printf("   [!] Warning: Fresh source not found: %s%n", freshPathStr);
                }
            }

            System.out.println("Target: " + targetRoot);

        } catch (Exception e) {
            System.err.println("\n*** FAILURE ***");
            System.err.println("Error details: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java SourceCopier <target-folder>");
        System.err.println("Example: java SourceCopier ../lab-1-student");
        System.exit(1);
    }

    private static void copyDirectoryWithCleaning(Path sourceDir, Path targetDir) {
        try {
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    Path targetPath = targetDir.resolve(sourceDir.relativize(dir));
                    try {
                        Files.createDirectories(targetPath);
                    } catch (IOException e) {
                        System.err.println("Error creating directory: " + targetPath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Path targetFile = targetDir.resolve(sourceDir.relativize(file));
                    if (file.toString().endsWith(".java")) {
                        try {
                            String content = Files.readString(file, StandardCharsets.UTF_8);
                            String cleaned = PRIVATE_BLOCK_PATTERN.matcher(content).replaceAll("");
                            Files.writeString(targetFile, cleaned, StandardCharsets.UTF_8,
                                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Error rw to file: " + targetFile);
                        }
                    } else {
                        try {
                            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println("Error copying file: " + file);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error copying source: " + sourceDir);
        }
    }

    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.err.println("Error deleting file: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    try {
                        Files.delete(dir);
                    } catch (IOException e) {
                        System.out.println("Error deleting directory: " + dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error deleting: " + path);
        }
    }
}