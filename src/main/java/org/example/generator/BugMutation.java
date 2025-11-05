package org.example.generator;

import com.github.javaparser.ast.body.MethodDeclaration;

@FunctionalInterface
public interface BugMutation {
    void apply(MethodDeclaration method);
}
