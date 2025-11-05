package org.example.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;


public class Generator {

    private static void makeAstFromFile() throws FileNotFoundException {
        String filePath = "src/main/java/org/example/Engine.java";
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(new File(filePath))
                .getResult().orElseThrow();

        String[] methodNames = BugRegistry.getAllMethodNames();
        String chosenMethod = methodNames[new Random().nextInt(methodNames.length)];
        int bugIndex = new Random().nextInt(3);

        Optional<BugMutation> bugOpt = BugRegistry.getBug(chosenMethod, bugIndex);
        System.out.println("BugOpt\n" + bugOpt);

        MethodDeclaration m = cu.findFirst(MethodDeclaration.class,
                methodDeclaration -> methodDeclaration.getNameAsString().equals(chosenMethod)).
                orElseThrow();
        System.out.println("M\n"+m);

        bugOpt.get().apply(m);

        String fileName = "Engine10.java";
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(cu.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        makeAstFromFile();
    }
}
