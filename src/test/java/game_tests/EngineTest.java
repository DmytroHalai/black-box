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

    /*
     Тут треба реалізувати тести для відбору правильної імплементації. Тест варто ранити за допомогою команди
     mvn clean-compile. Після того, як усі тести пройдять для кожної імплементації, в файлі tests_summary будуть
     записані лише ті імплементації тестів, які пройшли усі тести. Ваша задача - знайти ту єдину, яка є правильною
     шляхом покриття роботи головного "двигуна" гри юніт-тестами.
     */
}
