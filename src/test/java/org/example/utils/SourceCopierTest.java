package org.example.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SourceCopierTest {

    private final static String BEGIN_OF_PRIVATE = "//begin of private";
    private final static String END_OF_PRIVATE = "//end of private";
    private final static String PRIVATE_PART = "that's private part which is not included in copying process";
    private final static String PUBLIC_PART = "that's public part";
    @TempDir
    Path tempDir;
    private String originalUserDir;

    @BeforeEach
    void setUp() {
        originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toAbsolutePath().toString());
    }

    @AfterEach
    void tearDown() {
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void shouldCopyRepoAndCleanSecrets() throws IOException {
        // folders to copy from
        Path mainDirStudentRepoFolder = tempDir.resolve("student.repo");
        Path mainDirLogicFolder = tempDir.resolve("src/main/java/org/example/logic");
        Path mainDirRunnerFolder = tempDir.resolve("src/main/java/org/example/runner");
        Path mainDirTestFolder = tempDir.resolve("src/test/java/org/example/logic/api");

        // folder to copy to
        Path targetDir = tempDir.resolve("target-output");

        Files.createDirectories(mainDirStudentRepoFolder);
        Files.createDirectories(mainDirLogicFolder);
        Files.createDirectories(mainDirRunnerFolder);
        Files.createDirectories(mainDirTestFolder);

        //init of main dir files
        Path mainDirLogicFileExample = mainDirLogicFolder.resolve("Game.java");
        String mainDirLogicFileExampleCode = fillTheFile("Game", "play"); // name of class and method

        Path mainDirTestFileExample = mainDirTestFolder.resolve("GameTest.java");
        String mainDirTestFileExampleCode = fillTheFile("GameTest", "playTest");

        Path mainDirRunnerFileExample = mainDirRunnerFolder.resolve("GameRunner.java");
        String mainDirRunnerFileExampleCode = fillTheFile("GameRunner", "runTest");

        Path mainDirStudentReadmeFileExample = mainDirStudentRepoFolder.resolve("README.md");
        String mainDirStudentReadmeFileExampleFilling = """
                Hey there!
                That's README.md!!
                """;

        Path mainDirStudentPom = mainDirStudentRepoFolder.resolve("pom.xml");
        String pomXMLFilling = "<project>OLD VERSION</project>";

        // filling the main repo for copying
        Files.writeString(mainDirLogicFileExample, mainDirLogicFileExampleCode);
        Files.writeString(mainDirTestFileExample, mainDirTestFileExampleCode);
        Files.writeString(mainDirRunnerFileExample, mainDirRunnerFileExampleCode);
        Files.writeString(mainDirStudentReadmeFileExample, mainDirStudentReadmeFileExampleFilling);
        Files.writeString(mainDirStudentPom, pomXMLFilling);

        Files.createDirectories(targetDir.resolve("src/main/java/org/example/logic"));
        Files.writeString(targetDir.resolve("src/main/java/org/example/logic/Game.java"), "OLD CLASS");

        SourceCopier.main(new String[]{targetDir.toString()});

        Path targetRepoPomFileUpdated = targetDir.resolve("pom.xml");
        Path targetLogicGameClassUpdated = targetDir.resolve("src/main/java/org/example/logic/Game.java");
        Path targetTestGameClassUpdated = targetDir.resolve("src/test/java/org/example/logic/api/GameTest.java");
        Path targetRunnerClassUpdated = targetDir.resolve("src/main/java/org/example/runner/GameRunner.java");
        Path targetReadmeFileUpdated = targetDir.resolve("README.md");

        checkIfUpdated(targetRepoPomFileUpdated, pomXMLFilling);
        checkIfUpdated(targetLogicGameClassUpdated, "Game", PUBLIC_PART);
        checkIfUpdated(targetTestGameClassUpdated, "GameTest", PUBLIC_PART);
        checkIfUpdated(targetRunnerClassUpdated, "GameRunner", PUBLIC_PART);
        checkIfUpdated(targetReadmeFileUpdated, mainDirStudentReadmeFileExampleFilling);
    }

    private void checkIfUpdated(Path pathToFile, String ... expectedContentParts) throws IOException {
        String fileContent = Files.readString(pathToFile);
        assertThat(fileContent)
                .doesNotContain(BEGIN_OF_PRIVATE)
                .doesNotContain(END_OF_PRIVATE)
                .doesNotContain(PRIVATE_PART);

        for (String s : expectedContentParts) {
            assertThat(fileContent).contains(s);
        }
    }

    private String fillTheFile(String className, String methodName) {
        return """
                public class %s {
                    public void %s() {
                        %s
                        %s
                        %s
                        %s;
                    }
                }
                """.formatted(className, methodName, BEGIN_OF_PRIVATE, PRIVATE_PART, END_OF_PRIVATE, PUBLIC_PART);
    }
}