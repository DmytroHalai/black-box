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

        Path targetMain = Paths.get(args[0]);
        if (!Files.exists(targetMain)) {
            System.out.println("The give folder is not created yet...");
            Files.createDirectories(targetMain);
        }

        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path srcMain = projectRoot.resolve("src/main/java");
        Path srcTest = projectRoot.resolve("src/test/java");

        List<Path> foldersToCopy = List.of(
                srcMain.resolve("org/example/logic"),
                srcMain.resolve("org/example/runner"),
                srcTest.resolve("game")
        );

        for (Path source : foldersToCopy) {
            if (Files.exists(source)) {
                Path relative = projectRoot.relativize(source);
                Path target = targetMain.resolve(relative);
                copyAndClean(source, target);
            } else {
                System.out.println("Skip: " + source + " (not found)");
            }
        }

        System.out.println("All files are copied and cleaned successfully!");
    }

    private static void copyAndClean(Path source, Path target) throws IOException {
        Files.createDirectories(target);
        try (var paths = Files.walk(source)) {
            paths.forEach(src -> {
                try {
                    Path dest = target.resolve(source.relativize(src));
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        String content = Files.readString(src, StandardCharsets.UTF_8);
                        String cleaned = PRIVATE_BLOCK_PATTERN.matcher(content).replaceAll("");
                        Files.createDirectories(dest.getParent());
                        Files.writeString(dest, cleaned, StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error occurred: " + src, e);
                }
            });
        }
    }
}
