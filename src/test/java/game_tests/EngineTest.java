package game_tests;

import org.example.app_logic.api.GameEngine;
import org.example.test_runner.GameEngineFactory;
import org.junit.jupiter.api.BeforeEach;

class EngineTest {
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        int index = Integer.parseInt(System.getProperty("engine.index", "0"));
        engine = GameEngineFactory.create(index);
    }
}
