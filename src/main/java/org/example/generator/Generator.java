package org.example.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.example.web.model.ImplementationBatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.example.generator.BugRegistry.*;


public class Generator {

    private static final Random random = new Random();

    public static void main(String[] args) {
        String fileToSaveIn = "src/main/java/org/example/impl";
        generate(1000, "src/main/java/org/example/Engine.java", fileToSaveIn);
    }

    public static void generate(int num, String enginePath, String saveImplFolder) {
        File folder = new File(saveImplFolder);
        if (!folder.exists()) {
            try {
                Files.createDirectory(Path.of(saveImplFolder));
            } catch (IOException e) {
                System.err.println("Error creating directory: " + saveImplFolder);
            }
        }

        int correctIndex = random.nextInt(num);

        for (int i = 0; i < num; i++) {
            String className = "Engine" + i;
            boolean isCorrect = (i == correctIndex);
            doImplementation(enginePath, saveImplFolder, className, isCorrect);
        }
    }

    public static ImplementationBatch generateWebApi(int num, String enginePath, String saveImplFolder) {
        ImplementationBatch batch = new ImplementationBatch();

        List<String> implementations = new ArrayList<>();
        int correctIndex = random.nextInt(num);

        for (int i = 0; i < num; i++) {
            String className = "Engine" + i;
            implementations.add(
                    doImplementationWebApi(enginePath,
                            saveImplFolder,
                            className,
                            i == correctIndex)
            );
        }
        batch.setImplementations(implementations);
        batch.setCorrectImplementation(correctIndex);
        return batch;
    }

    private static String doImplementationWebApi(String enginePath, String saveImplFolder, String className, boolean isCorrect) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = null;
        try {
            cu = parser.parse(new File(enginePath))
                    .getResult().orElseThrow();

            makeImplementation(saveImplFolder, className, isCorrect, cu);
        } catch (FileNotFoundException e) {
            System.err.println("Error parsing file: " + enginePath);
        }

        return cu != null ? cu.toString() : null;
    }

    private static void doImplementation(String enginePath, String saveImplFolder, String className, boolean isCorrect) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu;

        try {
            cu = parser.parse(new File(enginePath))
                    .getResult().orElseThrow();
            makeImplementation(saveImplFolder, className, isCorrect, cu);
            Files.writeString(Path.of(saveImplFolder, className + ".java"), cu.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Error parsing engine: " + enginePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + saveImplFolder + "/" + className + ".java");
        }
    }

    private static void makeImplementation(String saveImplFolder, String className, boolean isCorrect, CompilationUnit cu) {
        cu.setPackageDeclaration(saveImplFolder.substring(saveImplFolder.indexOf("java") + 5).replace("/", "."));
        cu.findAll(ConstructorDeclaration.class)
                .forEach(constructorDeclaration -> constructorDeclaration.setName(className));
        cu.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.setName(className));
        if (!isCorrect) {
            makeRandomBugs(cu);
        }
    }

    private static void makeRandomBugs(CompilationUnit cu) {
        String[] methodNames = getAllMethodNames();
        int methodCounter = 0;
        Set<Integer> used = new HashSet<>();
        while (true) {
            methodCounter = methodCounter + random.nextInt(5);
            if (used.contains(methodCounter)) continue;
            if (methodCounter < methodNames.length) {
                used.add(methodCounter);
                String chosenMethod = methodNames[methodCounter];
                int bugIndex = random.nextInt(getBugMethodsAmount(chosenMethod));
                Optional<BugMutation> bugOpt = getBug(chosenMethod, bugIndex);
                MethodDeclaration m = cu.findFirst(MethodDeclaration.class,
                                methodDeclaration -> methodDeclaration.getNameAsString().equals(chosenMethod)).
                        orElseThrow();

                bugOpt.get().apply(m);
            } else break;
        }
    }
}
