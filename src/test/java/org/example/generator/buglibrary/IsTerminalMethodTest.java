package org.example.generator.buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IsTerminalMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                   public boolean isTerminal() {
                        return result != Result.ONGOING;
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
    void testBugIsTerminalAlwaysTrue() {
        //given
        BugLibrary.bugIsTerminalAlwaysTrue(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ReturnStmt.class).
                get(0).getExpression().
                get().toString().equals("true");
        assertTrue(cond, "Expected that the return will be always true");
    }

    @Test
    void testBugIsTerminalAlwaysFalse() {
        //given
        BugLibrary.bugIsTerminalAlwaysFalse(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ReturnStmt.class).
                get(0).getExpression().
                get().toString().equals("false");
        assertTrue(cond, "Expected that the return will be always false");
    }

    @Test
    void testBugIsTerminalResultEqualsOngoing() {
        //given
        BugLibrary.bugIsTerminalResultEqualsOngoing(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ReturnStmt.class).
                get(0).
                getExpression().
                get().asBinaryExpr().
                getOperator().equals(BinaryExpr.Operator.EQUALS);
        assertTrue(cond, "Expected that the return will use '==' operator");
    }

    @Test
    void testBugIsTerminalResultNotEqualsXWins() {
        //given
        BugLibrary.bugIsTerminalResultNotEqualsXWins(m);

        //then
        assertIfReturnsNotEqualsTo(m, "X_WINS");
    }

    @Test
    void testBugIsTerminalResultNotEqualsYWins() {
        //given
        BugLibrary.bugIsTerminalResultNotEqualsOWins(m);

        //then
        assertIfReturnsNotEqualsTo(m, "O_WINS");
    }

    @Test
    void testBugIsTerminalResultNotEqualsDraw() {
        //given
        BugLibrary.bugIsTerminalResultNotEqualsDraw(m);

        //then
        assertIfReturnsNotEqualsTo(m, "DRAW");
    }


    private void assertIfReturnsNotEqualsTo(MethodDeclaration m, String s) {
        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ReturnStmt.class).
                get(0).
                getExpression().
                get().asBinaryExpr().
                getRight().asFieldAccessExpr().
                getName().toString().equals(s);
        assertTrue(cond, "Expected that the return will use '!=' operator with " + s);
    }
}
