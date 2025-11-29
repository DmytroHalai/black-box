package org.example.runner;

import org.example.logic.api.GameEngine;
import org.reflections.Reflections;

import java.util.Comparator;
import java.util.List;

public class GameEngineFactory {

    private static final List<Class<? extends GameEngine>> IMPLEMENTATIONS;

    static {
        Reflections reflections = new Reflections(
                "org.example.impl"
        );
        IMPLEMENTATIONS = reflections.getSubTypesOf(GameEngine.class).
                stream()
                .sorted(Comparator.comparing(clazz ->
                        Integer.parseInt(clazz.getSimpleName().replaceAll("\\D", ""))))
                .toList();
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
                .toList();
    }
}