package game_tests;

import org.example.app_logic.api.GameEngine;
import org.example.test_runner.GameEngineFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineTest {
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        int index = Integer.parseInt(System.getProperty("engine.index", "0"));
        engine = GameEngineFactory.create(index);
    }
}
