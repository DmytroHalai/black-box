package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BugLibraryPlayTurnMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                    public void playTurn(Move move) {
                        validateMove(move);
                        int i = idx(move.x(), move.y());
                        board[i] = (turn == Player.X) ? Cell.X : Cell.O;
                
                        if (hasWin()) {
                            result = (turn == Player.X) ? Result.X_WINS : Result.O_WINS;
                        } else if (isBoardFull()) {
                            result = Result.DRAW;
                        } else {
                            turn = turn.other();
                        }
                    }
                }
                """;
        m = parseMethod(code);
    }

    @AfterEach
    void teardown() {
        m = null;
    }

    @Test
    void testBugPlayTurnEmpty() {
        BugLibrary.bugPlayTurnEmpty(m);

        String result = m.toString();
        assertFalse(result.contains("validateMove(move);"));
        assertTrue(result.contains("public void playTurn(Move move) {"));
    }

    @Test
    void testBugPlayTurnRemoveFirstLine() {
        BugLibrary.bugPlayTurnRemoveFirstLine(m);

        String result = m.toString();
        assertTrue(result.contains("public void playTurn(Move move) {"));
        assertFalse(result.contains("validateMove(move);"));
    }

    @Test
    void testBugPlayTurnTurnNotEqualToPlayerX() {
        BugLibrary.bugPlayTurnTurnNotEqualToPlayerX(m);

        String result = m.toString();
        assertTrue(result.contains("board[i] = (turn != Player.X) ? Cell.X : Cell.O;"));
    }

    @Test
    void testBugPlayTurnTurnEqualToPlayerO() {
        BugLibrary.bugPlayTurnTurnEqualToPlayerO(m);

        String result = m.toString();
        assertTrue(result.contains("board[i] = (turn == Player.O) ? Cell.X : Cell.O;"));
    }

    @Test
    void testBugPlayTurnIfTurnEqualsXCellInvert() {
        BugLibrary.bugPlayTurnIfTurnEqualsXCellInvert(m);

        String result = m.toString();
        assertTrue(result.contains("board[i] = (turn == Player.X) ? Cell.O : Cell.X;"));
    }

    @Test
    void testBugPlayTurnInvertHasWins() {
        BugLibrary.bugPlayTurnInvertHasWins(m);

        String result = m.toString();
        assertTrue(result.contains("if (!hasWin()) {"));
    }

    @Test
    void testBugPlayTurnResultInvert() {
        BugLibrary.bugPlayTurnResultInvert(m);

        String result = m.toString();
        assertTrue(result.contains("result = (turn == Player.X) ? Result.O_WINS : Result.X_WINS;"));
    }

    @Test
    void testBugPlayTurnIsBoardFullInvert() {
        BugLibrary.bugPlayTurnIsBoardFullInvert(m);

        String result = m.toString();
        assertTrue(result.contains("} else if (!isBoardFull()) {"));
    }

    @Test
    void testBugPlayTurnInvertResultDrawToOngoing() {
        BugLibrary.bugPlayTurnInvertResultDrawToOngoing(m);

        String result = m.toString();
        assertTrue(result.contains("result = Result.ONGOING;"));
    }

    @Test
    void testBugPlayTurnInvertResultDrawToXWins() {
        BugLibrary.bugPlayTurnInvertResultDrawToXWins(m);

        String result = m.toString();
        assertTrue(result.contains("result = Result.X_WINS;"));
    }

    @Test
    void testBugPlayTurnInvertResultDrawToOWins() {
        BugLibrary.bugPlayTurnInvertResultDrawToOWins(m);

        String result = m.toString();
        assertTrue(result.contains("result = Result.O_WINS;"));
    }

    @Test
    void testBugPlayTurnNoTurnSwitch() {
        BugLibrary.bugPlayTurnNoTurnSwitch(m);

        String result = m.toString();
        assertTrue(result.contains("board[i] = (turn == Player.X) ? Cell.X : Cell.O;"));
        assertFalse(result.contains("turn = turn.other();"));
    }

    @Test
    void testBugPlayTurnTurnToX() {
        BugLibrary.bugPlayTurnToX(m);

        String result = m.toString();
        assertTrue(result.contains("turn = Player.X"));
    }

    @Test
    void testBugPlayTurnTurnToO() {
        BugLibrary.bugPlayTurnToO(m);

        String result = m.toString();
        assertTrue(result.contains("turn = Player.O"));
    }

    @Test
    void testBugPlayTurnIdxWithXX() {
        BugLibrary.bugPlayTurnIdxWithXX(m);

        String result = m.toString();
        assertTrue(result.contains("int i = idx(move.x(), move.x());"));
    }

    @Test
    void testBugPlayTurnIdxWithYY() {
        BugLibrary.bugPlayTurnIdxWithYY(m);

        String result = m.toString();
        assertTrue(result.contains("int i = idx(move.y(), move.y());"));
    }



}
