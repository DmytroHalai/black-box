package buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetLinesMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                   public void setLines() {
                        lines = new int[][]{
                                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                                {0, 4, 8}, {2, 4, 6}
                        };
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
    void testBugSetLinesRemoveFirstDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveFirstDiagonal(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveSecondDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveSecondDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveThirdDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveThirdDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveForthDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveForthDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveFifthDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveFifthDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveSixthDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveSixthDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveSeventhDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveSeventhDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    @Test
    void testBugSetLinesRemoveEighthDiagonal() {
        //given
        BugLibrary.bugSetLinesRemoveEighthDiagonal(m);
        System.out.println(m);

        //then
        // the count of sub arrays is 8 after removing one of them there should be 7
        assertThatArraySizeIs7(m);
    }

    private void assertThatArraySizeIs7(MethodDeclaration m) {
        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        assertEquals(7, getNumberOfSubArrays(body));
    }

    // the count of sub arrays is 8 after removing one of them there should be 7
    private int getNumberOfSubArrays(BlockStmt body) {
        return body.findAll(ArrayInitializerExpr.class).getFirst().getValues().size();
    }
}
