package buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TurnMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                   public Player turn() {
                      return turn;
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
    void testBugTurnAlwaysX() {
        //given
        BugLibrary.bugTurnAlwaysX(m);

        //then
        assertTheReturnIs(m, "Player.X");
    }

    @Test
    void testBugTurnAlwaysO() {
        //given
        BugLibrary.bugTurnAlwaysO(m);

        //then
        assertTheReturnIs(m, "Player.O");
    }

    private void assertTheReturnIs(MethodDeclaration m, String s) {
        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ReturnStmt.class).
                get(0).getExpression().
                get().toString().equals(s);
        assertTrue(cond, "Expected that the return will be " + s);
    }
}
