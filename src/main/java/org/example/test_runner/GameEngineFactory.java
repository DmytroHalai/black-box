package org.example.test_runner;

import org.example.app_logic.api.GameEngine;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameEngineFactory {

    private static final List<Class<? extends GameEngine>> IMPLEMENTATIONS;

    static {
        Reflections reflections = new Reflections(
                "org.example.impl"
        );

        Set<Class<? extends GameEngine>> impls = reflections.getSubTypesOf(GameEngine.class);

        IMPLEMENTATIONS = impls.stream()
                .filter(cls -> !cls.isInterface() && !cls.isAnonymousClass() && !cls.isLocalClass())
                .sorted((a, b) -> {
                    String nameA = a.getSimpleName();
                    String nameB = b.getSimpleName();
                    int numA = extractNumber(nameA);
                    int numB = extractNumber(nameB);
                    return Integer.compare(numA, numB);
                })
                .collect(Collectors.toList());
    }

    public static GameEngine create(int index) {
        try {
            return IMPLEMENTATIONS.get(index).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create engine at index " + index, e);
        }
    }

    public static List<String> getImplementationNames() {
        return IMPLEMENTATIONS.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
    }

    private static int extractNumber(String name) {
        return Integer.parseInt(name.replaceAll("\\D+", ""));
    }
}