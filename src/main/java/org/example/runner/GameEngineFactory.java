package org.example.runner;

import org.example.logic.api.GameEngine;
import org.reflections.Reflections;

import java.util.ArrayList;
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

        IMPLEMENTATIONS = new ArrayList<>(impls);
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
}