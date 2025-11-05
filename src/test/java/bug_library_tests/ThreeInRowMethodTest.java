package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreeInRowMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
            class Test {
                public boolean threeInRow(int i, int j, int k) {
                    boolean isNotEmpty = board[i] != Cell.EMPTY;
                    boolean equalIJ = board[i] == board[j];
                    boolean equalJK = board[j] == board[k];
                    return isNotEmpty && equalIJ && equalJK;
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
    void testBugThreeInRowAlwaysTrue() {
        // given
        BugLibrary.bugThreeInRowAlwaysTrue(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all return statements
        List<ReturnStmt> returns = body.findAll(ReturnStmt.class);
        assertFalse(returns.isEmpty(), "Expected at least one return statement");

        // 2. check if there's return true exists
        boolean hasReturnTrue = returns.getFirst().getExpression().get().toString().equals("true");
        assertTrue(hasReturnTrue);
    }


    @Test
    void testBugThreeInRowAlwaysFalse() {
        //given
        BugLibrary.bugThreeInRowAlwaysFalse(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all return statements
        List<ReturnStmt> returns = body.findAll(ReturnStmt.class);
        assertFalse(returns.isEmpty(), "Expected at least one return statement");

        // 2. check if there's return true exists
        boolean hasReturnTrue = returns.getFirst().getExpression().get().toString().equals("false");
        assertTrue(hasReturnTrue);
    }

    @Test
    void testBugThreeInRowIsNotEmptyEquals() {
        //given
        BugLibrary.bugThreeInRowIsNotEmptyEquals(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean hasBuggedCheck = body.findAll(VariableDeclarationExpr.class).stream()
                .anyMatch(v -> v.toString().contains("board[i] == Cell.EMPTY"));

        assertTrue(hasBuggedCheck, "Expected buggy version with '==' instead of '!='");
    }


    @Test
    void testBugThreeInRowIsNotEmptyCellX() {
        BugLibrary.bugThreeInRowIsNotEmptyCellX(m);

        String result = m.toString();
        assertTrue(result.contains("boolean isNotEmpty = board[i] != Cell.X"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellO() {
        BugLibrary.bugThreeInRowIsNotEmptyCellO(m);

        String result = m.toString();
        assertTrue(result.contains("boolean isNotEmpty = board[i] != Cell.O"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowIsNotEmptyNull() {
        BugLibrary.bugThreeInRowIsNotEmptyNull(m);

        String result = m.toString();
        assertTrue(result.contains("boolean isNotEmpty = board[i] != null"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellJ() {
        BugLibrary.bugThreeInRowIsNotEmptyCellJ(m);

        String result = m.toString();
        assertTrue(result.contains("boolean isNotEmpty = board[j] != Cell.EMPTY"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellK() {
        BugLibrary.bugThreeInRowIsNotEmptyCellK(m);

        String result = m.toString();
        assertTrue(result.contains("boolean isNotEmpty = board[k] != Cell.EMPTY"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualIJtoNotEqual() {
        BugLibrary.bugThreeInRowEqualIJtoNotEqual(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalIJ = board[i] != board[j]"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualIJtoIK() {
        BugLibrary.bugThreeInRowEqualIJtoIK(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalIJ = board[i] == board[k]"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualIJtoII() {
        BugLibrary.bugThreeInRowEqualIJtoII(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalIJ = board[i] == board[i]"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualIJtoJJ() {
        BugLibrary.bugThreeInRowEqualIJtoJJ(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalIJ = board[j] == board[j]"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualJKtoNotEqual() {
        BugLibrary.bugThreeInRowEqualJKtoNotEqual(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalJK = board[j] != board[k];"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualJKtoJJ() {
        BugLibrary.bugThreeInRowEqualJKtoJJ(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalJK = board[j] == board[j];"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowEqualJKtoKK() {
        BugLibrary.bugThreeInRowEqualJKtoKK(m);

        String result = m.toString();
        assertTrue(result.contains("boolean equalJK = board[k] == board[k];"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowReturnFirstOR() {
        BugLibrary.bugThreeInRowReturnFirstOR(m);

        String result = m.toString();
        System.out.println(result);
        assertTrue(result.contains("return isNotEmpty || equalIJ && equalJK;"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowReturnSecondOR() {
        BugLibrary.bugThreeInRowReturnSecondOR(m);

        String result = m.toString();
        System.out.println(result);
        assertTrue(result.contains("return isNotEmpty && equalIJ || equalJK;"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowReturnNotFirst() {
        BugLibrary.bugThreeInRowReturnNotFirst(m);

        String result = m.toString();
        assertTrue(result.contains("return !isNotEmpty && equalIJ && equalJK;"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowReturnNotSecond() {
        BugLibrary.bugThreeInRowReturnNotSecond(m);

        String result = m.toString();
        System.out.println(result);
        assertTrue(result.contains("return isNotEmpty && !equalIJ && equalJK;"), "Method works not correctly!!!");
    }

    @Test
    void testBugThreeInRowReturnNotThird() {
        BugLibrary.bugThreeInRowReturnNotThird(m);

        String result = m.toString();
        System.out.println(result);
        assertTrue(result.contains("return isNotEmpty && equalIJ && !equalJK;"), "Method works not correctly!!!");
    }
}
