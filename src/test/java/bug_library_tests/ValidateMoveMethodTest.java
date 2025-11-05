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

class ValidateMoveMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                     public void validateMove(Move move) {
                         if (isTerminal()) throw new IllegalMoveException("Game is over");
                         if (move.player() != turn) throw new IllegalMoveException("Wrong turn: " + move.player());
                         if (move.x() < 0 || move.x() > 2 || move.y() < 0 || move.y() > 2)
                             throw new IllegalMoveException("Out of board");
                         if (board[idx(move.x(), move.y())] != Cell.EMPTY) throw new IllegalMoveException("Cell occupied");
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
    void testBugValidateMoveAlwaysValid() {
        BugLibrary.bugValidateMoveEmpty(m);

        String result = m.toString();
        assertTrue(result.contains("public void validateMove(Move move) {"));
        assertFalse(result.contains("if (isTerminal())"));
    }

    @Test
    void testBugValidateMoveIsTerminalInvert() {
        BugLibrary.bugValidateMoveIsTerminalInvert(m);

        String result = m.toString();
        assertTrue(result.contains("if (!isTerminal())"));
    }

    @Test
    void testBugValidateMoveEqualsTurn() {
        BugLibrary.bugValidateMoveEqualsTurn(m);

        String result = m.toString();
        assertTrue(result.contains("move.player() == turn"));
    }

    @Test
    void testBugValidateMoveXMoreThan0() {
        BugLibrary.bugValidateMoveXMoreThan0(m);

        String result = m.toString();
        assertTrue(result.contains("move.x() > 0"));
    }

    @Test
    void testBugValidateMoveXLessThan2() {
        BugLibrary.bugValidateMoveXLessThan2(m);

        String result = m.toString();
        assertTrue(result.contains("move.x() < 2"));
    }

    @Test
    void testBugValidateMoveYMoreThan0() {
        BugLibrary.bugValidateMoveYMoreThan0(m);

        String result = m.toString();
        assertTrue(result.contains("move.y() > 0"));
    }

    @Test
    void testBugValidateMoveYLessThan2() {
        BugLibrary.bugValidateMoveYLessThan2(m);

        String result = m.toString();
        assertTrue(result.contains("move.y() < 2"));
    }

    @Test
    void testBugValidateMoveInvertFirstAnd() {
        BugLibrary.bugValidateMoveInvertFirstAnd(m);

        String result = m.toString();
        assertTrue(result.contains("if (move.x() < 0 && move.x() > 2 || move.y() < 0 || move.y() > 2)"));
    }

    @Test
    void testBugValidateMoveInvertSecondAnd() {
        BugLibrary.bugValidateMoveInvertSecondAnd(m);

        String result = m.toString();
        assertTrue(result.contains("if (move.x() < 0 || move.x() > 2 && move.y() < 0 || move.y() > 2)"));
    }

    @Test
    void testBugValidateMoveInvertThirdAnd() {
        BugLibrary.bugValidateMoveInvertThirdAnd(m);

        String result = m.toString();
        assertTrue(result.contains("if (move.x() < 0 || move.x() > 2 || move.y() < 0 && move.y() > 2)"));
    }

    @Test
    void testBugValidateMoveBoardEqualsEmpty() {
        BugLibrary.bugValidateMoveBoardEqualsEmpty(m);

        String result = m.toString();
        assertTrue(result.contains("if (board[idx(move.x(), move.y())] == Cell.EMPTY)"));
    }

    @Test
    void testBugValidateMoveBoardNotEqualsX() {
        BugLibrary.bugValidateMoveBoardNotEqualsX(m);

        String result = m.toString();
        assertTrue(result.contains("if (board[idx(move.x(), move.y())] != Cell.X)"));
    }

    @Test
    void testBugValidateMoveBoardNotEqualsY() {
        BugLibrary.bugValidateMoveBoardNotEqualsY(m);

        String result = m.toString();
        assertTrue(result.contains("if (board[idx(move.x(), move.y())] != Cell.Y)"));
    }

    @Test
    void testBugValidateMoveIdxWithXX() {
        BugLibrary.bugValidateMoveIdxWithXX(m);

        String result = m.toString();
        assertTrue(result.contains("if (board[idx(move.x(), move.x())] != Cell.EMPTY)"));
    }

    @Test
    void testBugValidateMoveIdxWithYY() {
        BugLibrary.bugValidateMoveIdxWithYY(m);

        String result = m.toString();
        assertTrue(result.contains("if (board[idx(move.y(), move.y())] != Cell.EMPTY)"));
    }
}
