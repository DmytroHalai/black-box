package buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InitBoardMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                      public void initBoard() {
                          board = new Cell[9];
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
    void testBugInitBoardArraySize() {
        //given
        BugLibrary.bugInitBoardArraySize(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(AssignExpr.class).get(0).
                getValue().asArrayCreationExpr().getLevels().
                get(0).getDimension().
                get().toString().equals("3");
        assertTrue(cond, "Expected board to be initialized with size 3");
    }
}
