package org.example.generator.buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        BlockStmt body = m.getBody().orElseThrow();

        assertTrue(body.getStatements().isEmpty(), "Expected method body to be empty");
    }

    @Test
    void testBugResetNoArraysFill() {
        // given
        BugLibrary.bugResetNoArraysFill(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        // check there's no Array.fill method call
        List<MethodCallExpr> calls = body.findAll(MethodCallExpr.class);
        boolean hasArraysFill = calls.stream().anyMatch(call -> call.getScope().isPresent() &&
                call.getScope().get().toString().equals("Arrays") &&
                call.getNameAsString().equals("fill"));
        assertFalse(hasArraysFill, "Expected no Arrays.fill() calls in body");

        // check that there's still assign of Player.X to turn
        List<AssignExpr> assignments = body.findAll(AssignExpr.class);
        boolean hasTurnAssignment = assignments.stream().anyMatch(assign ->
                assign.getTarget().toString().equals("turn") &&
                        assign.getValue().toString().equals("Player.X"));
        assertTrue(hasTurnAssignment, "Expected assignment 'turn = Player.X;' to remain");
    }

    @Test
    void testBugResetCellX() {
        // given
        BugLibrary.bugResetCellX(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        assertArgChanged(body, "Cell.X");
    }

    @Test
    void testBugResetCellY() {
        // given
        BugLibrary.bugResetCellO(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        assertArgChanged(body, "Cell.O");
    }

    @Test
    void testBugResetTurnToO() {
        // given — apply the mutation
        BugLibrary.bugResetTurnToO(m);

        // when — extract method body from AST
        BlockStmt body = m.getBody().orElseThrow();

        // then — perform structural AST checks

        // ensure the method body is not empty
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        // collect all assignment expressions in the method
        List<AssignExpr> assignments = body.findAll(AssignExpr.class);

        // check that there is an assignment setting turn = Player.O
        boolean hasTurnAssignmentToO = assignments.stream().anyMatch(assign ->
                assign.getTarget().toString().equals("turn") &&
                        assign.getValue().toString().equals("Player.O"));

        // verify that the mutation correctly changed the target assignment
        assertTrue(hasTurnAssignmentToO, "Expected assignment 'turn = Player.O;' in method body");
    }

    @Test
    void testBugResetResultToDraw() {
        // given
        BugLibrary.bugResetResultToDraw(m);

        // then
        assertHasAssignment(m, "Result.DRAW");
    }

    @Test
    void testBugResetResultToXWins() {
        // given
        BugLibrary.bugResetResultToXWins(m);

        // then
        assertHasAssignment(m, "Result.X_WINS");
    }

    @Test
    void testBugResetResultToOWins() {
        // given
        BugLibrary.bugResetResultToOWins(m);

        // then
        assertHasAssignment(m, "Result.O_WINS");
    }

    private static void assertHasAssignment(MethodDeclaration m, String expectedValue) {
        // when — extract body
        BlockStmt body = m.getBody().orElseThrow();

        // ensure the body is not empty
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        // collect all assignment expressions
        List<AssignExpr> assignments = body.findAll(AssignExpr.class);

        // check that one matches the given pattern
        boolean hasExpectedAssignment = assignments.stream().anyMatch(assign ->
                assign.getTarget().toString().equals("result") &&
                        assign.getValue().toString().equals(expectedValue));

        // assert the expected assignment exists
        assertTrue(hasExpectedAssignment, "Expected assignment 'result' = " + expectedValue +
                ";' in method body");
    }

    private void assertArgChanged(BlockStmt body, String s) {
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        // check there's no Array.fill method call
        List<MethodCallExpr> calls = body.findAll(MethodCallExpr.class);
        boolean hasArraysFillChangedSecondParam = calls.stream().anyMatch(call ->
                call.getScope().isPresent() &&
                        call.getScope().get().toString().equals("Arrays") &&
                        call.getNameAsString().equals("fill") &&
                        call.getArguments().get(1).toString().equals(s));
        assertTrue(hasArraysFillChangedSecondParam, "Expected Arrays.fill() method call to have " + s +
                " as second argument");
    }
}
