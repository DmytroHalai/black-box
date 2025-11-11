package buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayTurnMethodTest {
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
        // given
        BugLibrary.bugPlayTurnEmpty(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then — perform structural checks

        // 1. ensure the method still has a body
        assertNotNull(body, "Expected method body to exist");

        // 2. ensure the method still has a valid declaration signature
        assertEquals("playTurn", m.getNameAsString(),
                "Expected method name to remain 'playTurn'");
    }

    @Test
    void testBugPlayTurnRemoveFirstLine() {
        // given
        BugLibrary.bugPlayTurnRemoveFirstLine(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. ensure the method body exists
        assertNotNull(body, "Expected method body to exist");

        // 2. ensure the first statement is no longer 'validateMove(move);'
        List<Statement> statements = body.getStatements();
        assertFalse(statements.isEmpty(), "Expected method body not to be empty");

        Statement firstStatement = statements.get(0);
        boolean isValidateMoveCall = firstStatement.isExpressionStmt()
                && firstStatement.asExpressionStmt().getExpression().isMethodCallExpr()
                && firstStatement.asExpressionStmt().getExpression().asMethodCallExpr()
                .getNameAsString().equals("validateMove");

        assertFalse(isValidateMoveCall, "Expected first line 'validateMove(move);' to be removed");

        // 3. ensure the method declaration name remains correct
        assertEquals("playTurn", m.getNameAsString(),
                "Expected method name to remain 'playTurn'");
    }


    @Test
    void testBugPlayTurnTurnNotEqualToPlayerX() {
        // given
        BugLibrary.bugPlayTurnTurnNotEqualToPlayerX(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        assertNotNull(body, "Expected method body to exist");

        List<AssignExpr> assignments = body.findAll(AssignExpr.class);

        boolean hasConditionalWithTurnNotEqual = assignments.stream().anyMatch(assign -> {
            if (!assign.getTarget().isArrayAccessExpr()) return false; // if not array left
            ArrayAccessExpr array = assign.getTarget().asArrayAccessExpr();
            if (!array.getName().toString().equals("board")) return false; // if not board[]

            if (!assign.getValue().isConditionalExpr()) return false;
            ConditionalExpr cond = assign.getValue().asConditionalExpr(); // if value not condition

            Expression condition = cond.getCondition();
            if (condition.isEnclosedExpr()) {
                condition = condition.asEnclosedExpr().getInner();
            } // if in ( )
            if (!condition.isBinaryExpr()) return false;
            BinaryExpr be = condition.asBinaryExpr(); // get the cond exactly

            boolean correctOperator = be.getOperator() == BinaryExpr.Operator.NOT_EQUALS; // if !=
            boolean correctLeft = be.getLeft().isNameExpr()
                    && be.getLeft().asNameExpr().getNameAsString().equals("turn"); // if turn on left side
            boolean correctRight = be.getRight().isFieldAccessExpr()
                    && be.getRight().asFieldAccessExpr().toString().
                    replace(" ", "").equals("Player.X"); // if Player.X on left side

            System.out.println("→ " + be.getLeft() + " " + be.getOperator() + " " + be.getRight());
            return correctOperator && correctLeft && correctRight;
        });

        assertTrue(hasConditionalWithTurnNotEqual,
                "Expected assignment 'board[i] = (turn != Player.X) ? Cell.X : Cell.O;'");
    }


    @Test
    void testBugPlayTurnTurnEqualToPlayerO() {
        // given
        BugLibrary.bugPlayTurnTurnEqualToPlayerO(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        assertNotNull(body, "Expected method body to exist");

        // 1. find all assignments
        List<AssignExpr> assignments = body.findAll(AssignExpr.class);

        // 2. check if there's an assignment needed
        boolean hasTurnEqualsPlayerO = assignments.stream().anyMatch(assign -> {
            if (!assign.getTarget().isArrayAccessExpr()) return false;
            ArrayAccessExpr array = assign.getTarget().asArrayAccessExpr();
            if (!array.getName().toString().equals("board")) return false;

            if (!assign.getValue().isConditionalExpr()) return false;
            ConditionalExpr cond = assign.getValue().asConditionalExpr();
            Expression condition = cond.getCondition();

            if (condition.isEnclosedExpr()) {
                condition = condition.asEnclosedExpr().getInner();
            }
            if (!condition.isBinaryExpr()) return false;
            BinaryExpr be = condition.asBinaryExpr();

            boolean correctOperator = be.getOperator() == BinaryExpr.Operator.EQUALS;
            boolean correctLeft = be.getLeft().isNameExpr() &&
                    be.getLeft().asNameExpr().getNameAsString().equals("turn");
            boolean correctRight = be.getRight().toString().equals("Player.O");

            return correctOperator && correctLeft && correctRight;
        });

        assertTrue(hasTurnEqualsPlayerO,
                "Expected assignment 'board[i] = (turn == Player.O) ? Cell.X : Cell.O;'");
    }



    @Test
    void testBugPlayTurnIfTurnEqualsXCellInvert() {
        // given
        BugLibrary.bugPlayTurnIfTurnEqualsXCellInvert(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. ensure body exists
        assertNotNull(body, "Expected method body to exist");

        // 2. find all conditional (ternary) expressions
        List<ConditionalExpr> conditionals = body.findAll(ConditionalExpr.class);

        // 3. find one where then/else branches are inverted: Cell.O / Cell.X
        boolean hasInvertedCells = conditionals.stream().anyMatch(cond ->
                cond.getThenExpr().toString().equals("Cell.O") &&
                        cond.getElseExpr().toString().equals("Cell.X")
        );

        assertTrue(hasInvertedCells,
                "Expected ternary '(turn == Player.X) ? Cell.O : Cell.X;' after inversion");
    }


    @Test
    void testBugPlayTurnInvertHasWins() {
        // given
        BugLibrary.bugPlayTurnInvertHasWins(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all if statements
        List<IfStmt> ifStatements = body.findAll(IfStmt.class);
        assertFalse(ifStatements.isEmpty(), "Expected at least one if statement in method");

        // 2. check if any has condition like !hasWin()
        boolean hasNegatedHasWin = ifStatements.stream().anyMatch(ifStmt ->
                ifStmt.getCondition().isUnaryExpr() &&
                        ifStmt.getCondition().asUnaryExpr().getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT &&
                        ifStmt.getCondition().asUnaryExpr().getExpression().isMethodCallExpr() &&
                        ifStmt.getCondition().asUnaryExpr().getExpression().asMethodCallExpr()
                                .getNameAsString().equals("hasWin")
        );

        assertTrue(hasNegatedHasWin,
                "Expected if condition to be negated: if (!hasWin()) { ... }");
    }


    @Test
    void testBugPlayTurnResultInvert() {
        // given
        BugLibrary.bugPlayTurnResultInvert(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all conditional (ternary) expressions
        List<ConditionalExpr> conditionals = body.findAll(ConditionalExpr.class);
        assertFalse(conditionals.isEmpty(), "Expected at least one ternary expression in method");

        // 2. check if there exists a ternary expression with inverted branches
        boolean hasInvertedResult = conditionals.stream().anyMatch(cond ->
                cond.getThenExpr().toString().equals("Result.O_WINS") &&
                        cond.getElseExpr().toString().equals("Result.X_WINS")
        );

        assertTrue(hasInvertedResult,
                "Expected ternary with inverted results: (turn == Player.X) ? Result.O_WINS : Result.X_WINS;");
    }


    @Test
    void testBugPlayTurnIsBoardFullInvert() {
        // given
        BugLibrary.bugPlayTurnIsBoardFullInvert(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. collect all if statements
        List<IfStmt> ifStatements = body.findAll(IfStmt.class);
        assertFalse(ifStatements.isEmpty(), "Expected at least one if statement in method");

        // 2. find an else-if with negated isBoardFull()
        boolean hasNegatedIsBoardFull = ifStatements.stream().anyMatch(ifStmt ->
                ifStmt.getElseStmt().isPresent() &&
                        ifStmt.getElseStmt().get().isIfStmt() &&
                        ifStmt.getElseStmt().get().asIfStmt().getCondition().isUnaryExpr() &&
                        ifStmt.getElseStmt().get().asIfStmt().getCondition().asUnaryExpr().getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT &&
                        ifStmt.getElseStmt().get().asIfStmt().getCondition().asUnaryExpr().getExpression().isMethodCallExpr() &&
                        ifStmt.getElseStmt().get().asIfStmt().getCondition().asUnaryExpr().getExpression().asMethodCallExpr()
                                .getNameAsString().equals("isBoardFull")
        );

        assertTrue(hasNegatedIsBoardFull,
                "Expected else-if condition to be negated: else if (!isBoardFull()) { ... }");
    }


    @Test
    void testBugPlayTurnInvertResultDrawToOngoing() {
        // given
        BugLibrary.bugPlayTurnInvertResultDrawToOngoing(m);

        //then
        checkIfInverted(m, "Result.ONGOING");
    }


    @Test
    void testBugPlayTurnInvertResultDrawToXWins() {
        //when
        BugLibrary.bugPlayTurnInvertResultDrawToXWins(m);

        //then
        checkIfInverted(m, "Result.X_WINS");
    }

    @Test
    void testBugPlayTurnInvertResultDrawToOWins() {
        //when
        BugLibrary.bugPlayTurnInvertResultDrawToOWins(m);

        //then
        checkIfInverted(m, "Result.O_WINS");
    }

    @Test
    void testBugPlayTurnNoTurnSwitch() {
        // given
        BugLibrary.bugPlayTurnNoTurnSwitch(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. ensure body exists and is not empty
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        List<AssignExpr> assignments = body.findAll(AssignExpr.class);

        // 3. ensure no statement assigns 'turn = turn.other();'
        boolean hasTurnSwitch = assignments.stream().anyMatch(assign ->
                assign.getTarget().toString().equals("turn") &&
                        assign.getValue().toString().equals("turn.other()")
        );
        assertFalse(hasTurnSwitch,
                "Expected no assignment 'turn = turn.other();' after mutation");
    }


    @Test
    void testBugPlayTurnTurnToX() {
        // given
        BugLibrary.bugPlayTurnToX(m);

        //then
        assertIfTurnedPlayer(m, "Player.X");
    }

    @Test
    void testBugPlayTurnTurnToO() {
        //given
        BugLibrary.bugPlayTurnToO(m);

        //then
        assertIfTurnedPlayer(m, "Player.O");
    }

    @Test
    void testBugPlayTurnIdxWithXX() {
        // given
        BugLibrary.bugPlayTurnIdxWithXX(m);

        //then
        assertIfIdxArgsChanged(m, "move.x()");
    }

    @Test
    void testBugPlayTurnIdxWithYY() {
        //given
        BugLibrary.bugPlayTurnIdxWithYY(m);

        //then
        assertIfIdxArgsChanged(m, "move.y()");
    }

    private void checkIfInverted(MethodDeclaration m, String s) {
        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all assignment expressions
        List<AssignExpr> assigns = body.findAll(AssignExpr.class);
        assertFalse(assigns.isEmpty(), "Expected at least one assignment expression");

        // 2. check for assignment
        boolean hasOngoingAssignment = assigns.stream().anyMatch(assign ->
                assign.getTarget().toString().equals("result") &&
                        assign.getValue().toString().equals(s)
        );

        assertTrue(hasOngoingAssignment,
                "Expected assignment 'result = " + s + ";'");
    }

    private void assertIfTurnedPlayer(MethodDeclaration m, String s) {
        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. ensure the body is not empty
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        // 2. find all assignment expressions
        List<AssignExpr> assignments = body.findAll(AssignExpr.class);
        assertFalse(assignments.isEmpty(), "Expected at least one assignment expression");

        // 3. verify that an assignment 'turn = String s' exists
        boolean hasTurnAssignmentToX = assignments.stream().anyMatch(assign ->
                assign.getTarget().toString().equals("turn") &&
                        assign.getValue().toString().equals(s)
        );

        assertTrue(hasTurnAssignmentToX,
                "Expected assignment 'turn = Player.X' in method body after mutation");
    }

    private void assertIfIdxArgsChanged(MethodDeclaration m, String expectedArg) {
        // when
        BlockStmt body = m.getBody().orElseThrow();

        //then

        // ensure method body is not empty
        assertFalse(body.getStatements().isEmpty(), "Expected method body not to be empty");

        List<MethodCallExpr> calls = body.findAll(MethodCallExpr.class);
        assertFalse(calls.isEmpty(), "Expected at least one call to idx()");

        boolean hasIdxArgsChanged = calls.stream().anyMatch(call ->
                call.getNameAsString().equals("idx") &&
                        call.getArguments().size() == 2 &&
                        call.getArguments().get(0).toString().equals(expectedArg) &&
                        call.getArguments().get(1).toString().equals(expectedArg)
        );

        assertTrue(hasIdxArgsChanged,
                "Expected both idx() arguments to be changed to " + expectedArg);
    }

}
