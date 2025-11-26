package org.example.generator.buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HasWinMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                     public boolean hasWin() {
                         for (int[] line : lines) {
                             if (threeInRow(line[0], line[1], line[2])) return true;
                         }
                         return false;
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
    void testBugHasWinFullInvertThreeInRowAllIndex0() {
        //given
        BugLibrary.bugHasWinFullInvertThreeInRowAllIndex0(m);

        //then
        assertThatAllIndexesAreEqualTo(m, "0");
    }
    @Test
    void testBugHasWinInvertThreeInRowAllIndex1() {
        //given
        BugLibrary.bugHasWinInvertThreeInRowAllIndex1(m);

        //then
        assertThatAllIndexesAreEqualTo(m, "1");
    }

    @Test
    void testBugHasWinInvertThreeInRowAllIndex2() {
        //given
        BugLibrary.bugHasWinInvertThreeInRowAllIndex2(m);

        //then
        assertThatAllIndexesAreEqualTo(m, "2");
    }

    @Test
    void testBugHasWinInvertThreeInRow() {
        //given
        BugLibrary.bugHasWinInvertThreeInRow(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(IfStmt.class).
                get(0).getCondition().asUnaryExpr().
                getOperator().equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        assertTrue(cond, "Expected that threeInRow method call is negated");
    }

    @Test
    void testBugHasWinInvertReturnAfterIf() {
        //given
        BugLibrary.bugHasWinInvertReturnAfterIf(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ReturnStmt.class).
                get(0).getExpression().
                get().toString().equals("false");
        assertTrue(cond, "Expected that threeInRow method call is negated");
    }

    private void assertThatAllIndexesAreEqualTo(MethodDeclaration m, String indexString) {
        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        int i = 0;
        boolean result = false;
        while(i < 3) {
            result = checkIndexEqualTo(body, indexString, i);
            i++;
        }
        assertTrue(result, "Expected that all indexes in threeInRow method call are equal to " + indexString);
    }

    private boolean checkIndexEqualTo(BlockStmt body, String i, int index) {
        return body.findAll(MethodCallExpr.class).
                get(0).getArguments().
                get(index).asArrayAccessExpr().
                getIndex().toString().equals(i);
    }
}
