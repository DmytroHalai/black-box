package org.example.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.example.generator.BugRegistry.*;


public class Generator {

    public static void generate(int num, String enginePath, String saveImplFolder) throws FileNotFoundException {
        File folder = new File(saveImplFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (int i = 0; i < num; i++) {
            doImplementation(enginePath, saveImplFolder, "Engine" + i);
        }
    }

    private static void doImplementation(String enginePath, String saveImplFolder, String className) throws FileNotFoundException {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(new File(enginePath))
                .getResult().orElseThrow();

        cu.setPackageDeclaration(saveImplFolder.substring(saveImplFolder.indexOf("java") + 5).replace("/", "."));
        cu.findAll(ConstructorDeclaration.class)
                .forEach(constructorDeclaration -> constructorDeclaration.setName(className));
        cu.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.setName(className));
        makeRandomBugs(cu);

        try (FileWriter fw = new FileWriter(saveImplFolder + "/" + className + ".java")) {
            fw.write(cu.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void makeRandomBugs(CompilationUnit cu) {
        Random random = new Random();
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

    public static void main(String[] args) throws FileNotFoundException {
        String fileToSaveIn = "src/main/java/org/example/impl";
        generate(1000, "src/main/java/org/example/Engine.java", fileToSaveIn);
    }
}
