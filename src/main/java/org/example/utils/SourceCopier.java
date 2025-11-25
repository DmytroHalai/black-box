package org.example.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

public class SourceCopier {

    private static final Pattern PRIVATE_BLOCK_PATTERN = Pattern.compile(
            "//begin of private[\\s\\S]*?//end of private", Pattern.MULTILINE);

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Enter the path to project root folder");
            System.err.println("Example: java SourceCopier D:/Backup");
            System.exit(1);
        }

        Path targetRoot = Paths.get(args[0]);
        if (!Files.exists(targetRoot)) {
            System.out.println("Target folder does not exist — creating...");
            Files.createDirectories(targetRoot);
        }

        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path srcMain = projectRoot.resolve("src/main/java");
        Path srcTest = projectRoot.resolve("src/test/java");

        List<Path> itemsToCopy = List.of(
                srcMain.resolve("org/example/logic"),
                srcMain.resolve("org/example/runner"),
                srcTest.resolve("org/example/logic/api")
        );

        for (Path source : itemsToCopy) {
            if (Files.exists(source)) {
                Path relative = projectRoot.relativize(source);
                Path target = targetRoot.resolve(relative);
                copyAndClean(source, target);
            } else {
                System.out.println("Skip: " + source + " (not found)");
            }
        }

        System.out.println("All files copied and cleaned successfully!");
    }

    private static void copyAndClean(Path source, Path target) throws IOException {
        if (Files.isRegularFile(source)) {
            Files.createDirectories(target.getParent());

            if (!source.toString().endsWith(".java")) {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                return;
            }

            String content = Files.readString(source, StandardCharsets.UTF_8);
            String cleaned = PRIVATE_BLOCK_PATTERN.matcher(content).replaceAll("");
            Files.writeString(target, cleaned, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return;
        }

        try (var paths = Files.walk(source)) {
            paths.forEach(src -> {
                try {
                    Path dest = target.resolve(source.relativize(src));
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.createDirectories(dest.getParent());
                        if (src.toString().endsWith(".java")) {
                            String content = Files.readString(src, StandardCharsets.UTF_8);
                            String cleaned = PRIVATE_BLOCK_PATTERN.matcher(content).replaceAll("");
                            Files.writeString(dest, cleaned, StandardCharsets.UTF_8,
                                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        } else {
                            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error occurred while copying: " + src, e);
                }
            });
        }
    }
}
