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

class ResetMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                     public void reset() {
                         Arrays.fill(board, Cell.EMPTY);
                         turn = Player.X;
                         result = Result.ONGOING;
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
    void testBugResetEmpty() {
        BugLibrary.bugResetEmpty(m);

        String result = m.toString();
        assertTrue(result.contains("public void reset() {"));
        assertFalse(result.contains("Arrays.fill(board, Cell.EMPTY);"));
    }

    @Test
    void testBugResetNoArraysFill() {
        BugLibrary.bugResetNoArraysFill(m);

        String result = m.toString();
        assertTrue(result.contains("public void reset() {"));
        assertFalse(result.contains("Arrays.fill(board, Cell.EMPTY);"));
        assertTrue(result.contains("turn = Player.X;"));
    }

    @Test
    void testBugResetCellX() {
        BugLibrary.bugResetCellX(m);

        String result = m.toString();
        assertTrue(result.contains("Arrays.fill(board, Cell.X);"));
    }

    @Test
    void testBugResetCellY() {
        BugLibrary.bugResetCellY(m);

        String result = m.toString();
        assertTrue(result.contains("Arrays.fill(board, Cell.Y);"));
    }

    @Test
    void testBugResetTurnToO() {
        BugLibrary.bugResetTurnToO(m);

        String result = m.toString();
        assertTrue(result.contains("turn = Player.O;"));
    }

    @Test
    void testBugResetResultToDraw() {
        BugLibrary.bugResetResultToDraw(m);

        String result = m.toString();
        assertTrue(result.contains("result = Result.DRAW;"));
    }

    @Test
    void testBugResetResultToXWins() {
        BugLibrary.bugResetResultToXWins(m);

        String result = m.toString();
        assertTrue(result.contains("result = Result.X_WINS;"));
    }

    @Test
    void testBugResetResultToYWins() {
        BugLibrary.bugResetResultToYWins(m);

        String result = m.toString();
        assertTrue(result.contains("result = Result.Y_WINS;"));
    }
}
