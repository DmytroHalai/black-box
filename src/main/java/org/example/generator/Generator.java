package org.example.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.example.generator.BugRegistry.*;


public class Generator {

    private static final Random random = new Random();

    public static void generate(int num, String enginePath, String saveImplFolder) throws FileNotFoundException {
        File folder = new File(saveImplFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int correctIndex = random.nextInt(num);

        for (int i = 0; i < num; i++) {
            String className = "Engine" + i;
            boolean isCorrect = (i == correctIndex);

            doImplementation(enginePath, saveImplFolder, className, isCorrect);
        }
    }

    private static void doImplementation(String enginePath, String saveImplFolder, String className, boolean isCorrect) throws FileNotFoundException {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(new File(enginePath))
                .getResult().orElseThrow();

        cu.setPackageDeclaration(saveImplFolder.substring(saveImplFolder.indexOf("java") + 5).replace("/", "."));
        cu.findAll(ConstructorDeclaration.class)
                .forEach(constructorDeclaration -> constructorDeclaration.setName(className));
        cu.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.setName(className));
        if (!isCorrect) {
            makeRandomBugs(cu);
            addCommentTo(cu, "that's not me");
        }
        if (isCorrect) addCommentTo(cu, "that's me");

        try (FileWriter fw = new FileWriter(saveImplFolder + "/" + className + ".java")) {
            fw.write(cu.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCommentTo(CompilationUnit cu, String commentText) {
        List<Node> nodes = cu.findAll(Node.class);
        int index = random.nextInt(nodes.size());
        Node node = nodes.get(index);
        node.setLineComment(commentText);
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

    public static void main(String[] args) throws FileNotFoundException {
        String fileToSaveIn = "src/main/java/org/example/impl";
        generate(10000, "src/main/java/org/example/Engine.java", fileToSaveIn);
    }
}
