package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IsBoardFullMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                     public boolean isBoardFull() {
                         for (Cell c : board) if (c == Cell.EMPTY) return false;
                         return true;
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
    void testBugIsBoardFullAlwaysTrue() {
        //given
        BugLibrary.bugIsBoardFullAlwaysTrue(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean isAlwaysTrue = body.findAll(ReturnStmt.class).getFirst().getExpression().get().toString().equals("true");
        assertTrue(isAlwaysTrue, "Expected isBoardFull to always return true");
    }

    @Test
    void testBugIsBoardFullAlwaysFalse() {
        //given
        BugLibrary.bugIsBoardFullAlwaysFalse(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean isAlwaysTrue = body.findAll(ReturnStmt.class).getFirst().getExpression().get().toString().equals("false");
        assertTrue(isAlwaysTrue, "Expected isBoardFull to always return true");
    }

    @Test
    void tesBugIsBoardFullCNotEqualsEmpty() {
        //given
        BugLibrary.bugIsBoardFullCNotEqualsEmpty(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(IfStmt.class).getFirst().getCondition().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.NOT_EQUALS);
        assertTrue(cond, "Expected condition to use '!=' operator");
    }

    @Test
    void tesBugIsBoardFullCEqualsX() {
        //given
        BugLibrary.bugIsBoardFullCEqualsX(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(IfStmt.class).
                getFirst().getCondition().asBinaryExpr().
                getRight().asFieldAccessExpr().
                getName().toString().equals("X");
        assertTrue(cond, "Expected condition to use '!=' operator");
    }

    @Test
    void tesBugIsBoardFullCEqualsY() {
        //given
        BugLibrary.bugIsBoardFullCEqualsY(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(IfStmt.class).
                getFirst().getCondition().asBinaryExpr().
                getRight().asFieldAccessExpr().
                getName().toString().equals("Y");
        assertTrue(cond, "Expected condition to use '!=' operator");
    }
}
