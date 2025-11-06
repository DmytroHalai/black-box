package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        // given
        BugLibrary.bugValidateMoveEmpty(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        // 1. check that method exists
        assertEquals("validateMove", m.getNameAsString(), "Expected validateMove method");

        // 2. check that the body of the method exists, but doesn't have any ifs or throws
        boolean hasIfStatements = !body.findAll(IfStmt.class).isEmpty();
        boolean hasThrowStatements = !body.findAll(ThrowStmt.class).isEmpty();

        assertFalse(hasIfStatements, "Expected method to contain no 'if' statements");
        assertFalse(hasThrowStatements, "Expected method to contain no 'throw' statements");

        // 3. check that the body is empty
        assertTrue(body.isEmpty(), "Expected validateMove body to be empty");
    }


    @Test
    void testBugValidateMoveIsTerminalInvert() {
        //given
        BugLibrary.bugValidateMoveIsTerminalInvert(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findFirst(IfStmt.class).orElseThrow();
        boolean cond = ifStmt.getCondition().asUnaryExpr().getOperator().equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT);

        assertTrue(cond, "Expected isTerminal() condition to be inverted");
    }

    @Test
    void testBugValidateMoveEqualsTurn() {
        //given
        BugLibrary.bugValidateMoveEqualsTurn(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(1);
        boolean cond = ifStmt.getCondition().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.EQUALS);

        assertTrue(cond, "Expected move.player() == turn condition");
    }

    @Test
    void testBugValidateMoveXMoreThan0() {
        //given
        BugLibrary.bugValidateMoveXMoreThan0(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getLeft().asBinaryExpr().
                getLeft().asBinaryExpr().
                getLeft().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.GREATER);

        assertTrue(cond, "Expected that the move.x() < 0 condition is changed to > 0");
    }

    @Test
    void testBugValidateMoveXLessThan2() {
        //given
        BugLibrary.bugValidateMoveXLessThan2(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getLeft().asBinaryExpr().
                getLeft().asBinaryExpr().
                getRight().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.LESS);

        assertTrue(cond, "Expected that the move.x() > 2 condition is changed to < 2");
    }

    @Test
    void testBugValidateMoveYMoreThan0() {
        //given
        BugLibrary.bugValidateMoveYMoreThan0(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getLeft().asBinaryExpr().
                getRight().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.GREATER);
        assertTrue(cond, "Expected that the move.y() < 0 condition is changed to > 0");
    }

    @Test
    void testBugValidateMoveYLessThan2() {
        //given
        BugLibrary.bugValidateMoveYLessThan2(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getRight().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.LESS);
        assertTrue(cond, "Expected that the move.y() > 2 condition is changed to < 2");
    }

    @Test
    void testBugValidateMoveInvertFirstAnd() {
        //given
        BugLibrary.bugValidateMoveInvertFirstAnd(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr()
                        .getLeft().asBinaryExpr()
                        .getLeft().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.AND);
        assertTrue(cond, "Expected to invert the first OR operator");
    }

    @Test
    void testBugValidateMoveInvertSecondAnd() {
        //given
        BugLibrary.bugValidateMoveInvertSecondAnd(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr()
                .getLeft().asBinaryExpr()
                .getOperator().equals(BinaryExpr.Operator.AND);
        assertTrue(cond, "Expected to invert the second OR operator");
    }

    @Test
    void testBugValidateMoveInvertThirdAnd() {
        //given
        BugLibrary.bugValidateMoveInvertThirdAnd(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(2);
        boolean cond = ifStmt.getCondition().asBinaryExpr()
                .getOperator().equals(BinaryExpr.Operator.AND);
        assertTrue(cond, "Expected to invert the last OR operator");
    }

    @Test
    void testBugValidateMoveBoardEqualsEmpty() {
        //given
        BugLibrary.bugValidateMoveBoardEqualsEmpty(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(3);
        boolean cond = ifStmt.getCondition().asBinaryExpr().getRight().asFieldAccessExpr().getName().toString().equals("EMPTY");
        assertTrue(cond, "Expected board to compare to Cell.EMPTY");
    }

    @Test
    void testBugValidateMoveBoardNotEqualsX() {
        //given
        BugLibrary.bugValidateMoveBoardNotEqualsX(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(3);
        boolean cond = ifStmt.getCondition().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.NOT_EQUALS);
        assertTrue(cond, "Expected board to use NOT_EQUALS operator");
    }

    @Test
    void testBugValidateMoveBoardNotEqualsO() {
        //given
        BugLibrary.bugValidateMoveBoardNotEqualsO(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(3);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getRight().asFieldAccessExpr().getName().toString().equals("O");
        assertTrue(cond, "Expected board to compare to Cell.O");
    }

    @Test
    void testBugValidateMoveIdxWithXX() {
        BugLibrary.bugValidateMoveIdxWithXX(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(3);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getLeft().asArrayAccessExpr().
                getIndex().asMethodCallExpr().getArguments().get(1).asFieldAccessExpr().
                getName().toString().equals("x()");
        System.out.println();
        assertTrue(cond, "Expected idx to use x()");
    }

    @Test
    void testBugValidateMoveIdxWithYY() {
        BugLibrary.bugValidateMoveIdxWithYY(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        IfStmt ifStmt = body.findAll(IfStmt.class).get(3);
        boolean cond = ifStmt.getCondition().asBinaryExpr().
                getLeft().asArrayAccessExpr().
                getIndex().asMethodCallExpr().getArguments().get(0).asFieldAccessExpr().
                getName().toString().equals("y()");
        System.out.println();
        assertTrue(cond, "Expected idx to use y()");
    }
}
