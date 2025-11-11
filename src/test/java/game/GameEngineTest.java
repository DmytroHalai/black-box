package game;

import org.example.logic.api.*;
import org.example.runner.GameEngineFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {
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

    @Test
    void resetTestCheckArrayFill() {
        engine.reset();
        GameEngine.Cell[] isBoardFilledByEmpty = engine.getBoard();
        Object[] result = Arrays.stream(isBoardFilledByEmpty).toArray();
        boolean allEmpty = true;
        for (Object o : result) {
            if (!o.equals(GameEngine.Cell.EMPTY)) {
                allEmpty = false;
                break;
            }
        }

        assertTrue(allEmpty, "Board is filled by EMPTY cells after reset");
    }

    @Test
    void resetTestTurnAssignX() {
        engine.reset();
        assertSame(Player.X, engine.getTurn(), "Turn is assigned to X after reset");
    }

    @Test
    void resetTestResultAssignOngoing() {
        engine.reset();
        assertSame(Result.ONGOING, engine.getResult(), "Result is assigned to ONGOING after reset");
    }

    @Test
    void initBoardTest() {
        engine.initBoard();
        GameEngine.Cell[] board = engine.getBoard();
        assertEquals(9, board.length, "Board size is either 3 or 9");
    }

    @Test
    void playTurnTestValidMove() {
        Move move = new Move(0, 0, Player.X);
        assertDoesNotThrow(() -> engine.playTurn(move), "Valid move should not throw exception");
    }

    @Test
    void playTurnTestInvalidMoveOccupiedCell() {
        Move move1 = new Move(0, 0, Player.X);
        Move move2 = new Move(0, 0, Player.O);
        engine.playTurn(move1);
        assertThrows(IllegalMoveException.class, () -> engine.playTurn(move2), "Playing on an occupied cell should throw IllegalMoveException");
    }

    @Test
    void isTerminalTest() {
        engine.reset();
        assertFalse(engine.isTerminal(), "Game should not be terminal after reset");
    }

    @Test
    void getWinnerTestNoWinner() {
        engine.reset();
        assertTrue(engine.getWinner().isEmpty(), "There should be no winner after reset");
    }

    @Test
    void isBoardFullTest() {
        engine.reset();
        assertFalse(engine.isBoardFull(), "Board should not be full after reset");
    }

    @Test
    void hasWinTest() {
        engine.reset();
        assertFalse(engine.hasWin(), "There should be no win after reset");
    }

    @Test
    void setLinesTest() {
        int[][] testLines = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}
        };
        engine.setLines();
        assertArrayEquals(testLines, engine.getLines(), "Lines should be set correctly");
    }

    @Test
    void threeInRowTest() {
        engine.reset();
        engine.setLines();
        engine.playTurn(new Move(0, 0, Player.X));
        engine.playTurn(new Move(1, 0, Player.O));
        engine.playTurn(new Move(0, 1, Player.X));
        engine.playTurn(new Move(1, 1, Player.O));
        engine.playTurn(new Move(0, 2, Player.X)); // X wins

        assertTrue(engine.hasWin(), "There should be a win for Player X");
    }

    @Test
    void threeInRowTestFalse() {
        engine.reset();
        engine.setLines();
        engine.playTurn(new Move(0, 0, Player.X));
        engine.playTurn(new Move(1, 0, Player.O));
        engine.playTurn(new Move(0, 1, Player.X));
        engine.playTurn(new Move(1, 1, Player.O));
        engine.playTurn(new Move(2, 2, Player.X)); // No win

        assertFalse(engine.hasWin(), "There should be no win");
    }

    @Test
    void getStateTest() {
        engine.reset();
        BoardView state = engine.getState();
        assertNotNull(state, "Board state should not be null after reset");
    }

    @Test
    void playTurnTestDraw() {
        engine.reset();
        engine.playTurn(new Move(0, 0, Player.X));
        engine.playTurn(new Move(0, 1, Player.O));
        engine.playTurn(new Move(0, 2, Player.X));
        engine.playTurn(new Move(1, 1, Player.O));
        engine.playTurn(new Move(1, 0, Player.X));
        engine.playTurn(new Move(1, 2, Player.O));
        engine.playTurn(new Move(2, 1, Player.X));
        engine.playTurn(new Move(2, 0, Player.O));
        engine.playTurn(new Move(2, 2, Player.X)); // Last move

        assertSame(Result.DRAW, engine.getResult(), "Board should be full");
    }

    @Test
    void validateMoveOTest() {
        engine.reset();
        Move invalidMove = new Move(0, 22, Player.X);
        assertThrows(IllegalMoveException.class, () -> engine.validateMove(invalidMove), "Invalid move should throw IllegalMoveException");
    }

    @Test
    void validateMoveXTest() {
        engine.reset();
        Move invalidMove = new Move(22, 0, Player.X);
        assertThrows(IllegalMoveException.class, () -> engine.validateMove(invalidMove), "Invalid move should throw IllegalMoveException");
    }

    @Test
    void getWinner() {
        engine.setResult(Result.X_WINS);
        assertEquals(Optional.of(Player.X), engine.getWinner(), "Winner should be Player X");
        engine.setResult(Result.O_WINS);
        assertEquals(Optional.of(Player.O), engine.getWinner(), "Winner should be Player O");
    }

    @Test
    void playTurnBoardAssignmentTest() {
        engine.setTurn(Player.X);
        engine.playTurn(new Move(0, 0, Player.X));
        assertEquals(GameEngine.Cell.X, engine.getBoard()[View.idx(0, 0)], "Cell (0,0) should be assigned to X");
        engine.setTurn(Player.O);
        engine.playTurn(new Move(1, 1, Player.O));
        assertEquals(GameEngine.Cell.O, engine.getBoard()[View.idx(1, 1)], "Cell (1,1) should be assigned to O");
    }

    @Test
    void testWinningMoveSetsCorrectResult() {
        engine.playTurn(new Move(0, 0, Player.X));
        engine.playTurn(new Move(1, 0, Player.O));
        engine.playTurn(new Move(0, 1, Player.X));
        engine.playTurn(new Move(1, 1, Player.O));

        // when
        engine.playTurn(new Move(0, 2, Player.X));

        // then
        assertEquals(Result.X_WINS, engine.getResult(),
                "Очікувався результат X_WINS після виграшного ходу X");
    }
}