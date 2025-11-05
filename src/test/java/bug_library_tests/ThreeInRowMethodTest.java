package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        BugLibrary.bugThreeInRowAlwaysTrue(m);

        String result = m.toString();
        assertTrue(result.contains("return true"), "Expected method to return true");
    }

    @Test
    void testBugThreeInRowAlwaysFalse() {
        BugLibrary.bugThreeInRowAlwaysFalse(m);

        String result = m.toString();
        assertTrue(result.contains("return false"), "Expected method to return false");
    }

    @Test
    void testBugThreeInRowIsNotEmptyEquals() {
        BugLibrary.bugThreeInRowIsNotEmptyEquals(m);

        String result = m.toString();
        assertTrue(result.contains("boolean isNotEmpty = board[i] == Cell.EMPTY"), "Method works not correctly!!!");
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
